package io.github.elias.beemastery.plugin;

import com.mojang.logging.LogUtils;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IForestryPlugin;
import io.github.elias.beemastery.effect.AuraBeeEffect;
import io.github.elias.beemastery.item.FEEnumAura;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Registers Bee Mastery's aura bee effects with Forestry CE.
 *
 * <p>Bee species themselves are <b>data-driven</b> in Forestry 3.x: they live in
 * {@code data/beemastery/bee_species/*.json}, and their mutations in
 * {@code data/beemastery/recipe/bee_mutation/*.json}. Forestry rebuilds the species list from
 * those files on every datapack load, so species registered from code here would be wiped the
 * moment a world loads. Effects are still code — the species JSON refers to them by id
 * ({@code beemastery:<aura>_aura}).
 */
public class ForestryExtrasPlugin implements IForestryPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath("beemastery", "plugin");
    }

    @Override
    public void registerApiculture(IApicultureRegistration registration) {
        for (FEEnumAura aura : FEEnumAura.VALUES) {
            registration.registerBeeEffect(auraId(aura), new AuraBeeEffect(aura));
        }
        LOGGER.info("[BeeMastery] Registered {} aura effects.", FEEnumAura.VALUES.length);
    }

    private static ResourceLocation auraId(FEEnumAura aura) {
        return ResourceLocation.fromNamespaceAndPath("beemastery", aura.getSerializedName() + "_aura");
    }
}
