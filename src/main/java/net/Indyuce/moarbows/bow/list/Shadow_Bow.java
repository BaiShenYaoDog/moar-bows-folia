package net.Indyuce.moarbows.bow.list;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import net.Indyuce.moarbows.BowUtils;
import net.Indyuce.moarbows.MoarBows;
import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import net.Indyuce.moarbows.util.LinearFormula;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Shadow_Bow extends MoarBow {
    public Shadow_Bow() {
        super(new String[]{"Shoots a long ranged linear", "cursed arrow that deals &c{damage}", "damage tothe first entity it hits."},
                new ParticleData(Particle.REDSTONE, Color.fromRGB(128, 0, 128), 2),
                new String[]{"ENDER_EYE,ENDER_EYE,ENDER_EYE", "ENDER_EYE,BOW,ENDER_EYE", "ENDER_EYE,ENDER_EYE,ENDER_EYE"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(10, -1, 3, 10)), new DoubleModifier("damage", new LinearFormula(8, 4)));
    }

    @Override
    public boolean canShoot(EntityShootBowEvent event, ArrowData data) {
        event.setCancelled(true);
        final double damage = data.getDouble("damage") * BowUtils.getPowerDamageMultiplier(data.getSource());
        if (!BowUtils.consumeAmmo(data.getShooter(), new ItemStack(Material.ARROW)))
            return false;

        new UniversalRunnable() {
            final Location loc = data.getShooter().getEyeLocation();
            final Vector v = data.getShooter().getEyeLocation().getDirection().multiply(1.25);
            double ti = 0;

            public void run() {
                for (double j = 0; j < 3; j++) {
                    ti += .5;
                    loc.add(v);
                    loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 8, .1, .1, .1, 0);
                    loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_HURT, 3, 2);
                    for (LivingEntity entity : loc.getWorld().getEntitiesByClass(LivingEntity.class))
                        if (BowUtils.canTarget(data.getShooter(), loc, entity)) {
                            new UniversalRunnable() {
                                final Location loc2 = entity.getLocation();
                                double y = 0;

                                public void run() {
                                    for (int item = 0; item < 2; item++) {
                                        y += .05;
                                        for (int j = 0; j < 2; j++) {
                                            double xz = y * Math.PI * .8 + (j * Math.PI);
                                            loc.getWorld().spawnParticle(Particle.REDSTONE,
                                                    loc2.clone().add(Math.cos(xz) * 1.3, y, Math.sin(xz) * 1.3), 0,
                                                    new Particle.DustOptions(Color.PURPLE, 1));
                                        }
                                    }
                                    if (y >= 2.5) {
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(MoarBows.plugin, 0, 1);
                            loc.getWorld().playSound(entity.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 0);
                            loc.getWorld().spawnParticle(Particle.SPELL_WITCH, entity.getLocation().add(0, 1, 0), 0);
                            cancel();
                            entity.damage(damage, entity);
                            return;
                        }
                }
                if (ti >= 20 * event.getForce())
                    cancel();
            }
        }.runTaskTimer(MoarBows.plugin, 0, 1);
        return false;
    }

    @Override
    public void whenHit(EntityDamageByEntityEvent event, ArrowData data, Entity target) {
    }

    @Override
    public void whenLand(ArrowData data) {
    }
}
