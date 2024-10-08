package net.Indyuce.moarbows.bow.list;

import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import net.Indyuce.moarbows.util.LinearFormula;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Explosive_Bow extends MoarBow {
    public Explosive_Bow() {
        super(new String[]{"Arrows explode when landing, deal", "&c{damage} &7damage to nearby entities."},
                new ParticleData(Particle.EXPLOSION_NORMAL), new String[]{"TNT,TNT,TNT", "TNT,BOW,TNT", "TNT,TNT,TNT"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(0, 0)), new DoubleModifier("damage", new LinearFormula(8, 4)));
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
        double dmg = data.getDouble("damage");
        data.getArrow().remove();
        data.getArrow().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, data.getArrow().getLocation(), 16, 1.5, 1.5, 1.5);
        data.getArrow().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, data.getArrow().getLocation(), 48, 0, 0, 0, .4);
        data.getArrow().getWorld().playSound(data.getArrow().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
        for (Entity ent : data.getArrow().getNearbyEntities(5, 5, 5))
            if (ent instanceof LivingEntity)
                ((LivingEntity) ent).damage(dmg, data.getShooter());
    }
}
