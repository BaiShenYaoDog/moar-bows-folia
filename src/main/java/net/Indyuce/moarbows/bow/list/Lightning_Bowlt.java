package net.Indyuce.moarbows.bow.list;

import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import net.Indyuce.moarbows.util.LinearFormula;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Lightning_Bowlt extends MoarBow {
    public Lightning_Bowlt() {
        super("LIGHTNING_BOWLT", "&fLightning Bow'lt", new String[]{"Shoots arrows that summon", "lightning upon landing."}, 0,
                new ParticleData(Particle.FIREWORKS_SPARK), new String[]{"AIR,BEACON,AIR", "AIR,BOW,AIR", "AIR,AIR,AIR"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(10, -1, 3, 10)));
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
        data.getArrow().getWorld().strikeLightning(data.getArrow().getLocation());
    }
}
