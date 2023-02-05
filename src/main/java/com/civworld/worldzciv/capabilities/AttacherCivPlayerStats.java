package com.civworld.worldzciv.capabilities;
/**
 * The following code falls under GNU Lesser General Public License v3.0
 *
 * @Author TelepathicGrunt
 * Taken from https://github.com/TelepathicGrunt/Bumblezone/blob/1.19.2-Forge/src/main/java/com/telepathicgrunt/the_bumblezone/capabilities
 */

import com.civworld.worldzciv.WorldZCiv;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

public class AttacherCivPlayerStats {

    private static class WZCCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(WorldZCiv.MODID, "civ_player_stats");
        private final CivPlayerStats backend = new CivPlayerStats();
        private final LazyOptional<CivPlayerStats> optionalData = LazyOptional.of(() -> backend);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return WZCCapabilities.CIV_PLAYER_STATS.orEmpty(cap, this.optionalData);
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
        if (entity instanceof ServerPlayer) {
            final WZCCapabilityProvider provider = new WZCCapabilityProvider();
            event.addCapability(WZCCapabilityProvider.IDENTIFIER, provider);
        }
    }
}
