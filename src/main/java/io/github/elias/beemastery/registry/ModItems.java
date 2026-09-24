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
import io.github.elias.beemastery.item.FEItemAuraBelt;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemHiveModule;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;

import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ForestryExtras.MOD_ID);

    // Соты
    public static final Map<FEEnumHoneyComb, DeferredItem<Item>> BEE_COMBS = new HashMap<>();
    
    // Слитки
    public static final Map<FEEnumIngot, DeferredItem<Item>> INGOTS = new HashMap<>();

    // Крюки (Scoops)
    public static final Map<FEEnumScoop, DeferredItem<Item>> SCOOPS = new HashMap<>();

    // Прививочные ножи (Grafters)
    public static final Map<FEEnumGrafter, DeferredItem<Item>> GRAFTERS = new HashMap<>();

    // Самородки (Nuggets)
    public static final Map<FEEnumNugget, DeferredItem<Item>> NUGGETS = new HashMap<>();

    // Рамки (Frames)
    public static final Map<FEEnumFrame, DeferredItem<Item>> FRAMES = new HashMap<>();

    // Палки (Sticks)
    public static final Map<FEEnumStick, DeferredItem<Item>> STICKS = new HashMap<>();

    // Прополис (Propolis)
    public static final Map<FEEnumPropolis, DeferredItem<Item>> PROPOLIS = new HashMap<>();

    // Аура-талисманы (Aura Charms)
    public static final Map<FEEnumAura, DeferredItem<Item>> AURA_CHARMS = new HashMap<>();

    // Пояс аур (держит ограниченное число талисманов)
    public static final DeferredItem<Item> AURA_BELT = ITEMS.registerItem("aura_belt", properties -> new FEItemAuraBelt(properties));

    // Модули портативного улья
    public static final Map<FEEnumHiveModule, DeferredItem<Item>> HIVE_MODULES = new HashMap<>();

    // Походный улей (носится в слоте нагрудника)
    public static final DeferredItem<Item> PORTABLE_HIVE = ITEMS.registerItem("portable_hive", FEItemPortableHive::new);

    static {
        // Регистрируем все соты
        for (FEEnumHoneyComb combType : FEEnumHoneyComb.VALUES) {
            String name = "bee_comb_" + combType.getSerializedName();
            FEEnumHoneyComb finalCombType = combType; // Захватываем значение для лямбды
            DeferredItem<Item> comb = ITEMS.registerItem(name, properties -> new FEItemHoneyComb(finalCombType, properties));
            BEE_COMBS.put(combType, comb);
        }
        
        // Регистрируем все слитки
        for (FEEnumIngot ingotType : FEEnumIngot.VALUES) {
            String name = "ingot_" + ingotType.getSerializedName();
            FEEnumIngot finalIngotType = ingotType; // Захватываем значение для лямбды
            DeferredItem<Item> ingot = ITEMS.registerItem(name, properties -> new FEItemIngot(finalIngotType, properties));
            INGOTS.put(ingotType, ingot);
        }

        // Регистрируем все крюки
        for (FEEnumScoop scoopType : FEEnumScoop.VALUES) {
            String name = "scoop_" + scoopType.getSerializedName();
            FEEnumScoop finalScoopType = scoopType; // Захватываем значение для лямбды
            DeferredItem<Item> scoop = ITEMS.registerItem(name, properties -> new FEItemScoop(finalScoopType, properties));
            SCOOPS.put(scoopType, scoop);
        }

        // Регистрируем все прививочные ножи
        for (FEEnumGrafter grafterType : FEEnumGrafter.VALUES) {
            String name = "grafter_" + grafterType.getSerializedName();
            FEEnumGrafter finalGrafterType = grafterType; // Захватываем значение для лямбды
            DeferredItem<Item> grafter = ITEMS.registerItem(name, properties -> new FEItemGrafter(finalGrafterType, properties));
            GRAFTERS.put(grafterType, grafter);
        }

        // Регистрируем все самородки
        for (FEEnumNugget nuggetType : FEEnumNugget.VALUES) {
            String name = "nugget_" + nuggetType.getSerializedName();
            FEEnumNugget finalNuggetType = nuggetType; // Захватываем значение для лямбды
            DeferredItem<Item> nugget = ITEMS.registerItem(name, properties -> new FEItemNugget(finalNuggetType, properties));
            NUGGETS.put(nuggetType, nugget);
        }

        // Регистрируем все рамки
        for (FEEnumFrame frameType : FEEnumFrame.VALUES) {
            String name = "frame_" + frameType.getSerializedName();
            FEEnumFrame finalFrameType = frameType; // Захватываем значение для лямбды
            DeferredItem<Item> frame = ITEMS.registerItem(name, properties -> new FEItemFrame(finalFrameType, properties));
            FRAMES.put(frameType, frame);
        }

        // Регистрируем все палки
        for (FEEnumStick stickType : FEEnumStick.VALUES) {
            String name = "stick_" + stickType.getSerializedName();
            FEEnumStick finalStickType = stickType; // Захватываем значение для лямбды
            DeferredItem<Item> stick = ITEMS.registerItem(name, properties -> new FEItemStick(finalStickType, properties));
            STICKS.put(stickType, stick);
        }

        // Регистрируем весь прополис
        for (FEEnumPropolis propolisType : FEEnumPropolis.VALUES) {
            String name = "propolis_" + propolisType.getSerializedName();
            FEEnumPropolis finalPropolisType = propolisType; // Захватываем значение для лямбды
            DeferredItem<Item> propolis = ITEMS.registerItem(name, properties -> new FEItemPropolis(finalPropolisType, properties));
            PROPOLIS.put(propolisType, propolis);
        }

        // Регистрируем модули портативного улья
        for (FEEnumHiveModule moduleType : FEEnumHiveModule.VALUES) {
            String name = "hive_module_" + moduleType.getSerializedName();
            DeferredItem<Item> module = ITEMS.registerItem(name, properties -> new FEItemHiveModule(moduleType, properties));
            HIVE_MODULES.put(moduleType, module);
        }

        // Регистрируем аура-талисманы
        for (FEEnumAura auraType : FEEnumAura.VALUES) {
            String name = "aura_charm_" + auraType.getSerializedName();
            FEEnumAura finalAuraType = auraType; // Захватываем значение для лямбды
            DeferredItem<Item> charm = ITEMS.registerItem(name, properties -> new FEItemAuraCharm(finalAuraType, properties));
            AURA_CHARMS.put(auraType, charm);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
