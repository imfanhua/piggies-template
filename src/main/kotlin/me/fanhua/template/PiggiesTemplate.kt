package me.fanhua.template

import me.fanhua.piggies.PiggyPlugin
import me.fanhua.piggies.tools.plugins.logger

object PiggiesTemplate: PiggyPlugin() {

	internal class IPlugin : Plugin(this)

	override fun load() {
		TestItem
		TestGUI
		logger.info("Loaded!")
	}

}
