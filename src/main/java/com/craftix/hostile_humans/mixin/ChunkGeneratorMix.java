package com.craftix.hostile_humans.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.craftix.hostile_humans.HumanUtil.isStructureDisabled;

@Mixin(value = ChunkGenerator.class)
public abstract class ChunkGeneratorMix {

    @Inject(method = "tryGenerateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredStructureFeature;generate(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"), cancellable = true)
    private void injected(StructureSet.StructureSelectionEntry p_208017_, StructureFeatureManager structureFeatureManager, RegistryAccess p_208019_, StructureManager structureManager, long p_208021_, ChunkAccess chunkAccess, ChunkPos chunkPos, SectionPos p_208024_, CallbackInfoReturnable<Boolean> cir) {
        var key = p_208017_.structure().unwrapKey();

        if (key.isPresent() && key.get().location().toString().contains("hostile_humans")) {
            int x = chunkPos.getMiddleBlockX();
            int z = chunkPos.getMiddleBlockZ();
            ChunkGenerator chunkGenerator = (ChunkGenerator) (Object) this;

            if (!isLegal(x, z, chunkAccess, chunkGenerator) || !isLegal(x - 16, z, chunkAccess, chunkGenerator) || !isLegal(x + 16, z, chunkAccess, chunkGenerator) || !isLegal(x, z - 16, chunkAccess, chunkGenerator) || !isLegal(x, z + 16, chunkAccess, chunkGenerator)) {
                cir.setReturnValue(false);
                return;
            }

            if (isStructureDisabled(key.get().location().getPath())) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Unique
    private boolean isLegal(int x, int z, ChunkAccess chunkAccess, ChunkGenerator chunkGenerator) {
        int k = chunkGenerator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, chunkAccess);

        return k <= 78 && k >= 55;
    }
}