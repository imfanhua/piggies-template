package me.fanhua.template

import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import me.fanhua.piggies.coroutines.async
import me.fanhua.piggies.coroutines.launch
import me.fanhua.piggies.coroutines.sync
import me.fanhua.piggies.gui.GUI
import me.fanhua.piggies.gui.new
import me.fanhua.piggies.gui.ui.button
import me.fanhua.piggies.gui.ui.inv
import me.fanhua.piggies.parts.Parts
import me.fanhua.piggies.players.events.PlayerSneakSwapEvent
import me.fanhua.piggies.tools.data.holders.PlayerHold
import me.fanhua.piggies.tools.data.holders.hold
import me.fanhua.piggies.tools.items.item
import me.fanhua.piggies.tools.items.name
import me.fanhua.piggies.tools.plugins.key
import me.fanhua.piggies.tools.plugins.logger
import me.fanhua.piggies.tools.plugins.on
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType

@Serializable
data class TestData(var value: Int = 100)
val TestDataPart = Parts.persistent(PiggiesTemplate.key("test_data"), ::TestData)

object TestGUI : Listener {

	init {
		GUI
		PiggiesTemplate.on(this)
	}

	val logger get() = PiggiesTemplate.logger

	@EventHandler
	fun onPlayerSneakSwapEvent(event: PlayerSneakSwapEvent) {
		menu(event.player.hold).open(event.player)
		event.use()
	}

	fun menu(target: PlayerHold) = GUI.PLAYER.new("§7> §0Test") {
		inv(target) { _, slot ->
			target.orNull?.inventory?.setItem(slot, null)
			logger.info("Removed slot: $slot")
		}

		button(6, 4, Material.BOOK.item { name("§a测试") }) {
			PiggiesTemplate.launch(PiggiesTemplate.async) {
				println("Running: ${Thread.currentThread()}")
				delay(2000)
				println("Done async: ${Thread.currentThread()}")
				PiggiesTemplate.sync {
					println("Sync: ${Thread.currentThread()}")
					Bukkit.getOnlinePlayers().randomOrNull()?.let {
						it.gameMode = if (it.gameMode == GameMode.SPECTATOR) GameMode.CREATIVE else GameMode.SPECTATOR
					}
					println("Done sync: ${Thread.currentThread()}")
				}
			}
		}

		button(7, 4, Material.STRUCTURE_VOID.item { name("§b测试") }) {
			when (it) {
				ClickType.LEFT -> logger.info("Data: ${TestDataPart[this].value++}")
				ClickType.RIGHT -> logger.info("Data: ${TestDataPart[this].value--}")
				ClickType.DROP -> {
					TestDataPart[this].value = 0
					logger.info("Data: 0")
				}
				else -> {}
			}
		}

		button(8, 4, Material.BARRIER.item { name("§c关闭") }) { closeInventory() }
		onClose { logger.info("Closed by: ${it.name}") }
	}

}
