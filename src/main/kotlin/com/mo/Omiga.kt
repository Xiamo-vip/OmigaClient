package com.mo

import com.mo.module.ModuleManager
import com.mo.event.EvenManager
import com.mo.utils.RenderUtil
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Omiga : ModInitializer {
    private val logger = LoggerFactory.getLogger("OmigaClient")

	public val clientVerson = "β0.1"

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		ModuleManager
		EvenManager.init()
		RenderUtil


	}
}