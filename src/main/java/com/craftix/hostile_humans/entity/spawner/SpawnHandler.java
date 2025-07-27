package com.craftix.hostile_humans.entity.spawner;

import com.craftix.hostile_humans.entity.HumanEntity;
import com.craftix.hostile_humans.entity.entities.Human;
import com.craftix.hostile_humans.entity.entities.ModEntityType;
import com.craftix.hostile_humans.entity.entities.SpawnerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Random;

import static net.minecraft.world.entity.Mob.checkMobSpawnRules;

@Mod.EventBusSubscriber()
public class SpawnHandler {

    protected SpawnHandler() {
    }

    @SubscribeEvent
    public static void onWorldLoad(ServerAboutToStartEvent event) {

    }

    @SubscribeEvent()
    public static void handleBiomeLoadingEvent(BiomeLoadingEvent event) {
        ResourceLocation biomeRegistry = event.getName();
        if (biomeRegistry == null) {
            return;
        }

        event.getSpawns().getSpawner(HumanEntity.CATEGORY).add(new MobSpawnSettings.SpawnerData(ModEntityType.ROAMER.get(), 1, 1, 1));
        event.getSpawns().getSpawner(HumanEntity.CATEGORY).add(new MobSpawnSettings.SpawnerData(ModEntityType.SPAWNER_ENTITY.get(), 1, 1, 1));
    }

    public static void registerSpawnPlacements(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntityType.ROAMER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnHandler::checkHumanSpawnRules);
            SpawnPlacements.register(ModEntityType.SPAWNER_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnHandler::checkSpawnerEntityRules);
        });
    }

    public static boolean checkSpawnerEntityRules(EntityType<SpawnerEntity> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, Random random) {
        if (random.nextInt(200) != 0) return false;
        return isBrightEnoughToSpawn(p_33019_, p_33021_, random) && checkMobSpawnRules(p_33018_, p_33019_, p_33020_, p_33021_, random);
    }

    public static boolean checkHumanSpawnRules(EntityType<? extends Human> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, Random random) {
        if (random.nextInt(200) != 0) return false;
        return isBrightEnoughToSpawn(p_33019_, p_33021_, random) && checkMobSpawnRules(p_33018_, p_33019_, p_33020_, p_33021_, random);
    }

    public static boolean isBrightEnoughToSpawn(ServerLevelAccessor p_33009_, BlockPos p_33010_, Random p_33011_) {
        return p_33009_.getBrightness(LightLayer.SKY, p_33010_) > 10;
    }
}
