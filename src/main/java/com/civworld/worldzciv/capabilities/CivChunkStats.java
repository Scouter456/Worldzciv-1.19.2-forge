package com.civworld.worldzciv.capabilities;

import com.civworld.worldzciv.civcodecs.BiomeStatsManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CivChunkStats implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public int hammers = 0;
    public int beakers = 0;
    public int culture = 0;
    public String civID = "empty_id";

    public boolean statsCalculted = false;
    public boolean wilderness = true;
    private HashMap<ResourceKey<Biome>, Integer> biomeCountInChunk = new HashMap<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("hammers", this.hammers);
        nbt.putInt("beakers", this.beakers);
        nbt.putInt("culture", this.culture);
        nbt.putBoolean("statsCalculated", this.statsCalculted);
        nbt.putBoolean("wilderness", this.wilderness);
        nbt.putString("civId", this.civID);

        // Serialize biomeCountInChunk map
        ListTag biomeCountInChunkList = new ListTag();
        for (Map.Entry<ResourceKey<Biome>, Integer> entry : biomeCountInChunk.entrySet()) {
            CompoundTag biomeCountInChunkEntry = new CompoundTag();
            biomeCountInChunkEntry.putString("biomeKey", entry.getKey().toString());
            biomeCountInChunkEntry.putInt("count", entry.getValue());
            biomeCountInChunkList.add(biomeCountInChunkEntry);
        }
        nbt.put("biomeCountInChunk", biomeCountInChunkList);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.hammers = nbt.getInt("hammers");
        this.beakers = nbt.getInt("beakers");
        this.culture = nbt.getInt("culture");
        this.statsCalculted = nbt.getBoolean("statsCalculated");
        this.wilderness = nbt.getBoolean("wilderness");
        this.civID = nbt.getString("civId");

        // Deserialize biomeCountInChunk map
        ListTag biomeCountInChunkList = nbt.getList("biomeCountInChunk", 10);
        for (int i = 0; i < biomeCountInChunkList.size(); i++) {
            CompoundTag biomeCountInChunkEntry = biomeCountInChunkList.getCompound(i);
            ResourceKey<Biome> biomeKey = ForgeRegistries.BIOMES.getHolder(new ResourceLocation(biomeCountInChunkEntry.getString("biomeKey"))).get().unwrapKey().get();
            int count = biomeCountInChunkEntry.getInt("count");
            biomeCountInChunk.put(biomeKey, count);
        }
    }

    public static void tellStats(TickEvent.PlayerTickEvent event){
        if(event.player.level.isClientSide || !(event.player instanceof ServerPlayer serverPlayer) || (event.player.level.getGameTime() & 100) != 0 || event.phase == TickEvent.Phase.START) return;
        Level level = event.player.level;
        LevelChunk levelChunk = level.getChunkAt(serverPlayer.blockPosition());
        //calculateStats(level, levelChunk.getPos());
        LazyOptional<CivChunkStats> civChunkStatsLazyOptional = levelChunk.getCapability(WZCCapabilities.CIV_CHUNK_STATS);
        civChunkStatsLazyOptional.ifPresent(capability -> {
            serverPlayer.sendSystemMessage(Component.literal("There are " + capability.hammers + " Hammers in this chunk"));
            serverPlayer.sendSystemMessage(Component.literal("There are " + capability.beakers + " Beakers in this chunk"));
            serverPlayer.sendSystemMessage(Component.literal("There is " + capability.culture + " Culture in this chunk"));
            serverPlayer.sendSystemMessage(Component.literal("The chunk has the following biomes " + capability.biomeCountInChunk));
            serverPlayer.sendSystemMessage(Component.literal("This chunk is owned by " + capability.civID));
            serverPlayer.sendSystemMessage(Component.literal("This chunk is wilderness " + capability.wilderness));
        });

        if(serverPlayer.getLevel().getBlockState(serverPlayer.blockPosition().below()).is(Blocks.DIAMOND_BLOCK)){
            reCalculateStats(civChunkStatsLazyOptional);
        }
    }

    public static void reCalculateStats(LazyOptional<CivChunkStats> civChunkStatsLazyOptional){
        civChunkStatsLazyOptional.ifPresent(capability -> {
            capability.statsCalculted = false;
            capability.hammers = 0;
            capability.culture = 0;
            capability.beakers = 0;
        });

    }


    public static void preventBreaking(BlockEvent.BreakEvent event){
        if(event.getPlayer().level.isClientSide || !(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;
        ServerLevel level = serverPlayer.getLevel();
        LevelChunk levelChunk = level.getChunkAt(serverPlayer.blockPosition());


    }

    public static Map<String, Integer>  getStats(LazyOptional<CivChunkStats> civChunkStatsLazyOptional, Level level, ChunkPos chunkPos){
        Map<String, Integer> statsMap = new HashMap<>();
        civChunkStatsLazyOptional.ifPresent(capability -> {
            if(!capability.statsCalculted){
                calculateStats(level, chunkPos);
            }
            statsMap.put("hammers", capability.hammers);
            statsMap.put("beakers", capability.beakers);
            statsMap.put("culture", capability.culture);
            return;
        });
        if(statsMap.isEmpty()){
            LOGGER.error("No data present in chunk");
        }
        return statsMap;
    }

    public static void calculateStats(Level level, ChunkPos pos){
        if(level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        LevelChunk levelChunk = serverLevel.getChunk(pos.x, pos.z);
        if (levelChunk == null || !levelChunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
            LOGGER.error("Error: Invalid chunk status. Expected status >= " + ChunkStatus.FULL + ", but got " + levelChunk.getStatus());
            return;
        }

        LazyOptional<CivChunkStats> civChunkStatsLazyOptional = levelChunk.getCapability(WZCCapabilities.CIV_CHUNK_STATS);
        civChunkStatsLazyOptional.ifPresent(capability -> {
            if(capability.statsCalculted == true){
                return;
            }
            Map<ResourceKey<Biome>, Integer> biomeStatsMap;
            try {
                biomeStatsMap = determineBiomes(level, pos);
            } catch (Exception e) {
                LOGGER.error("Error: Failed to determine biomes for chunk at " + pos + " due to " + e.getMessage());
                return;
            }

            capability.biomeCountInChunk.putAll(biomeStatsMap);
            for(ResourceKey<Biome> biome : biomeStatsMap.keySet()){
                try {
                    capability.hammers = capability.hammers + determineHammerValues(biome, biomeStatsMap.get(biome));
                    capability.beakers = capability.beakers + determineBeakerValues(biome, biomeStatsMap.get(biome));
                    capability.culture = capability.culture + determineCultureValues(biome, biomeStatsMap.get(biome));
                } catch (Exception e) {
                    LOGGER.error("Error: Failed to calculate values for biome " + biome + " due to " + e.getMessage());
                }
            }

            capability.statsCalculted = true;
        });


    }
    public static int determineHammerValues(ResourceKey<Biome> biome, Integer amount){
        return BiomeStatsManager.getHammersFor(biome) * amount;
    }

    public static int determineBeakerValues(ResourceKey<Biome> biome, Integer amount){
        return BiomeStatsManager.getBeakersFor(biome) * amount;
    }
    public static int determineCultureValues(ResourceKey<Biome> biome, Integer amount){
        return BiomeStatsManager.getCultureFor(biome) * amount;
    }
    public static Map<ResourceKey<Biome>, Integer> determineBiomes(LevelAccessor levelAccessor, ChunkPos chunkPos) {
        Map<ResourceKey<Biome>, Integer> biomeCounts = new HashMap<>();
        try{
            BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), levelAccessor.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ()) ,chunkPos.getMinBlockZ());
            for(int x = 0; x < 16; x ++){
                for(int z = 0; z < 16; z++){
                    //levelAccessor.setBlock(blockPos.offset(x,0,z), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                    ResourceKey<Biome> biome = levelAccessor.getBiome(blockPos.offset(x,0,z)).unwrapKey().get();
                    biomeCounts.merge(biome, 1, Integer::sum);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error determining biomes: " + e.getMessage());
        }
        return biomeCounts;
    }
}
