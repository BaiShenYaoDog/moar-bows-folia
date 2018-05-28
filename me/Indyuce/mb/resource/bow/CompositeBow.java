package me.Indyuce.mb.resource.bow;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.Indyuce.mb.ConfigData;
import me.Indyuce.mb.Eff;
import me.Indyuce.mb.Main;
import me.Indyuce.mb.resource.SpecialBow;
import me.Indyuce.mb.resource.TaskState;
import me.Indyuce.mb.util.Utils;
import me.Indyuce.mb.util.VersionUtils;

public class CompositeBow implements SpecialBow {
	@Override
	public TaskState shoot(EntityShootBowEvent e, Arrow a, Player p, ItemStack i) {
		FileConfiguration config = ConfigData.getCD(Main.plugin, "", "bows");
		final double dmg = config.getDouble("COMPOSITE_BOW.damage");
		e.setCancelled(true);
		if (!Utils.consumeAmmo(p, new ItemStack(Material.ARROW)))
			return TaskState.BREAK;

		VersionUtils.sound(p.getLocation(), "ENTITY_ARROW_SHOOT", 2, 0);
		new BukkitRunnable() {
			Location loc = p.getEyeLocation();
			double ti = 0;
			double max = 20 * e.getForce();
			Vector v = p.getEyeLocation().getDirection().multiply(1.25);

			public void run() {
				for (double j = 0; j < 3; j++) {
					ti += .5;
					loc.add(v);
					Eff.CRIT.display(.1f, .1f, .1f, .1f, 8, loc, 100);
					VersionUtils.sound(loc, "BLOCK_NOTE_HAT", 3, 2);
					for (LivingEntity t : loc.getWorld().getEntitiesByClass(LivingEntity.class))
						if (Utils.canDmgEntity(p, loc, t) && t != p) {
							VersionUtils.sound(t.getLocation(), "ENTITY_FIREWORK_BLAST", 3, 0);
							Eff.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, t.getLocation().add(0, 1, 0), 100);
							cancel();
							t.damage(dmg);
							return;
						}
				}
				if (ti >= max)
					cancel();
			}
		}.runTaskTimer(Main.plugin, 0, 1);
		return TaskState.BREAK;
	}

	@Override
	public TaskState hit(EntityDamageByEntityEvent e, Arrow a, Entity p, Player t, Object... obj) {
		return TaskState.CONTINUE;
	}

	@Override
	public TaskState land(Arrow a) {
		return TaskState.CONTINUE;
	}
}
