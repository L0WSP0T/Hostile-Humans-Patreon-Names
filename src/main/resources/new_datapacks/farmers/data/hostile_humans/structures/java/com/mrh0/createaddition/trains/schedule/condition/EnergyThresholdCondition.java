package com.mrh0.createaddition.trains.schedule.condition;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.CargoThresholdCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyThresholdCondition extends CargoThresholdCondition {
    @Override
    protected Component getUnit() {
        return Component.literal("Kfe");
    }

    @Override
    protected ItemStack getIcon() {
        return CAItems.CAPACITOR.asStack();
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return CAItems.CAPACITOR.asStack();
    }

    @Override
    public int slotsTargeted() {
        return 0;
    }

    public boolean lazyTickCompletion(Level level, Train train, CompoundTag context) {
        return this.test(level, train, context);
    }

    @Override
    protected boolean test(Level level, Train train, CompoundTag context) {
        Ops operator = getOperator();
        int target = getThreshold();

        int foundEnergy = 0;
        for (Carriage carriage : train.carriages) {
            if(carriage.anyAvailableEntity() == null) continue;
            IEnergyStorage ies = PortableEnergyManager.get(carriage.anyAvailableEntity().getContraption());
            if(ies == null) continue;
            foundEnergy += ies.getEnergyStored();
        }

        requestStatusToUpdate(foundEnergy / 1000, context);
        return operator.test(foundEnergy, target * 1000);
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(
                CreateLang.translateDirect("schedule.condition.threshold.train_holds",
                        CreateLang.translateDirect("schedule.condition.threshold." + CreateLang.asId(getOperator().name()))),
                CreateLang.translateDirect("schedule.condition.threshold.x_units_of_item", getThreshold(),
                                Component.translatable("createaddition.schedule.condition.threshold.unit"),
                                Component.translatable("createaddition.schedule.condition.threshold.energy"))
                        .withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public ResourceLocation getId() {
        return CreateAddition.asResource("energy_threshold");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        super.initConfigurationWidgets(builder);
        builder.addSelectionScrollInput(71, 50, (i, l) -> {
            i.forOptions(ImmutableList.of(Component.translatable("createaddition.schedule.condition.threshold.unit")))
                    .titled(null);
        }, "Measure");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        int lastDisplaySnapshot = getLastDisplaySnapshot(tag);
        if (lastDisplaySnapshot == -1)
            return Component.empty();
        int offset = getOperator() == Ops.LESS ? -1 : getOperator() == Ops.GREATER ? 1 : 0;
        return CreateLang.translateDirect("schedule.condition.threshold.status", lastDisplaySnapshot,
                Math.max(0, getThreshold() + offset), Component.translatable("createaddition.schedule.condition.threshold.unit"));
    }

}