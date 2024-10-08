package net.Indyuce.moarbows.bow.list;

import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import net.Indyuce.moarbows.util.LinearFormula;
import net.Indyuce.moarbows.util.SmallParticleEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Fire_Bow extends MoarBow {
    public Fire_Bow() {
        super(new String[]{"Shoots burning arrows that cause a", "first burst upon landing, igniting", "any entity within &c{radius} &7blocks.",
                        "Ignite duration: &c{ignite} &7seconds"}, new ParticleData(Particle.FLAME),
                new String[]{"BLAZE_ROD,BLAZE_ROD,BLAZE_ROD", "BLAZE_ROD,BOW,BLAZE_ROD", "BLAZE_ROD,BLAZE_ROD,BLAZE_ROD"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(0, 0)), new DoubleModifier("radius", new LinearFormula(5, 1)),
                new DoubleModifier("ignite", new LinearFormula(4, 2)), new DoubleModifier("max-burning-time", new LinearFormula(8, 2)));
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
        int duration = (int) (data.getDouble("ignite") * 20);
        int maxTicks = (int) (data.getDouble("max-burning-time") * 20);
        double radius = data.getDouble("radius");

        data.getArrow().remove();
        data.getArrow().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, data.getArrow().getLocation(), 0);
        data.getArrow().getWorld().spawnParticle(Particle.LAVA, data.getArrow().getLocation(), 12, 0, 0, 0);
        data.getArrow().getWorld().spawnParticle(Particle.FLAME, data.getArrow().getLocation(), 48, 0, 0, 0, .13);
        data.getArrow().getWorld().playSound(data.getArrow().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 1);
        for (Entity entity : data.getArrow().getNearbyEntities(radius, radius, radius))
            if (entity instanceof LivingEntity) {
                new SmallParticleEffect(entity, Particle.FLAME);
                entity.setFireTicks(Math.min(entity.getFireTicks() + duration, maxTicks));
            }
    }
}
