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

public class BlazeBow implements SpecialBow {
	@Override
	public TaskState shoot(EntityShootBowEvent e, Arrow a, Player p, ItemStack i) {
		e.setCancelled(true);
		FileConfiguration config = ConfigData.getCD(Main.plugin, "", "bows");
		final double dmg = config.getDouble("BLAZE_BOW.damage");
		final double duration = config.getDouble("BLAZE_BOW.duration");
		if (!Utils.consumeAmmo(p, new ItemStack(Material.ARROW)))
			return TaskState.BREAK;

		new BukkitRunnable() {
			Location loc = p.getEyeLocation();
			double ti = 0;
			Vector v = p.getEyeLocation().getDirection().multiply(1.25);

			public void run() {
				for (double j = 0; j < 3; j++) {
					ti += .5;
					loc.add(v);
					Eff.FLAME.display(.1f, .1f, .1f, 0, 8, loc, 100);
					Eff.SMOKE_NORMAL.display(0, 0, 0, 0, 1, loc, 150);
					VersionUtils.sound(loc, "BLOCK_NOTE_HAT", 3, 2);
					for (LivingEntity t : loc.getWorld().getEntitiesByClass(LivingEntity.class))
						if (Utils.canDmgEntity(p, loc, t) && t != p) {
							new BukkitRunnable() {
								final Location loc2 = t.getLocation();
								double y = 0;

								public void run() {
									for (int i = 0; i < 2; i++) {
										y += .05;
										for (int j = 0; j < 2; j++) {
											double xz = y * Math.PI * .8 + (j * Math.PI);
											Eff.FLAME.display(0, 0, 0, 0, 1, loc2.clone().add(Math.cos(xz) * 1.3, y, Math.sin(xz) * 1.3), 150);
										}
									}
									if (y >= 2.5)
										cancel();
								}
							}.runTaskTimer(Main.plugin, 0, 1);
							VersionUtils.sound(t.getLocation(), "ENTITY_FIREWORK_BLAST", 3, 0);
							Eff.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, t.getLocation().add(0, 1, 0), 100);
							cancel();
							t.damage(dmg);
							t.setFireTicks((int) (duration * 20));
							return;
						}
				}
				if (ti >= 20 * e.getForce())
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
