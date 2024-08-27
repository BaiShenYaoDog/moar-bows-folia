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

public class Composite_Bow extends MoarBow {
    public Composite_Bow() {
        super(new String[]{"Fires enchanted arrows that", "follow a linear trajectory.", "Deals &c{damage} &7damage."},
                new ParticleData(Particle.REDSTONE, Color.fromRGB(91, 60, 17), 2),
                new String[]{"AIR,AIR,AIR", "BOW,NETHER_STAR,BOW", "AIR,AIR,AIR"});

        addModifier(new DoubleModifier("cooldown", new LinearFormula(2, 0)), new DoubleModifier("damage", new LinearFormula(8, 2)));
    }

    @Override
    public boolean canShoot(EntityShootBowEvent event, ArrowData data) {
        final double dmg = data.getDouble("damage") * BowUtils.getPowerDamageMultiplier(data.getSource());
        event.setCancelled(true);
        if (!BowUtils.consumeAmmo(data.getShooter(), new ItemStack(Material.ARROW)))
            return false;

        data.getShooter().getWorld().playSound(data.getShooter().getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 0);
        new UniversalRunnable() {
            final Location loc = data.getShooter().getEyeLocation();
            final double max = 20 * event.getForce();
            final Vector v = data.getShooter().getEyeLocation().getDirection().multiply(1.25);
            double ti = 0;

            public void run() {
                for (double j = 0; j < 3; j++) {
                    ti += .5;
                    loc.add(v);
                    loc.getWorld().spawnParticle(Particle.CRIT, loc, 8, .1, .1, .1, .1);
                    loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 3, 2);
                    for (LivingEntity entity : loc.getWorld().getEntitiesByClass(LivingEntity.class))
                        if (BowUtils.canTarget(data.getShooter(), loc, entity) && !entity.equals(data.getShooter())) {
                            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 0);
                            loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 0);
                            cancel();
                            entity.damage(dmg, data.getShooter());
                            return;
                        }
                }
                if (ti >= max)
                    cancel();
            }
        }.runTaskTimer(MoarBows.plugin, 0, 1);
        return false;
    }

    @Override
    public void whenHit(EntityDamageByEntityEvent event, ArrowData data, Entity target) {
        // TODO Auto-generated method stub

    }

    @Override
    public void whenLand(ArrowData data) {
        // TODO Auto-generated method stub

    }
}
