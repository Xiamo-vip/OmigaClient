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

class GlyphAtlasFontRenderer(
    private val baseFont: Font,
    private val fallbackFamilies: List<String> = listOf(
        "Segoe UI Emoji", "Segoe UI Symbol", "Apple Color Emoji", "Noto Color Emoji", "Noto Emoji"
    ),
    atlasSize: Int = 2048,
    private val antialias: Boolean = true
) {
    private data class GlyphKey(val codePoint: Int, val size: Int)
    private data class Glyph(
        val u0: Float, val v0: Float, val u1: Float, val v1: Float,
        val w: Int, val h: Int,
        val xAdvance: Int,
        val xOffset: Int,
        val yOffset: Int
    )

    private class Atlas(val size: Int) {
        val image = NativeImage(NativeImage.Format.RGBA, size, size, true)
        val texture = NativeImageBackedTexture(image)
        val id: Identifier = Identifier("omiga", "font/atlas/${UUID.randomUUID()}")
        var cursorX = 0
        var cursorY = 0
        var rowH = 0
        fun hasSpace(w: Int, h: Int): Boolean {
            if (w > size || h > size) return false
            if (cursorX + w > size) {
                cursorX = 0
                cursorY += rowH
                rowH = 0
            }
            return cursorY + h <= size
        }
        fun alloc(w: Int, h: Int): Pair<Int, Int>? {
            if (!hasSpace(w, h)) return null
            if (cursorX + w > size) {
                cursorX = 0
                cursorY += rowH
                rowH = 0
            }
            val x = cursorX
            val y = cursorY
            cursorX += w
            rowH = maxOf(rowH, h)
            return x to y
        }
    }

    private val atlas = Atlas(atlasSize)
    private val glyphs = HashMap<GlyphKey, Glyph>()
    private val metricsCache = HashMap<Int, Pair<Int, Int>>() // size -> (ascent, height)

    init {
        MinecraftClient.getInstance().textureManager.registerTexture(atlas.id, atlas.texture)
        atlas.texture.setFilter(false, false)
    }

    fun clear() {
        glyphs.clear()
        metricsCache.clear()
        for (y in 0 until atlas.size) for (x in 0 until atlas.size) atlas.image.setColor(x, y, 0)
        atlas.texture.upload()
        atlas.cursorX = 0; atlas.cursorY = 0; atlas.rowH = 0
    }

    fun getFontHeight(text:String,size: Int): Int = getMetrics(text,size).second

    fun drawString(drawContext: DrawContext, text: String, x: Int, y: Int, argb: Int, size: Int) {
        if (text.isEmpty()) return
        val ascent = getMetrics(text,size).first
        val tess = Tessellator.getInstance()
        val buf = tess.buffer
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram)
        RenderSystem.setShaderTexture(0, atlas.id)

        val matrix = drawContext.matrices.peek().positionMatrix
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        var penX = x
        val penY = y + ascent
        val a = (argb ushr 24) and 0xFF
        val r = (argb ushr 16) and 0xFF
        val gC = (argb ushr 8) and 0xFF
        val b = argb and 0xFF
        for (cp in text.codePoints().toArray()) {
            val g = getGlyph(cp, size) ?: continue
            val x0 = (penX + g.xOffset).toFloat()
            val y0 = (penY - g.yOffset).toFloat()
            val x1 = x0 + g.w
            val y1 = y0 + g.h

            buf.vertex(matrix, x0, y1, 0f).texture(g.u0, g.v1).color(r, gC, b, a).next()
            buf.vertex(matrix, x1, y1, 0f).texture(g.u1, g.v1).color(r, gC, b, a).next()
            buf.vertex(matrix, x1, y0, 0f).texture(g.u1, g.v0).color(r, gC, b, a).next()
            buf.vertex(matrix, x0, y0, 0f).texture(g.u0, g.v0).color(r, gC, b, a).next()

            penX += g.xAdvance
        }
        BufferRenderer.drawWithGlobalProgram(buf.end())
        RenderSystem.disableBlend()
    }

    fun getStringWidth(text: String, size: Int): Int {
        if (text.isEmpty()) return 0
        var width = 0
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            val font = chooseFont(cp, size)
            val start = i
            i += Character.charCount(cp)
            while (i < text.length) {
                val ncp = text.codePointAt(i)
                val nf = chooseFont(ncp, size)
                if (nf.family != font.family) break
                i += Character.charCount(ncp)
            }
            val run = text.substring(start, i)
            val frc = createFRC(size)
            val gv: GlyphVector = font.createGlyphVector(frc, run.toCharArray())
            val positions = FloatArray(gv.numGlyphs * 2)
            gv.getGlyphPositions(0, gv.numGlyphs, positions)
            val adv = (positions[positions.size - 2] - positions[0]).toDouble()
            width += ceil(adv).toInt()
        }
        return width
    }

    private fun getGlyph(codePoint: Int, size: Int): Glyph? {
        val key = GlyphKey(codePoint, size)
        glyphs[key]?.let { return it }

        val font = chooseFont(codePoint, size)
        val frc = createFRC(size)
        val gv = font.createGlyphVector(frc, String(Character.toChars(codePoint)))
        val bounds = gv.getGlyphPixelBounds(0, frc, 0f, 0f)
        val gm = gv.getGlyphMetrics(0)

        val w = maxOf(1, bounds.width)
        val h = maxOf(1, bounds.height)
        val xOffset = bounds.x
        val yOffset = -bounds.y // distance from baseline to top-left y
        val xAdvance = ceil(gm.advanceX.toDouble()).toInt()

        val pos = atlas.alloc(w, h) ?: return null

        val img = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = img.createGraphics()
        configureHints(g2, size)
        g2.composite = AlphaComposite.Src
        g2.font = font
        g2.color = Color(0xFFFFFFFF.toInt(), true)
        g2.drawGlyphVector(gv, (-bounds.x).toFloat(), (-bounds.y).toFloat())
        g2.dispose()

        val pixels = IntArray(w * h)
        img.getRGB(0, 0, w, h, pixels, 0, w)
        var index = 0
        for (py in 0 until h) {
            for (px in 0 until w) {
                val argb = pixels[index++]
                val a2 = (argb ushr 24) and 0xFF
                var rr = (argb ushr 16) and 0xFF
                var gg = (argb ushr 8) and 0xFF
                var bb = argb and 0xFF
                rr = (rr * a2 + 127) / 255
                gg = (gg * a2 + 127) / 255
                bb = (bb * a2 + 127) / 255
                val rgba = (rr shl 24) or (gg shl 16) or (bb shl 8) or a2
                atlas.image.setColor(pos.first + px, pos.second + py, rgba)
            }
        }
        atlas.texture.upload()

        val u0 = (pos.first).toFloat() / atlas.size
        val v0 = (pos.second).toFloat() / atlas.size
        val u1 = (pos.first + w).toFloat() / atlas.size
        val v1 = (pos.second + h).toFloat() / atlas.size
        val glyph = Glyph(u0, v0, u1, v1, w, h, xAdvance, xOffset, yOffset)
        glyphs[key] = glyph
        return glyph
    }

    private fun getMetrics(text:String,size: Int): Pair<Int, Int> {
        metricsCache.getOrPut(size) {
            val frc = createFRC(size)
            val gv = baseFont.deriveFont(size.toFloat()).createGlyphVector(frc, text)
            val bounds = gv.pixelBounds
            val ascent = -bounds.y
            val height = bounds.height
            ascent to height
        }

        if (metricsCache.getValue(size).second == 0){
            val frc = createFRC(size)
            val gv = baseFont.deriveFont(size.toFloat()).createGlyphVector(frc, text)
            val bounds = gv.pixelBounds
            val ascent = -bounds.y
            val height = bounds.height
            metricsCache.put(size, Pair(ascent,height))

        }
        return metricsCache.getValue(size)
    }

    private val GlyphVector.pixelBounds: Rectangle
        get() = getGlyphPixelBounds(0, fontRenderContext, 0f, 0f)

    private fun createFRC(size: Int): FontRenderContext {
        val aaHint = if (antialias) if (size <= 14) RenderingHints.VALUE_TEXT_ANTIALIAS_GASP else RenderingHints.VALUE_TEXT_ANTIALIAS_ON else RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        val fm = size > 14
        return FontRenderContext(null, aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_ON || aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP, fm)
    }

    private fun configureHints(g2d: Graphics2D, size: Int) {
        if (antialias) {
            if (size <= 14) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
            } else {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        }
        val useFractional = size > 14
        if (useFractional) {
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF)
        }
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    }

    private fun chooseFont(codePoint: Int, size: Int): Font {
        if (baseFont.canDisplay(codePoint)) return baseFont.deriveFont(size.toFloat())
        for (family in fallbackFamilies) {
            try {
                val f = Font(family, Font.PLAIN, size)
                if (f.canDisplay(codePoint)) return f
            } catch (_: Throwable) {}
        }
        return baseFont.deriveFont(size.toFloat())
    }
}
