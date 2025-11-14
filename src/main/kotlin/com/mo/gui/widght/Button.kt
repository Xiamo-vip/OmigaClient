package com.mo.gui.widght

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.navigation.GuiNavigation
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.function.Consumer

open class Button(x : Int, y : Int,width: Int, height: Int) : ClickableWidget(
    x,
    y,
    width,
    height,
    Text.of("cs")
) {

    override fun getHeight(): Int {
        return super.getHeight()
    }

    override fun setTooltip(tooltip: Tooltip?) {
        super.setTooltip(tooltip)
    }

    override fun getTooltip(): Tooltip? {
        return super.getTooltip()
    }

    override fun setTooltipDelay(delay: Int) {
        super.setTooltipDelay(delay)
    }

    override fun getNarrationMessage(): MutableText? {
        return super.getNarrationMessage()
    }

    override fun renderWidget(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        TODO("Not yet implemented")
    }

    override fun drawScrollableText(
        context: DrawContext?,
        textRenderer: TextRenderer?,
        xMargin: Int,
        color: Int
    ) {
        super.drawScrollableText(context, textRenderer, xMargin, color)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        super.onRelease(mouseX, mouseY)
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun isValidClickButton(button: Int): Boolean {
        return super.isValidClickButton(button)
    }

    override fun mouseDragged(
        mouseX: Double,
        mouseY: Double,
        button: Int,
        deltaX: Double,
        deltaY: Double
    ): Boolean {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun clicked(mouseX: Double, mouseY: Double): Boolean {
        return super.clicked(mouseX, mouseY)
    }

    override fun getNavigationPath(navigation: GuiNavigation?): GuiNavigationPath? {
        return super.getNavigationPath(navigation)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return super.isMouseOver(mouseX, mouseY)
    }

    override fun playDownSound(soundManager: SoundManager?) {
        super.playDownSound(soundManager)
    }

    override fun getWidth(): Int {
        return super.getWidth()
    }

    override fun setWidth(width: Int) {
        super.setWidth(width)
    }

    override fun setHeight(height: Int) {
        super.setHeight(height)
    }

    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)
    }

    override fun setMessage(message: Text?) {
        super.setMessage(message)
    }

    override fun getMessage(): Text? {
        return super.getMessage()
    }

    override fun isFocused(): Boolean {
        return super.isFocused()
    }

    override fun isHovered(): Boolean {
        return super.isHovered()
    }

    override fun isSelected(): Boolean {
        return super.isSelected()
    }

    override fun isNarratable(): Boolean {
        return super.isNarratable()
    }

    override fun setFocused(focused: Boolean) {
        super.setFocused(focused)
    }

    override fun getType(): Selectable.SelectionType? {
        return super.getType()
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        TODO("Not yet implemented")
    }

    override fun appendDefaultNarrations(builder: NarrationMessageBuilder?) {
        super.appendDefaultNarrations(builder)
    }

    override fun getX(): Int {
        return super.getX()
    }

    override fun setX(x: Int) {
        super.setX(x)
    }

    override fun getY(): Int {
        return super.getY()
    }

    override fun setY(y: Int) {
        super.setY(y)
    }

    override fun getRight(): Int {
        return super.getRight()
    }

    override fun getBottom(): Int {
        return super.getBottom()
    }

    override fun forEachChild(consumer: Consumer<ClickableWidget?>?) {
        super.forEachChild(consumer)
    }

    override fun setDimensions(width: Int, height: Int) {
        super.setDimensions(width, height)
    }

    override fun getNavigationFocus(): ScreenRect? {
        return super.getNavigationFocus()
    }

    override fun setDimensionsAndPosition(width: Int, height: Int, x: Int, y: Int) {
        super.setDimensionsAndPosition(width, height, x, y)
    }

    override fun getNavigationOrder(): Int {
        return super.getNavigationOrder()
    }

    override fun setNavigationOrder(navigationOrder: Int) {
        super.setNavigationOrder(navigationOrder)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return super.charTyped(chr, modifiers)
    }

    override fun getFocusedPath(): GuiNavigationPath? {
        return super.getFocusedPath()
    }

    override fun setPosition(x: Int, y: Int) {
        super.setPosition(x, y)
    }
}