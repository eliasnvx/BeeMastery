package io.github.elias.beemastery.config;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.HiveTier;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Bee Mastery settings.
 *
 * <p>{@link #SERVER}: gameplay numbers, per world ({@code serverconfig/beemastery-server.toml},
 * modpacks ship defaults in {@code defaultconfigs/}); NeoForge syncs it to joining clients, so
 * tooltips show the server's values. {@link #CLIENT}: per-player display options.
 *
 * <p>Getters fall back to the default before the config is loaded (e.g. an item tooltip in the
 * main menu), since reading an unloaded value throws.
 */
public final class BeeMasteryConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final Server SERVER;
    public static final Client CLIENT;

    static {
        ModConfigSpec.Builder server = new ModConfigSpec.Builder();
        SERVER = new Server(server);
        SERVER_SPEC = server.build();
        ModConfigSpec.Builder client = new ModConfigSpec.Builder();
        CLIENT = new Client(client);
        CLIENT_SPEC = client.build();
    }

    private BeeMasteryConfig() {
    }

    private static String key(String name) {
        return ForestryExtras.MOD_ID + ".configuration." + name;
    }

    public static final class Server {
        private final ModConfigSpec.DoubleValue[] tierSpeed = new ModConfigSpec.DoubleValue[HiveTier.VALUES.length];
        private final ModConfigSpec.BooleanValue ownHiveProtection;
        private final ModConfigSpec.DoubleValue boosterMultiplier;
        private final ModConfigSpec.DoubleValue longevityAging;
        private final ModConfigSpec.DoubleValue mutagenMultiplier;
        private final ModConfigSpec.IntValue collectorInterval;
        private final ModConfigSpec.BooleanValue lanternLight;
        private final ModConfigSpec.IntValue lanternLightLevel;

        private Server(ModConfigSpec.Builder b) {
            b.comment("The portable hive (worn on the back).").translation(key("portable_hive")).push("portable_hive");
            double[] defaults = {0.25, 0.35, 0.5, 0.75};
            for (HiveTier tier : HiveTier.VALUES) {
                tierSpeed[tier.ordinal()] = b
                        .comment("Production speed of a " + tier.name + " hive, relative to the bees' own speed gene. Forestry's Bee House is 0.25.")
                        .translation(key("speed_" + tier.name))
                        .defineInRange("speed_" + tier.name, defaults[tier.ordinal()], 0.0, 10.0);
            }
            ownHiveProtection = b
                    .comment("The wearer is never hurt by their own hive's bee effects (damage, harmful potions, fire, teleports, creeper blasts).")
                    .translation(key("own_hive_protection"))
                    .define("own_hive_protection", true);

            b.comment("Hive modules.").translation(key("modules")).push("modules");
            boosterMultiplier = b.comment("Production multiplier of the Booster module.")
                    .translation(key("booster_multiplier")).defineInRange("booster_multiplier", 1.25, 1.0, 10.0);
            longevityAging = b.comment("Queen ageing speed with the Longevity module (0.5 = she lives twice as long).")
                    .translation(key("longevity_aging")).defineInRange("longevity_aging", 0.5, 0.05, 1.0);
            mutagenMultiplier = b.comment("Mutation chance multiplier of the Mutagen module.")
                    .translation(key("mutagen_multiplier")).defineInRange("mutagen_multiplier", 2.0, 1.0, 10.0);
            collectorInterval = b.comment("How often the Collector module moves products into the wearer's inventory, in ticks (20 = once a second).")
                    .translation(key("collector_interval")).defineInRange("collector_interval", 20, 1, 1200);
            lanternLight = b.comment("The Lantern module lights up the area around the wearer (an invisible light block that follows them). "
                            + "When off, the lantern only makes the bees work day and night.")
                    .translation(key("lantern_light")).define("lantern_light", true);
            lanternLightLevel = b.comment("Light level of the Lantern module (15 = as bright as a torch or lantern block).")
                    .translation(key("lantern_light_level")).defineInRange("lantern_light_level", 14, 1, 15);
            b.pop(2);
        }

        public float tierSpeed(HiveTier tier) {
            return (float) (double) get(tierSpeed[tier.ordinal()]);
        }

        public boolean ownHiveProtection() {
            return get(ownHiveProtection);
        }

        public float boosterMultiplier() {
            return (float) (double) get(boosterMultiplier);
        }

        public float longevityAging() {
            return (float) (double) get(longevityAging);
        }

        public float mutagenMultiplier() {
            return (float) (double) get(mutagenMultiplier);
        }

        public int collectorInterval() {
            return get(collectorInterval);
        }

        public boolean lanternLight() {
            return get(lanternLight);
        }

        public int lanternLightLevel() {
            return get(lanternLightLevel);
        }

        private static <T> T get(ModConfigSpec.ConfigValue<T> value) {
            return SERVER_SPEC.isLoaded() ? value.get() : value.getDefault();
        }
    }

    public static final class Client {
        public enum Corner { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

        private final ModConfigSpec.BooleanValue showHud;
        private final ModConfigSpec.EnumValue<Corner> hudCorner;
        private final ModConfigSpec.BooleanValue beeParticles;

        private Client(ModConfigSpec.Builder b) {
            b.comment("The portable hive (worn on the back).").translation(key("portable_hive")).push("portable_hive");
            showHud = b.comment("Show the small panel with the queen and the hive status while a hive is worn.")
                    .translation(key("show_hud")).define("show_hud", true);
            hudCorner = b.comment("Screen corner for that panel.")
                    .translation(key("hud_corner")).defineEnum("hud_corner", Corner.TOP_LEFT);
            beeParticles = b.comment("Bees buzzing around worn hives (yours and other players').")
                    .translation(key("bee_particles")).define("bee_particles", true);
            b.pop();
        }

        public boolean showHud() {
            return get(showHud);
        }

        public Corner hudCorner() {
            return get(hudCorner);
        }

        public boolean beeParticles() {
            return get(beeParticles);
        }

        private static <T> T get(ModConfigSpec.ConfigValue<T> value) {
            return CLIENT_SPEC.isLoaded() ? value.get() : value.getDefault();
        }
    }
}
