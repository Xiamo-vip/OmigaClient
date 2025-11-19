package com.mo.gui.screen

import com.mo.gui.widght.Button
import com.mo.utils.FontUtils
import com.mo.utils.RenderUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.*
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.sound.MusicSound
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList

class TitleScreen : Screen(Text.of("TitleScreen")) {
    private val title = "Omiga Client"
    private val titleSize = 40

    private val buttonWidth = 180
    private val buttonHeight = titleSize - 20

    private val spacing = RenderUtil.getWindowsHeight()/4 - FontUtils.getStringHeight(title,titleSize)/2

    var y = (spacing + RenderUtil.getWindowsHeight() * 0.4).toInt()

    private val buttons = CopyOnWriteArrayList<Button>()

    init {
        registerButton()
        positionButtons()
    }

    override fun children(): List<Element?>? {
        return super.children()
    }

    override fun getTitle(): Text? {
        return super.getTitle()
    }

    override fun getNarratedTitle(): Text? {
        return super.getNarratedTitle()
    }


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        buttons.forEach { it.onMouse(mouseX,mouseY) }
        super.mouseMoved(mouseX, mouseY)
    }



    fun registerButton(){

        buttons.add(
            Button((RenderUtil.getWindowsWidth()/2-buttonWidth/2),
                y = y+(buttonHeight+10)*buttons.count(),
                buttonWidth,
                buttonHeight,
                "Single Play",
                { MinecraftClient.getInstance().setScreen(SelectWorldScreen(this)) }
            )
        )

        buttons.add(
            Button((RenderUtil.getWindowsWidth()/2-buttonWidth/2),
                y = y+(buttonHeight+10)*buttons.count(),
                buttonWidth,
                buttonHeight,
                "Multi Play",
                { MinecraftClient.getInstance().setScreen(MultiplayerScreen(this)) }
            )
        )

        buttons.add(
            Button((RenderUtil.getWindowsWidth()/2-buttonWidth/2),
                y = y+(buttonHeight+10)*buttons.count(),
                buttonWidth,
                buttonHeight,
                "Exit",
                { MinecraftClient.getInstance().close() }
            )
        )
    }


    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        buttons.forEach { it.mouseReleased(mouseX,mouseY) }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun render(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {

        Identifier.of("omiga","/background.png")?.let {
            RenderUtil.drawImage(context,
                it,
                0f,
                0f,
                RenderUtil.getWindowsWidth().toFloat(),
                RenderUtil.getWindowsHeight().toFloat()

            )
        }


        RenderUtil.drawStringWithShadow(
            context,
            title,
            RenderUtil.getWindowsWidth()/2 - FontUtils.getStringWidth(title,titleSize)/2,
            spacing,
            Color.WHITE.rgb,
            titleSize
            )

        val y = RenderUtil.getWindowsHeight()/4 - FontUtils.getStringHeight(title,titleSize)/2 + buttonHeight+30

        buttons.forEach { it.render(context,mouseX,mouseY,delta) }






    }

    private fun positionButtons() {


        buttons.forEachIndexed{index, button ->
            button.x = (RenderUtil.getWindowsWidth()/2-buttonWidth/2)
            button.y = y+(buttonHeight+10)*index
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun setInitialFocus(element: Element?) {
        super.setInitialFocus(element)
    }

    override fun blur() {
        super.blur()
    }

    override fun switchFocus(path: GuiNavigationPath?) {
        super.switchFocus(path)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return super.shouldCloseOnEsc()
    }

    override fun close() {
        super.close()
    }

    override fun <T> addDrawableChild(drawableElement: T?): T? where T : Element?, T : Drawable?, T : Selectable? {
        return super.addDrawableChild(drawableElement)
    }

    override fun <T : Drawable?> addDrawable(drawable: T?): T? {
        return super.addDrawable(drawable)
    }

    override fun <T> addSelectableChild(child: T?): T? where T : Element?, T : Selectable? {
        return super.addSelectableChild(child)
    }

    override fun remove(child: Element?) {
        super.remove(child)
    }

    override fun clearChildren() {
        super.clearChildren()
    }

    override fun insertText(text: String?, override: Boolean) {
        super.insertText(text, override)
    }

    override fun handleTextClick(style: Style?): Boolean {
        return super.handleTextClick(style)
    }

    override fun init() {
        super.init()
    }

    override fun clearAndInit() {
        super.clearAndInit()
    }

    override fun tick() {
        super.tick()
    }

    override fun removed() {
        super.removed()
    }

    override fun onDisplayed() {
        super.onDisplayed()
    }

    override fun renderBackground(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.renderBackground(context, mouseX, mouseY, delta)
    }

    override fun renderInGameBackground(context: DrawContext?) {
        super.renderInGameBackground(context)
    }

    override fun renderBackgroundTexture(context: DrawContext?) {
        super.renderBackgroundTexture(context)
    }

    override fun shouldPause(): Boolean {
        return super.shouldPause()
    }

    override fun initTabNavigation() {
        super.initTabNavigation()
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        positionButtons()
        super.resize(client, width, height)
    }

    override fun isValidCharacterForName(
        name: String?,
        character: Char,
        cursorPos: Int
    ): Boolean {
        return super.isValidCharacterForName(name, character, cursorPos)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return super.isMouseOver(mouseX, mouseY)
    }

    override fun filesDragged(paths: List<Path?>?) {
        super.filesDragged(paths)
    }

    override fun applyMouseMoveNarratorDelay() {
        super.applyMouseMoveNarratorDelay()
    }

    override fun applyMousePressScrollNarratorDelay() {
        super.applyMousePressScrollNarratorDelay()
    }

    override fun applyKeyPressNarratorDelay() {
        super.applyKeyPressNarratorDelay()
    }

    override fun updateNarrator() {
        super.updateNarrator()
    }

    override fun narrateScreenIfNarrationEnabled(onlyChangedNarrations: Boolean) {
        super.narrateScreenIfNarrationEnabled(onlyChangedNarrations)
    }

    override fun hasUsageText(): Boolean {
        return super.hasUsageText()
    }

    override fun addScreenNarrations(messageBuilder: NarrationMessageBuilder?) {
        super.addScreenNarrations(messageBuilder)
    }

    override fun addElementNarrations(builder: NarrationMessageBuilder?) {
        super.addElementNarrations(builder)
    }

    override fun getUsageNarrationText(): Text? {
        return super.getUsageNarrationText()
    }

    override fun applyNarratorModeChangeDelay() {
        super.applyNarratorModeChangeDelay()
    }

    override fun setTooltip(tooltip: List<OrderedText?>?) {
        super.setTooltip(tooltip)
    }

    override fun setTooltip(
        tooltip: List<OrderedText?>?,
        positioner: TooltipPositioner?,
        focused: Boolean
    ) {
        super.setTooltip(tooltip, positioner, focused)
    }

    override fun setTooltip(tooltip: Text?) {
        super.setTooltip(tooltip)
    }

    override fun setTooltip(
        tooltip: Tooltip?,
        positioner: TooltipPositioner?,
        focused: Boolean
    ) {
        super.setTooltip(tooltip, positioner, focused)
    }

    override fun getNavigationFocus(): ScreenRect? {
        return super.getNavigationFocus()
    }

    override fun getMusic(): MusicSound? {
        return super.getMusic()
    }
}