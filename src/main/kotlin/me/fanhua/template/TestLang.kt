package me.fanhua.template

import me.fanhua.piggies.lang.LanguageSet
import me.fanhua.piggies.lang.Languages
import me.fanhua.piggies.tools.entitiy.ENTITIES
import me.fanhua.piggies.tools.items.ITEMS
import me.fanhua.piggies.tools.misc.BIOMES
import me.fanhua.piggies.tools.misc.isGameplay
import me.fanhua.piggies.tools.plugins.logger
import me.fanhua.piggies.tools.plugins.title
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.advancement.Advancement
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionEffectType

object TestLang {

	init {
		PiggiesTemplate.logger.apply {
			title("Advancements")
			test(Bukkit.advancementIterator().asSequence().filter(Advancement::isGameplay).toList(), Languages.Advancements)
			title("Items")
			test(ITEMS, Languages.Items)
			title("Entities")
			test(ENTITIES, Languages.Entities)
			title("Enchantments")
			test(Enchantment.values().toList(), Languages.Enchantments)
			title("PotionEffects")
			test(PotionEffectType.values().toList(), Languages.PotionEffects)
			title("Biomes")
			test(BIOMES, Languages.Biomes)
			title("Attributes")
			test(Attribute.values().toList(), Languages.Attributes)
		}
	}

	private fun <T: Keyed> test(values: Collection<T>, set: LanguageSet<T>) {
		PiggiesTemplate.logger.apply {
			for (value in values) {
				val name = set[value]
				if (name == null) warning("!${value.key}: $value")
				else info(" $name")
			}
		}
	}

}