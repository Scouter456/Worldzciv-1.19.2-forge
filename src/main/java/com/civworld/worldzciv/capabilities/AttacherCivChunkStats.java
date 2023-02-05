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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

public class AttacherCivChunkStats {

    private static class WZCCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(WorldZCiv.MODID, "civ_chunk_stats");
        private final CivChunkStats backend = new CivChunkStats();
        private final LazyOptional<CivChunkStats> optionalData = LazyOptional.of(() -> backend);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return WZCCapabilities.CIV_CHUNK_STATS.orEmpty(cap, this.optionalData);
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

    public static void attach(final AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk levelChunk = event.getObject();
        final WZCCapabilityProvider provider = new WZCCapabilityProvider();
        event.addCapability(WZCCapabilityProvider.IDENTIFIER, provider);
    }
}
