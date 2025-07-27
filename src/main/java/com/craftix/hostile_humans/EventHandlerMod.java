package com.craftix.hostile_humans;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathResourcePack;

import java.nio.file.Path;

import static com.craftix.hostile_humans.HostileHumans.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandlerMod {

    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
    	boolean quark = ModList.get().isLoaded("quark");
    	boolean mctb = ModList.get().isLoaded("mctb");
    	boolean farmersdelight = ModList.get().isLoaded("farmersdelight");
    	boolean waystones = ModList.get().isLoaded("waystones") && !Config.noWaystones.get();
    	if (farmersdelight && waystones && quark && mctb)
            registerAddon(event, "craftin_farmers_waystones");
    	else if (farmersdelight && quark && mctb)
            registerAddon(event, "craftin_farmers");
    	else if (waystones && quark && mctb)
            registerAddon(event, "craftin_waystones");
    	else if (farmersdelight && waystones)
            registerAddon(event, "farmers_waystones");
    	else if (quark && mctb)
    		registerAddon(event, "craftin");
        else if (farmersdelight)
            registerAddon(event, "farmers");
        else if (waystones)
            registerAddon(event, "waystones");
    }

    //https://github.com/MinecraftModDevelopmentMods/Extra-Golems/blob/master-1.19-2/src/main/java/com/mcmoddev/golems/integration/AddonLoader.java
    private static void registerAddon(final AddPackFindersEvent event, final String packName) {
        event.addRepositorySource((packConsumer, constructor) -> {
            Pack pack = Pack.create(MOD_ID + ":" + packName, true, () -> {
                Path path = ModList.get().getModFileById(MOD_ID).getFile().findResource("/datapacks/" + packName);
                return new PathResourcePack(packName, path);
            }, constructor, Pack.Position.TOP, PackSource.DEFAULT);

            if (pack != null) {
                packConsumer.accept(pack);
            } else {
                throw new RuntimeException("Couldn't find pack " + packName);
            }
        });
    }
}
