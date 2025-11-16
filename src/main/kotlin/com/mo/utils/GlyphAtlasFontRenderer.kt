package com.mo.utils

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

class GlyphAtlasFontRenderer(
    private val baseFont: Font,
    private val fallbackFonts: List<String> = listOf(
        "Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Noto Emoji"
    ),
    atlasSize: Int = 2048,
    private val antialias: Boolean = true
) {

    // --- data classes ---
    private data class GlyphKey(val codePoint: Int, val size: Int)
    private data class Glyph(
        val u0: Float, val v0: Float, val u1: Float, val v1: Float,
        val width: Int, val height: Int,
        val xAdvance: Int,
        val xOffset: Int,
        val yOffset: Int
    )

    // --- Atlas ---
    private class Atlas(val size: Int) {
        val img = NativeImage(NativeImage.Format.RGBA, size, size, true)
        val texture = NativeImageBackedTexture(img)
        val id: Identifier = Identifier("font", "atlas/${UUID.randomUUID()}")

        var cursorX = 0
        var cursorY = 0
        var rowHeight = 0

        init {
            MinecraftClient.getInstance().textureManager.registerTexture(id, texture)
        }

        fun alloc(w: Int, h: Int): Pair<Int, Int>? {
            if (w > size || h > size) return null
            if (cursorX + w > size) {
                cursorX = 0
                cursorY += rowHeight
                rowHeight = 0
            }
            if (cursorY + h > size) return null

            val pos = cursorX to cursorY
            cursorX += w
            rowHeight = max(rowHeight, h)
            return pos
        }
    }

    private val atlas = Atlas(atlasSize)
    private val glyphCache = HashMap<GlyphKey, Glyph>()

    // --- PUBLIC API ---

    fun drawString(context: DrawContext, str: String, x: Int, y: Int, color: Int, size: Int) {
        if (str.isEmpty()) return

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram)
        RenderSystem.setShaderTexture(0, atlas.id)

        val matrix = context.matrices.peek().positionMatrix
        val tess = Tessellator.getInstance()
        val buf = tess.buffer

        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)

        var penX = x
        val (ascent, _) = getMetrics(size)
        val penY = y + ascent

        val a = (color ushr 24) and 255
        val r = (color ushr 16) and 255
        val g = (color ushr 8) and 255
        val b = color and 255

        for (cp in str.codePoints()) {
            val glyph = loadGlyph(cp, size)
            if (glyph == null) {
                penX += size / 2
                continue
            }

            val x0 = (penX + glyph.xOffset).toFloat()
            val y0 = (penY - glyph.yOffset).toFloat()
            val x1 = x0 + glyph.width
            val y1 = y0 + glyph.height

            buf.vertex(matrix, x0, y1, 0f).texture(glyph.u0, glyph.v1).color(r, g, b, a).next()
            buf.vertex(matrix, x1, y1, 0f).texture(glyph.u1, glyph.v1).color(r, g, b, a).next()
            buf.vertex(matrix, x1, y0, 0f).texture(glyph.u1, glyph.v0).color(r, g, b, a).next()
            buf.vertex(matrix, x0, y0, 0f).texture(glyph.u0, glyph.v0).color(r, g, b, a).next()

            penX += glyph.xAdvance
        }

        BufferRenderer.drawWithGlobalProgram(buf.end())
        RenderSystem.disableBlend()
    }

    fun getStringWidth(text: String, size: Int): Int {
        var w = 0
        for (cp in text.codePoints()) {
            val g = loadGlyph(cp, size)
            w += g?.xAdvance ?: (size / 2)
        }
        return w
    }

    fun getStringHeight(size: Int): Int = getMetrics(size).second


    // --- internal functions ---
    private fun loadGlyph(cp: Int, size: Int): Glyph? {
        val key = GlyphKey(cp, size)
        glyphCache[key]?.let { return it }

        // choose font
        val font = chooseFont(cp, size)
        val frc = createFRC(size)

        val gv = font.createGlyphVector(frc, String(Character.toChars(cp)))
        val gm = gv.getGlyphMetrics(0)

        // real width = advanceX (render width)
        val advance = ceil(gm.advanceX.toDouble()).toInt()

        val bounds = gv.getGlyphPixelBounds(0, frc, 0f, 0f)

        // if bounds.width == 0 → use advance
        val drawW = max(1, bounds.width.takeIf { it > 0 } ?: advance)
        val drawH = max(1, bounds.height)

        val pos = atlas.alloc(drawW, drawH) ?: return null

        val image = BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB)
        val g2 = image.createGraphics()
        configureHints(g2, size)
        g2.color = Color.WHITE
        g2.font = font
        g2.drawGlyphVector(gv, (-bounds.x).toFloat(), (-bounds.y).toFloat())
        g2.dispose()

        val pixel = IntArray(drawW * drawH)
        image.getRGB(0, 0, drawW, drawH, pixel, 0, drawW)

        var idx = 0
        for (iy in 0 until drawH)
            for (ix in 0 until drawW) {
                atlas.img.setColor(pos.first + ix, pos.second + iy, pixel[idx++])
            }

        atlas.texture.upload()

        val glyph = Glyph(
            pos.first / atlas.size.toFloat(),
            pos.second / atlas.size.toFloat(),
            (pos.first + drawW) / atlas.size.toFloat(),
            (pos.second + drawH) / atlas.size.toFloat(),
            drawW,
            drawH,
            advance,
            bounds.x,
            -bounds.y
        )

        glyphCache[key] = glyph
        return glyph
    }

    private val metrics = HashMap<Int, Pair<Int, Int>>()

    private fun getMetrics(size: Int): Pair<Int, Int> =
        metrics.getOrPut(size) {
            val frc = createFRC(size)
            val gv = baseFont.deriveFont(size.toFloat()).createGlyphVector(frc, "Hg")
            val b = gv.getPixelBounds(frc,0f,0f)
            val ascent = -b.y
            b.height to ascent
        }

    private fun createFRC(size: Int): FontRenderContext {
        return FontRenderContext(
            null,
            antialias,
            size > 14
        )
    }

    private fun chooseFont(cp: Int, size: Int): Font {
        if (baseFont.canDisplay(cp)) return baseFont.deriveFont(size.toFloat())
        for (ff in fallbackFonts) {
            try {
                val f = Font(ff, Font.PLAIN, size)
                if (f.canDisplay(cp)) return f
            } catch (_: Throwable) {}
        }
        return baseFont.deriveFont(size.toFloat())
    }

    private fun configureHints(g: Graphics2D, size: Int) {
        if (antialias) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        }
    }
}
