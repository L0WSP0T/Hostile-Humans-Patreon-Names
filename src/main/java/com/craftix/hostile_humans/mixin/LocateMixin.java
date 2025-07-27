package com.craftix.hostile_humans.mixin;

import com.craftix.hostile_humans.HumanUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocateCommand.class)
public abstract class LocateMixin {

    @Unique
    private static final DynamicCommandExceptionType ERROR_DISABLED = new DynamicCommandExceptionType((p_201831_) -> new TranslatableComponent("error.disabled", p_201831_));
    @Shadow
    @Final
    private static DynamicCommandExceptionType ERROR_INVALID;

    @Inject(method = "locate", at = @At(value = "HEAD"))
    private static void injected(CommandSourceStack p_207515_, ResourceOrTagLocationArgument.Result<ConfiguredStructureFeature<?, ?>> p_207516_, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {

        Registry<ConfiguredStructureFeature<?, ?>> registry = p_207515_.getLevel().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        HolderSet<ConfiguredStructureFeature<?, ?>> holderset = p_207516_.unwrap().map((p_207532_) -> registry.getHolder(p_207532_).map(HolderSet::direct), registry::getTag).orElseThrow(() -> ERROR_INVALID.create(p_207516_.asPrintable()));

        for (var val : holderset) {
            String path = val.unwrapKey().get().location().getPath();
            if (HumanUtil.isStructureDisabled(path)) {
                throw (ERROR_DISABLED.create(p_207516_.asPrintable()));
            }
        }
    }
}