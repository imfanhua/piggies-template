package me.fanhua.template

import me.fanhua.piggies.items.*
import me.fanhua.piggies.tools.items.*
import org.bukkit.Material

object TestItem : CustomItem(PiggiesTemplate, "test") {

	val item = Material.PAPER.item { name("§7测试道具") }.with(this)

	override fun whenClicked(event: CustomItemClickedEvent) {
		if (event.isLeftClick) return
		event.use().used()
		event.player.give(TierItem.values().random().random().item())
	}

}
