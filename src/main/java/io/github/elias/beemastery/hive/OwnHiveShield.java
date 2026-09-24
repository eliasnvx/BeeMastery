package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

/**
 * Your own worn hive never turns its bees on you.
 *
 * <p>Forestry's harmful bee effects (aggressive, radioactive, ignition, creeper, phasing, harmful
 * potions...) hit every living thing in the hive's territory unless it wears a full apiarist suit.
 * A hive on your back is only one "piece", and you are always inside its territory — so while the
 * wearer's own hive runs its bees, whatever those effects do to the wearer is undone: damage and
 * the creeper blast are cancelled, harmful potion effects are refused, fire is put out and a
 * random teleport is reverted. Other hives — stationary or someone else's — still affect you.
 */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID)
public final class OwnHiveShield {

    /** The wearer whose own hive is running its bees right now (server thread only), or null. */
    private static Player shielded;

    private OwnHiveShield() {
    }

    /** Run {@code work} (the hive's bee logic) with its wearer shielded from the effects it causes. */
    public static void run(Player wearer, Runnable work) {
        Vec3 position = wearer.position();
        int fire = wearer.getRemainingFireTicks();
        shielded = wearer;
        try {
            work.run();
        } finally {
            shielded = null;
        }
        if (wearer.getRemainingFireTicks() > fire) {
            wearer.setRemainingFireTicks(fire);
        }
        if (wearer.position().distanceToSqr(position) > 1.0E-4) {
            wearer.teleportTo(position.x, position.y, position.z);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if (shielded != null && event.getEntity() == shielded) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEffect(MobEffectEvent.Applicable event) {
        if (shielded != null && event.getEntity() == shielded
                && event.getEffectInstance().getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    /** The creeper effect's blast (no block damage): keep the wearer out of it, so no damage and no knockback. */
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (shielded != null) {
            event.getAffectedEntities().remove(shielded);
        }
    }
}
