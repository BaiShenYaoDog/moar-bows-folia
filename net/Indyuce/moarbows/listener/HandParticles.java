package net.Indyuce.moarbows.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.moarbows.Eff;
import net.Indyuce.moarbows.MoarBows;
import net.Indyuce.moarbows.api.MoarBow;
import net.Indyuce.moarbows.util.Utils;

public class HandParticles {
	private static int parsPerSec = 1;

	public static void initialize() {
		parsPerSec = MoarBows.plugin.getConfig().getBoolean("reduce-hand-particles") ? 8 : 1;

		if (MoarBows.plugin.getConfig().getBoolean("bows-hand-particles")) {
			new BukkitRunnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers())
						loopHandParticles(p);
				}
			}.runTaskTimer(MoarBows.plugin, 0, (MoarBows.plugin.getConfig().getBoolean("reduce-hand-particles") ? 20 : 2));
		}
	}

	public static void loopHandParticles(Player p) {
		ItemStack[] items = Utils.getHandItems(p);
		for (int j = 0; j < items.length; j++) {
			ItemStack i = items[j];
			if (!Utils.isPluginItem(i, false))
				continue;

			MoarBow b = MoarBow.get(i);
			if (b == null)
				continue;

			String eff = b.getParticleEffect();
			if (eff == null || eff.equals(""))
				continue;

			Location loc = p.getLocation().clone().add(0, .8, 0);
			loc.setYaw(p.getLocation().getYaw());
			loc.add(loc.getDirection().multiply(.2));
			loc.setPitch(0);
			loc.setYaw(p.getLocation().getYaw() + 90 - (j * 180));
			loc.add(loc.getDirection().multiply(.3));
			String[] s = eff.split(":");
			Eff effName = null;
			try {
				effName = Eff.valueOf(s[0].toUpperCase());
			} catch (Exception e) {
				continue;
			}
			if (s.length == 1)
				effName.display(.1f, .1f, .1f, 0, parsPerSec, loc, 200);
			if (s.length == 2)
				for (int j1 = 0; j1 < parsPerSec; j1++) {
					loc.add(new Random().nextDouble() / 2.5 - .2, new Random().nextDouble() / 2.5 - .2, new Random().nextDouble() / 2.5 - .2);
					String[] rgb = s[1].split(",");
					Color c = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
					effName.display(new Eff.OrdinaryColor(c), loc, 200);
				}
		}
	}
}
