package com.craftix.hostile_humans.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Beardifier.class)
public abstract class BeardifierMix {

    @Shadow
    @Final
    protected ObjectList<StructurePiece> rigids;

    @Shadow
    @Final
    protected ObjectList<JigsawJunction> junctions;

    @Inject(method = "lambda$new$2(Lnet/minecraft/world/level/ChunkPos;IILnet/minecraft/world/level/levelgen/structure/StructureStart;)V", at = @At(value = "HEAD"), cancellable = true)
    private void injected(ChunkPos chunkpos, int i, int j, StructureStart p_208198_, CallbackInfo ci) {
        ci.cancel();
        for (StructurePiece structurepiece : p_208198_.getPieces()) {
            if (structurepiece.isCloseToChunk(chunkpos, 12)) {
                if (structurepiece instanceof PoolElementStructurePiece poolelementstructurepiece) {

                    if (poolelementstructurepiece.getElement() instanceof SinglePoolElement singlePoolElement && singlePoolElement.template.toString().contains("hostile_humans:fortress_bottom")) {
                        return;
                    }

                    StructureTemplatePool.Projection structuretemplatepool$projection = poolelementstructurepiece.getElement().getProjection();
                    if (structuretemplatepool$projection == StructureTemplatePool.Projection.RIGID) {
                        rigids.add(poolelementstructurepiece);
                    }

                    for (JigsawJunction jigsawjunction : poolelementstructurepiece.getJunctions()) {
                        int k = jigsawjunction.getSourceX();
                        int l = jigsawjunction.getSourceZ();
                        if (k > i - 12 && l > j - 12 && k < i + 15 + 12 && l < j + 15 + 12) {
                            junctions.add(jigsawjunction);
                        }
                    }
                } else {
                    this.rigids.add(structurepiece);
                }
            }
        }
    }
}