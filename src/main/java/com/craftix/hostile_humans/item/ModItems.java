package com.craftix.hostile_humans.item;

import com.craftix.hostile_humans.HostileHumans;
import com.craftix.hostile_humans.entity.entities.ModEntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HostileHumans.MOD_ID);
    public static final RegistryObject<Item> HUMAN1_SPAWN_EGG = ITEMS.register("human_tier1_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityType.HUMAN1, MaterialColor.COLOR_ORANGE.col,
                    MaterialColor.TERRACOTTA_WHITE.col,
                    new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> HUMAN2_SPAWN_EGG = ITEMS.register("human_tier2_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityType.HUMAN2, MaterialColor.COLOR_ORANGE.col,
                    MaterialColor.GOLD.col,
                    new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> ROAMER_SPAWN_EGG = ITEMS.register("human_roamer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityType.ROAMER, MaterialColor.COLOR_ORANGE.col,
                    MaterialColor.COLOR_BLACK.col,
                    new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
}
