package io.github.elias.beemastery.client;

import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.item.FEItemHoneyComb;
import io.github.elias.beemastery.item.FEItemIngot;
import io.github.elias.beemastery.item.FEItemScoop;
import io.github.elias.beemastery.item.FEItemGrafter;
import io.github.elias.beemastery.item.FEItemNugget;
import io.github.elias.beemastery.item.FEItemFrame;
import io.github.elias.beemastery.item.FEItemStick;
import io.github.elias.beemastery.item.FEItemPropolis;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "beemastery", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Регистрируем цвета для всех сот
        for (var combEntry : ModItems.BEE_COMBS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemHoneyComb honeyComb) {
                        return honeyComb.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, combEntry.getValue().get());
        }
        
        // Регистрируем цвета для всех слитков
        for (var ingotEntry : ModItems.INGOTS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemIngot ingot) {
                        return ingot.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, ingotEntry.getValue().get());
        }

        // Регистрируем цвета для всех крюков
        for (var scoopEntry : ModItems.SCOOPS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemScoop scoop) {
                        return scoop.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, scoopEntry.getValue().get());
        }

        // Регистрируем цвета для всех прививочных ножей
        for (var grafterEntry : ModItems.GRAFTERS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemGrafter grafter) {
                        return grafter.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, grafterEntry.getValue().get());
        }

        // Регистрируем цвета для всех самородков
        for (var nuggetEntry : ModItems.NUGGETS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemNugget nugget) {
                        return nugget.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, nuggetEntry.getValue().get());
        }

        // Регистрируем цвета для всех рамок
        for (var frameEntry : ModItems.FRAMES.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemFrame frame) {
                        return frame.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, frameEntry.getValue().get());
        }

        // Регистрируем цвета для всех палок
        for (var stickEntry : ModItems.STICKS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemStick stick) {
                        return stick.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, stickEntry.getValue().get());
        }

        // Регистрируем цвета для всего прополиса
        for (var propolisEntry : ModItems.PROPOLIS.entrySet()) {
            event.register(new ItemColor() {
                @Override
                public int getColor(ItemStack stack, int tintIndex) {
                    if (stack.getItem() instanceof FEItemPropolis propolis) {
                        return propolis.getColorFromItemStack(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                }
            }, propolisEntry.getValue().get());
        }
    }
}
