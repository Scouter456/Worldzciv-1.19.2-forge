package com.civworld.worldzciv.capabilities;
/**
 * The following code falls under GNU Lesser General Public License v3.0
 *
 * @Author TelepathicGrunt
 * Taken from https://github.com/TelepathicGrunt/Bumblezone/blob/1.19.2-Forge/src/main/java/com/telepathicgrunt/the_bumblezone/capabilities/BzCapabilities.java
 */

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class WZCCapabilities {

    public static final Capability<CivChunkStats> CIV_CHUNK_STATS = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<CivPlayerStats> CIV_PLAYER_STATS = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<AnimalStats> ANIMAL_STATS = CapabilityManager.get(new CapabilityToken<>() {
    });
    private WZCCapabilities() {}
    public static void setupCapabilities() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(WZCCapabilities::registerCapabilities);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addGenericListener(LevelChunk.class, AttacherCivChunkStats::attach);
        forgeBus.addGenericListener(Entity.class, AttacherCivPlayerStats::attach);
        forgeBus.addGenericListener(Entity.class, AttacherAnimalStats::attach);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(CivChunkStats.class);
        event.register(CivPlayerStats.class);
        event.register(AnimalStats.class);
    }
}
