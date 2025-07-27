package com.mrh0.createaddition.event;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.mrh0.createaddition.util.Util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = CreateAddition.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {

    public static boolean clientRenderHeldWire = false;

    @SubscribeEvent
    public static void playerRendererEvent(ClientTickEvent.Post evt) {
        if(ClientMinecraftWrapper.getPlayer() == null) return;
        ItemStack stack = ClientMinecraftWrapper.getPlayer().getInventory().getSelected();
        if(stack.isEmpty()) return;
        if(WireSpool.isRemover(stack.getItem())) return;
        clientRenderHeldWire = Util.getWireNodeOfSpools(stack) != null;
    }

    @SubscribeEvent
    public static void tickSoundscapes(ClientTickEvent.Post event) {
        CASoundScapes.tick();
    }

	// Fluid Fog TODO: update!
	/*@SubscribeEvent
	public static void getFogDensity(EntityViewRenderEvent.FogDensity event) {
		Camera info = event.getInfo();
		FluidState fluidState = info.getFluidInCamera();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getType();

		if (fluid.isSame(CAFluids.SEED_OIL.get())) {
			event.setDensity(3.5f);
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void getFogColor(EntityViewRenderEvent.FogColors event) {
		Camera info = event.getInfo();
		FluidState fluidState = info.getFluidInCamera();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getType();

		if (fluid.isSame(CAFluids.SEED_OIL.get())) {
			event.setRed(70 / 256f);
			event.setGreen(74 / 256f);
			event.setBlue(52 / 256f);
		}
	}*/

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new ResourceReloadListener());
        }
    }
}