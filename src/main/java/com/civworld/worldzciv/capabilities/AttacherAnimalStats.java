package com.civworld.worldzciv.capabilities;

import com.civworld.worldzciv.WorldZCiv;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

public class AttacherAnimalStats {
    private static class WZCCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(WorldZCiv.MODID, "animal_stats");
        private final AnimalStats backend = new AnimalStats();
        private final LazyOptional<AnimalStats> optionalData = LazyOptional.of(() -> backend);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return WZCCapabilities.ANIMAL_STATS.orEmpty(cap, this.optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }

    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(entity instanceof Animal) {
            final AttacherAnimalStats.WZCCapabilityProvider provider = new AttacherAnimalStats.WZCCapabilityProvider();
            event.addCapability(AttacherAnimalStats.WZCCapabilityProvider.IDENTIFIER, provider);
        }
    }
}
