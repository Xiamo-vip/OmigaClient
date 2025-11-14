package com.mo.utils


import com.mo.Omiga
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import java.awt.Graphics2D
import java.awt.Color
import java.awt.AlphaComposite
import java.util.LinkedHashMap
import java.util.UUID
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import kotlin.math.ceil

open class CustomFontRenderer(
    baseFont: Font,
    private val antialias: Boolean = true,
    private val fractionalMetrics: Boolean = true,
    private val maxCacheEntries: Int = 256
) {
    private val font: Font = baseFont
    private val emojiFallbackFamilies = listOf(
        "Segoe UI Emoji", "Segoe UI Symbol",
        "Apple Color Emoji",
        "Noto Color Emoji", "Noto Emoji"
    )

    // Registered external emoji providers (e.g., Twemoji atlas)
    interface EmojiProvider {
        fun getEmojiImage(codePoint: Int, size: Int): BufferedImage?
    }
    private val emojiProviders = mutableListOf<EmojiProvider>()
    fun registerEmojiProvider(provider: EmojiProvider) { emojiProviders.add(provider) }

    data class DrawStyle(
        val outlineWidth: Int = 0,
        val outlineColor: Int = 0xFF000000.toInt(),
        val shadowDx: Int = 0,
        val shadowDy: Int = 0,
        val shadowColor: Int = 0x80000000.toInt()
    )

    private data class CacheKey(
        val text: String,
        val size: Int,
        val color: Int,
        val style: DrawStyle
    )

    private data class CacheEntry(
        val id: Identifier,
        val texture: NativeImageBackedTexture,
        val width: Int,
        val height: Int
    )

    private val cache = object : LinkedHashMap<CacheKey, CacheEntry>(128, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey, CacheEntry>?): Boolean {
            if (size > maxCacheEntries) {
                eldest?.value?.let { destroyTexture(it) }
                return true
            }
            return false
        }
    }

    fun clearCache() {
        val iterator = cache.values.iterator()
        while (iterator.hasNext()) {
            destroyTexture(iterator.next())
            iterator.remove()
        }
    }

    fun drawString(
        drawContext: DrawContext,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        size: Int = 18,
        style: DrawStyle = DrawStyle()
    ) {
        if (text.isEmpty()) return
        val key = CacheKey(text, size, color, style)
        val entry = cache.getOrPut(key) { renderStringToTexture(text, size, color, style) }

        drawContext.drawTexture(
            entry.id,
            x,
            y,
            0f,
            0f,
            entry.width,
            entry.height,
            entry.width,
            entry.height
        )
    }

    // ================== Tessellator + BufferBuilder Batch ==================
    private data class BatchItem(val entry: CacheEntry, val x: Int, val y: Int, val color: Int)
    private var batching = false
    private val batchBuckets = LinkedHashMap<Identifier, MutableList<BatchItem>>()

    fun beginBatch() { batching = true }

    fun drawStringBatched(
        text: String,
        x: Int,
        y: Int,
        color: Int,
        size: Int = 18,
        style: DrawStyle = DrawStyle()
    ) {
        if (!batching) {
            val dc = DrawContext(MinecraftClient.getInstance(), MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers)
            drawString(dc, text, x, y, color, size, style)
            return
        }
        if (text.isEmpty()) return
        val key = CacheKey(text, size, color, style)
        val entry = cache.getOrPut(key) { renderStringToTexture(text, size, color, style) }
        val list = batchBuckets.getOrPut(entry.id) { ArrayList() }
        list.add(BatchItem(entry, x, y, color))
    }

    fun endBatch() {
        if (!batching) return
        batching = false
        if (batchBuckets.isEmpty()) return

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram)

        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        for ((textureId, items) in batchBuckets) {
            RenderSystem.setShaderTexture(0, textureId)
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
            for (item in items) {
                val x0 = item.x.toFloat()
                val y0 = item.y.toFloat()
                val x1 = x0 + item.entry.width
                val y1 = y0 + item.entry.height
                val u0 = 0f
                val v0 = 0f
                val u1 = 1f
                val v1 = 1f
                val a = (item.color ushr 24) and 0xFF
                val r = (item.color ushr 16) and 0xFF
                val g = (item.color ushr 8) and 0xFF
                val b = item.color and 0xFF

                buffer.vertex(x0.toDouble(), y1.toDouble(), 0.0).texture(u0, v1).color(r, g, b, a).next()
                buffer.vertex(x1.toDouble(), y1.toDouble(), 0.0).texture(u1, v1).color(r, g, b, a).next()
                buffer.vertex(x1.toDouble(), y0.toDouble(), 0.0).texture(u1, v0).color(r, g, b, a).next()
                buffer.vertex(x0.toDouble(), y0.toDouble(), 0.0).texture(u0, v0).color(r, g, b, a).next()
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end())
        }

        RenderSystem.disableBlend()
        batchBuckets.clear()
    }
    // ======================================================================

    open fun getStringWidth(text: String, size: Int = 18): Int {
        if (text.isEmpty()) return 0
        val measure = measureRuns(text, size)
        return measure.totalWidth
    }

    open fun getFontHeight(size: Int = 128): Int {
        val measure = measureRuns("Hg", size)
        return measure.totalHeight
    }

    data class TextRun(
        val text: String,
        val font: Font?,
        val width: Int,
        val isEmoji: Boolean,
        val emojiImage: BufferedImage? = null,
        val emojiAscent: Int = 0,
        val emojiHeight: Int = 0
    )
    data class MeasureResult(val runs: List<TextRun>, val ascent: Int, val totalWidth: Int, val totalHeight: Int)

    public fun measureRuns(text: String, size: Int): MeasureResult {
        if (text.isEmpty()) return MeasureResult(emptyList(), 0, 0, 0)
        val img = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val g2d = img.createGraphics()
        configureHints(g2d, size)

        val runs = ArrayList<TextRun>()
        var i = 0
        var totalWidth = 0
        var maxAscent = 0
        var maxHeight = 0

        while (i < text.length) {
            val cp = text.codePointAt(i)
            val charCount = Character.charCount(cp)

            var emojiImg: BufferedImage? = null
            for (provider in emojiProviders) {
                emojiImg = provider.getEmojiImage(cp, size)
                if (emojiImg != null) break
            }
            if (emojiImg != null) {
                val w = emojiImg.width
                val h = emojiImg.height
                runs.add(TextRun(String(Character.toChars(cp)), null, w, true, emojiImg, emojiAscent = (h * 0.85f).toInt(), emojiHeight = h))
                totalWidth += w
                if ((h * 0.85f).toInt() > maxAscent) maxAscent = (h * 0.85f).toInt()
                if (h > maxHeight) maxHeight = h
                i += charCount
                continue
            }

            val chosen = chooseFontFor(cp, size)
            val start = i
            i += charCount
            while (i < text.length) {
                val nextCp = text.codePointAt(i)
                var providedByEmoji = false
                for (provider in emojiProviders) {
                    if (provider.getEmojiImage(nextCp, size) != null) { providedByEmoji = true; break }
                }
                if (providedByEmoji) break
                if (!chosen.canDisplay(nextCp)) break
                i += Character.charCount(nextCp)
            }
            val runText = text.substring(start, i)
            g2d.font = chosen
            val fm = g2d.fontMetrics
            val w = fm.stringWidth(runText)
            runs.add(TextRun(runText, chosen, w, false))
            totalWidth += w
            if (fm.ascent > maxAscent) maxAscent = fm.ascent
            if (fm.height > maxHeight) maxHeight = fm.height
        }
        g2d.dispose()
        return MeasureResult(runs, maxAscent, totalWidth.coerceAtLeast(1), maxHeight.coerceAtLeast(1))
    }

    private fun renderStringToTexture(text: String, size: Int, argbColor: Int, style: DrawStyle): CacheEntry {
        val ssFactor = when {
            size <= 12 -> 3
            size <= 16 -> 2
            else -> 1
        }
        val finalImage = if (ssFactor == 1) {
            renderStringImage(text, size, argbColor, style)
        } else {
            val hiRes = renderStringImage(text, size * ssFactor, argbColor, style, fractionalOverride = false)
            downscaleImage(hiRes, 1.0 / ssFactor)
        }

        val nativeImage = bufferedImageToNativeImage(finalImage)
        val texture = NativeImageBackedTexture(nativeImage)
        texture.setFilter(false, false)

        val id = Identifier("omiga", "font/${UUID.randomUUID()}")
        MinecraftClient.getInstance().textureManager.registerTexture(id, texture)

        return CacheEntry(id, texture, finalImage.width, finalImage.height)
    }

    private fun renderStringImage(text: String, size: Int, argbColor: Int, style: DrawStyle, fractionalOverride: Boolean? = null): BufferedImage {
        val measure = measureRuns(text, size)
        val width = measure.totalWidth
        val height = measure.totalHeight

        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = img.createGraphics()
        configureHints(g2d, size, fractionalOverride)
        g2d.composite = AlphaComposite.Src

        var xCursor = 0
        val baselineY = measure.ascent

        fun drawRun(run: TextRun, color: Int) {
            if (run.isEmoji) {
                val emoji = run.emojiImage ?: return
                val drawY = baselineY - run.emojiHeight
                g2d.drawImage(emoji, xCursor, drawY, null)
            } else {
                g2d.font = run.font
                g2d.color = Color(color, true)
                g2d.drawString(run.text, xCursor, baselineY)
            }
        }

        for (run in measure.runs) {
            if (!run.isEmoji) {
                if (style.shadowDx != 0 || style.shadowDy != 0) {
                    g2d.color = Color(style.shadowColor, true)
                    g2d.font = run.font
                    g2d.drawString(run.text, xCursor + style.shadowDx, baselineY + style.shadowDy)
                }
                if (style.outlineWidth > 0) {
                    val ow = style.outlineWidth
                    g2d.color = Color(style.outlineColor, true)
                    g2d.font = run.font
                    for (ox in -ow..ow) {
                        for (oy in -ow..ow) {
                            if (ox == 0 && oy == 0) continue
                            g2d.drawString(run.text, xCursor + ox, baselineY + oy)
                        }
                    }
                }
            } else {
                if (style.shadowDx != 0 || style.shadowDy != 0) {
                    val emoji = run.emojiImage
                    if (emoji != null) {
                        val drawY = baselineY - run.emojiHeight
                        val shaded = BufferedImage(emoji.width, emoji.height, BufferedImage.TYPE_INT_ARGB)
                        val sg = shaded.createGraphics()
                        sg.composite = AlphaComposite.Src
                        val sc = Color(style.shadowColor, true)
                        val pixels = IntArray(emoji.width * emoji.height)
                        emoji.getRGB(0, 0, emoji.width, emoji.height, pixels, 0, emoji.width)
                        var idx = 0
                        for (py in 0 until emoji.height) {
                            for (px in 0 until emoji.width) {
                                val p = pixels[idx++]
                                val a = p ushr 24 and 0xFF
                                val sa = (sc.alpha * a + 127) / 255
                                val rgba = (sc.red shl 16) or (sc.green shl 8) or sc.blue or (sa shl 24)
                                pixels[idx - 1] = rgba
                            }
                        }
                        shaded.setRGB(0, 0, emoji.width, emoji.height, pixels, 0, emoji.width)
                        sg.dispose()
                        g2d.drawImage(shaded, xCursor + style.shadowDx, drawY + style.shadowDy, null)
                    }
                }
            }
            drawRun(run, argbColor)
            xCursor += run.width
        }
        g2d.dispose()
        return img
    }

    private fun downscaleImage(src: BufferedImage, scale: Double): BufferedImage {
        val targetW = ceil(src.width * scale).toInt().coerceAtLeast(1)
        val targetH = ceil(src.height * scale).toInt().coerceAtLeast(1)
        val dst = BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB)
        val g = dst.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.composite = AlphaComposite.Src
        g.drawImage(src, 0, 0, targetW, targetH, null)
        g.dispose()
        return dst
    }

    private fun configureHints(g2d: Graphics2D, size: Int, fractionalOverride: Boolean? = null) {
        val small = size <= 14
        if (antialias) {
            if (small) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
            } else {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        }
        val useFractional = fractionalOverride ?: (size > 14)
        if (useFractional) {
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF)
        }
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        // Small sizes benefit from stroke normalization (align to pixel grid)
        if (small) {
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        }
    }

    private fun bufferedImageToNativeImage(img: BufferedImage): NativeImage {
        val native = NativeImage(NativeImage.Format.RGBA, img.width, img.height, false)
        val pixels = IntArray(img.width * img.height)
        img.getRGB(0, 0, img.width, img.height, pixels, 0, img.width)
        var index = 0
        for (y in 0 until img.height) {
            for (x in 0 until img.width) {
                val argb = pixels[index++]
                val a = argb ushr 24 and 0xFF
                var r = argb ushr 16 and 0xFF
                var g = argb ushr 8 and 0xFF
                var b = argb and 0xFF
                r = (r * a + 127) / 255
                g = (g * a + 127) / 255
                b = (b * a + 127) / 255
                val rgba = (r shl 24) or (g shl 16) or (b shl 8) or a
                native.setColor(x, y, rgba)
            }
        }
        return native
    }

    private fun chooseFontFor(codePoint: Int, size: Int): Font {
        if (font.canDisplay(codePoint)) return font.deriveFont(size.toFloat())
        for (family in emojiFallbackFamilies) {
            try {
                val f = Font(family, Font.PLAIN, size)
                if (f.canDisplay(codePoint)) return f
            } catch (_: Throwable) {}
        }
        return font.deriveFont(size.toFloat())
    }

    private fun destroyTexture(entry: CacheEntry) {
        try {
            MinecraftClient.getInstance().textureManager.destroyTexture(entry.id)
        } catch (_: Throwable) {
        }
        try {
            entry.texture.close()
        } catch (_: Throwable) {
        }
    }


    companion object {
        fun fromSystemFont(
            fonPath: String = CustomFonts.Jigsaw,
            style: Int = Font.PLAIN,
            antialias: Boolean = true,
            fractionalMetrics: Boolean = true,
            maxCacheEntries: Int = 256
        ): CustomFontRenderer {
            val base = Font.createFont(Font.TRUETYPE_FONT, Omiga.javaClass.getResourceAsStream(fonPath))
            return CustomFontRenderer(base, antialias, fractionalMetrics, maxCacheEntries)
        }
    }
}