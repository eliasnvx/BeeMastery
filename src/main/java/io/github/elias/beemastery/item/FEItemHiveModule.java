package io.github.elias.beemastery.item;

import io.github.elias.beemastery.config.BeeMasteryConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** A module for the portable hive's module slots (see {@link FEEnumHiveModule}). */
public class FEItemHiveModule extends Item {
    private final FEEnumHiveModule type;

    public FEItemHiveModule(FEEnumHiveModule type) {
        super(new Properties().stacksTo(16));
        this.type = type;
    }

    public FEEnumHiveModule getType() {
        return type;
    }

    /** The number shown in the description of modules whose strength is configurable. */
    private Object configuredValue() {
        var config = BeeMasteryConfig.SERVER;
        return switch (type) {
            case BOOSTER -> Math.round((config.boosterMultiplier() - 1) * 100);
            case LONGEVITY -> number(1 / config.longevityAging());
            case MUTAGEN -> number(config.mutagenMultiplier());
            default -> "";
        };
    }

    /** 2.0 -> "2", 1.5 -> "1.5". */
    private static String number(float value) {
        float rounded = Math.round(value * 100) / 100f;
        return rounded == (int) rounded ? Integer.toString((int) rounded) : Float.toString(rounded);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("beemastery.module." + type.name + ".desc", configuredValue()).withStyle(ChatFormatting.GRAY));
        for (FEEnumHiveModule other : FEEnumHiveModule.VALUES) {
            if (type.conflictsWith(other)) {
                tooltip.add(Component.translatable("beemastery.module.conflicts",
                        Component.translatable("item.beemastery.hive_module_" + other.name)).withStyle(ChatFormatting.RED));
            }
        }
        tooltip.add(Component.translatable("beemastery.module.hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
