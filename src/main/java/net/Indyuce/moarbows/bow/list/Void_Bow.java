package net.Indyuce.moarbows.bow.list;

import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import net.Indyuce.moarbows.util.LinearFormula;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Void_Bow extends MoarBow {
    public Void_Bow() {
        super(new String[]{"Its arrows teleport you", "to where they land."}, new ParticleData(Particle.REDSTONE, Color.fromRGB(128, 0, 128), 2),
                new String[]{"AIR,ENDER_PEARL,AIR", "ENDER_PEARL,BOW,ENDER_PEARL", "AIR,ENDER_PEARL,AIR"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(5, -1, 2, 5)));
    }

    @Override
    public boolean canShoot(EntityShootBowEvent event, ArrowData data) {
        return true;
    }

    @Override
    public void whenHit(EntityDamageByEntityEvent event, ArrowData data, Entity target) {
        whenLand(data);
    }

    @Override
    public void whenLand(ArrowData data) {
        data.getArrow().remove();
        Location loc = data.getArrow().getLocation();
        loc.setPitch(((Player) data.getArrow().getShooter()).getLocation().getPitch());
        loc.setYaw(((Player) data.getArrow().getShooter()).getLocation().getYaw());
        data.getShooter().teleport(loc);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 0);
        data.getArrow().getWorld().playSound(data.getArrow().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 3, 1);
    }
}
