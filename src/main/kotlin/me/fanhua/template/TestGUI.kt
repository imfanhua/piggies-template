package me.fanhua.template

import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import me.fanhua.piggies.coroutines.async
import me.fanhua.piggies.coroutines.launch
import me.fanhua.piggies.coroutines.sync
import me.fanhua.piggies.gui.GUI
import me.fanhua.piggies.gui.new
import me.fanhua.piggies.gui.ui.*
import me.fanhua.piggies.parts.Parts
import me.fanhua.piggies.players.events.PlayerSneakSwapEvent
import me.fanhua.piggies.plugins.events.use
import me.fanhua.piggies.tools.data.holders.PlayerHold
import me.fanhua.piggies.tools.data.holders.hold
import me.fanhua.piggies.tools.items.*
import me.fanhua.piggies.tools.plugins.keyed
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
val TestDataPart = Parts.persistent(PiggiesTemplate.keyed("test_data"), ::TestData)

object TestGUI : Listener {

	private val ICON_NONE = Material.RED_BANNER.item { name("§c测试") }
	private val ICON_OK = Material.GREEN_BANNER.item { name("§a测试") }

	init {
		GUI
		PiggiesTemplate.on(this)
	}

	val logger get() = PiggiesTemplate.logger

	@EventHandler
	fun onPlayerSneakSwapEvent(event: PlayerSneakSwapEvent)
		= event.use().let { menu(event.player.hold).open(event.player) }

	fun menu(target: PlayerHold) = GUI.MAX.new("§7> §0Test") {
		inv(target) { _, _, slot ->
			target.orNull?.inventory?.setItem(slot, null)
			logger.info("Removed slot: $slot")
		}

		slot(5, 4, Material.PAPER.item { name("§e槽位测试") }).drop(this)
		tag(6, 4, Material.PAPER.item { name("§e模板测试 TAG") })
		tag(7, 4, Material.PAPER.item { name("§e模板测试 O1") }, UISlot.Tag.ONLY_ONE)
		tag(8, 4, Material.PAPER.item { name("§e模板测试 OT") }, UISlot.Tag.ONLY_TYPE)
		tag(0, 5, Material.PAPER.item { name("§e模板测试 OT1") }, UISlot.Tag.ONLY_ONE_TYPE)

		var ok = false
		val btn = button(5, 5, ICON_NONE) { _, clicker, click ->
			if (click.isRightClick) clicker.give(TestItem.item.clone())
			else if (ok) {
				clicker.closeInventory()
				GUI.syncOf(clicker).open(clicker)
			}
		}

		selection(1, 5) {
			for (i in 0 until 3) h(Material.BAMBOO.item { name("§2选择") }.suffix("§c${i + 1}"), i == 1)
		}.onChanged { _, i ->
			ok = i == 2
			btn.icon = if (ok) ICON_OK else ICON_NONE
		}

		switch(4, 5, Material.EMERALD.item { name("§d开关测试") })

		button(6, 5, Material.BOOK.item { name("§a测试") }) { _, clicker, click ->
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

		button(7, 5, target.get.skullOf { name("§b测试") }) { _, clicker, click ->
			when (click) {
				ActionType.LEFT -> logger.info("Data: ${TestDataPart[clicker].value++}")
				ActionType.RIGHT -> logger.info("Data: ${TestDataPart[clicker].value--}")
				ActionType.DROP -> {
					TestDataPart[clicker].value = 0
					logger.info("Data: 0")
				}
				else -> {}
			}
		}

		button(8, 5, Material.BARRIER.item { name("§c关闭") }) { _, clicker, _ -> clicker.closeInventory() }
		onClose { logger.info("Closed by: ${it.name}") }
	}

}
