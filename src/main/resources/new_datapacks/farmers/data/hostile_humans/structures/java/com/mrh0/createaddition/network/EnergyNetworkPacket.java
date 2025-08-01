package com.mrh0.createaddition.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class EnergyNetworkPacket implements CustomPacketPayload {
	private BlockPos pos;
	private int demand;
	private int buff;
	
	public static double clientSaturation = 0;
	public static int clientDemand = 0;
	public static int clientBuff = 0;
	
	public EnergyNetworkPacket(BlockPos pos, int demand, int buff) {
		this.pos = pos;
		this.demand = demand;
		this.buff = buff;
	}
	
	public static void encode(EnergyNetworkPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.demand);
        tag.writeInt(packet.buff);
    }
	
	public static EnergyNetworkPacket decode(FriendlyByteBuf buf) {
		EnergyNetworkPacket scp = new EnergyNetworkPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
        return scp;
    }
	
	public static void handle(EnergyNetworkPacket pkt, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			try {
				updateClientCache(pkt.pos, pkt.demand, pkt.buff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
//		ctx.get().setPacketHandled(true);
	}
	
	private static void updateClientCache(BlockPos pos, int demand, int buff) {
		clientDemand = demand;
		clientBuff = buff;
		clientSaturation = buff - demand;
    }
	
	public static void send(BlockPos pos, int demand, int buff, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new EnergyNetworkPacket(pos, demand, buff));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("cas", "eng"));
	}
}
