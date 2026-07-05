package io.github.elias.beemastery.client;

import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.item.FEItemHoneyComb;
import io.github.elias.beemastery.item.FEItemFrame;
import io.github.elias.beemastery.item.FEItemStick;
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

        // Прополис, слитки, самородки, крюки, прививочные ножи и аура-талисманы используют собственные готовые текстуры, без тинта.
    }
}
