package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.item.FEEnumHoneyComb;
import io.github.elias.beemastery.item.FEEnumIngot;
import io.github.elias.beemastery.item.FEEnumScoop;
import io.github.elias.beemastery.item.FEEnumGrafter;
import io.github.elias.beemastery.item.FEEnumNugget;
import io.github.elias.beemastery.item.FEEnumFrame;
import io.github.elias.beemastery.item.FEEnumStick;
import io.github.elias.beemastery.item.FEEnumPropolis;
import io.github.elias.beemastery.item.FEEnumAura;
import io.github.elias.beemastery.item.FEItemHoneyComb;
import io.github.elias.beemastery.item.FEItemIngot;
import io.github.elias.beemastery.item.FEItemScoop;
import io.github.elias.beemastery.item.FEItemGrafter;
import io.github.elias.beemastery.item.FEItemNugget;
import io.github.elias.beemastery.item.FEItemFrame;
import io.github.elias.beemastery.item.FEItemStick;
import io.github.elias.beemastery.item.FEItemPropolis;
import io.github.elias.beemastery.item.FEItemAuraCharm;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForestryExtras.MOD_ID);

    // Соты
    public static final Map<FEEnumHoneyComb, RegistryObject<Item>> BEE_COMBS = new HashMap<>();
    
    // Слитки
    public static final Map<FEEnumIngot, RegistryObject<Item>> INGOTS = new HashMap<>();

    // Крюки (Scoops)
    public static final Map<FEEnumScoop, RegistryObject<Item>> SCOOPS = new HashMap<>();

    // Прививочные ножи (Grafters)
    public static final Map<FEEnumGrafter, RegistryObject<Item>> GRAFTERS = new HashMap<>();

    // Самородки (Nuggets)
    public static final Map<FEEnumNugget, RegistryObject<Item>> NUGGETS = new HashMap<>();

    // Рамки (Frames)
    public static final Map<FEEnumFrame, RegistryObject<Item>> FRAMES = new HashMap<>();

    // Палки (Sticks)
    public static final Map<FEEnumStick, RegistryObject<Item>> STICKS = new HashMap<>();

    // Прополис (Propolis)
    public static final Map<FEEnumPropolis, RegistryObject<Item>> PROPOLIS = new HashMap<>();

    // Аура-талисманы (Aura Charms)
    public static final Map<FEEnumAura, RegistryObject<Item>> AURA_CHARMS = new HashMap<>();

    static {
        // Регистрируем все соты
        for (FEEnumHoneyComb combType : FEEnumHoneyComb.VALUES) {
            String name = "bee_comb_" + combType.getSerializedName();
            FEEnumHoneyComb finalCombType = combType; // Захватываем значение для лямбды
            RegistryObject<Item> comb = ITEMS.register(name, () -> new FEItemHoneyComb(finalCombType));
            BEE_COMBS.put(combType, comb);
        }
        
        // Регистрируем все слитки
        for (FEEnumIngot ingotType : FEEnumIngot.VALUES) {
            String name = "ingot_" + ingotType.getSerializedName();
            FEEnumIngot finalIngotType = ingotType; // Захватываем значение для лямбды
            RegistryObject<Item> ingot = ITEMS.register(name, () -> new FEItemIngot(finalIngotType));
            INGOTS.put(ingotType, ingot);
        }

        // Регистрируем все крюки
        for (FEEnumScoop scoopType : FEEnumScoop.VALUES) {
            String name = "scoop_" + scoopType.getSerializedName();
            FEEnumScoop finalScoopType = scoopType; // Захватываем значение для лямбды
            RegistryObject<Item> scoop = ITEMS.register(name, () -> new FEItemScoop(finalScoopType));
            SCOOPS.put(scoopType, scoop);
        }

        // Регистрируем все прививочные ножи
        for (FEEnumGrafter grafterType : FEEnumGrafter.VALUES) {
            String name = "grafter_" + grafterType.getSerializedName();
            FEEnumGrafter finalGrafterType = grafterType; // Захватываем значение для лямбды
            RegistryObject<Item> grafter = ITEMS.register(name, () -> new FEItemGrafter(finalGrafterType));
            GRAFTERS.put(grafterType, grafter);
        }

        // Регистрируем все самородки
        for (FEEnumNugget nuggetType : FEEnumNugget.VALUES) {
            String name = "nugget_" + nuggetType.getSerializedName();
            FEEnumNugget finalNuggetType = nuggetType; // Захватываем значение для лямбды
            RegistryObject<Item> nugget = ITEMS.register(name, () -> new FEItemNugget(finalNuggetType));
            NUGGETS.put(nuggetType, nugget);
        }

        // Регистрируем все рамки
        for (FEEnumFrame frameType : FEEnumFrame.VALUES) {
            String name = "frame_" + frameType.getSerializedName();
            FEEnumFrame finalFrameType = frameType; // Захватываем значение для лямбды
            RegistryObject<Item> frame = ITEMS.register(name, () -> new FEItemFrame(finalFrameType));
            FRAMES.put(frameType, frame);
        }

        // Регистрируем все палки
        for (FEEnumStick stickType : FEEnumStick.VALUES) {
            String name = "stick_" + stickType.getSerializedName();
            FEEnumStick finalStickType = stickType; // Захватываем значение для лямбды
            RegistryObject<Item> stick = ITEMS.register(name, () -> new FEItemStick(finalStickType));
            STICKS.put(stickType, stick);
        }

        // Регистрируем весь прополис
        for (FEEnumPropolis propolisType : FEEnumPropolis.VALUES) {
            String name = "propolis_" + propolisType.getSerializedName();
            FEEnumPropolis finalPropolisType = propolisType; // Захватываем значение для лямбды
            RegistryObject<Item> propolis = ITEMS.register(name, () -> new FEItemPropolis(finalPropolisType));
            PROPOLIS.put(propolisType, propolis);
        }

        // Регистрируем аура-талисманы
        for (FEEnumAura auraType : FEEnumAura.VALUES) {
            String name = "aura_charm_" + auraType.getSerializedName();
            FEEnumAura finalAuraType = auraType; // Захватываем значение для лямбды
            RegistryObject<Item> charm = ITEMS.register(name, () -> new FEItemAuraCharm(finalAuraType));
            AURA_CHARMS.put(auraType, charm);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
