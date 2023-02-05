package com.civworld.worldzciv.items;

import com.civworld.worldzciv.capabilities.CivChunkStats;
import com.civworld.worldzciv.capabilities.WZCCapabilities;
import com.civworld.worldzciv.managers.CivManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CivMaker  extends Item {
    public CivMaker(Properties pProperties) {
        super(pProperties);
    }
    private static final Logger LOGGER = LogUtils.getLogger();
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide || !(pPlayer instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));

        ServerLevel serverLevel = (ServerLevel) pLevel;
        CivManager civManager = CivManager.get(serverLevel);



        LazyOptional<CivChunkStats> civChunkStatsLazyOptional = serverLevel.getChunkAt(pPlayer.getOnPos()).getCapability(WZCCapabilities.CIV_CHUNK_STATS);
        Map<String, Integer> chunkStats = new HashMap<>();
        int radius = 4;
        ChunkPos chunkPos = serverPlayer.chunkPosition();

        for(int i = 0; i < radius*16; i = i + 16){
            for(int j = 0; j < radius*16; j = j + 16){
                BlockPos pos = serverPlayer.blockPosition().offset(i,0,j);
                chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
                civChunkStatsLazyOptional = serverLevel.getChunk(chunkPos.x, chunkPos.z).getCapability(WZCCapabilities.CIV_CHUNK_STATS);
                Map<String, Integer> getStats = CivChunkStats.getStats(civChunkStatsLazyOptional, serverLevel, chunkPos);
                LOGGER.info("stats " + getStats);
                getStats.forEach((string, integer) -> {
                    chunkStats.merge(string, integer,Integer::sum);
                });

                civChunkStatsLazyOptional.ifPresent(capability -> {
                    capability.civID = "CrazyCiv";
                    capability.wilderness = false;
                });
            }
        }

        LOGGER.info("chunkStats " + chunkStats);
        civManager.createCivWithStats("CrazyCiv", chunkStats.get("hammers"), chunkStats.get("beakers"), chunkStats.get("culture"));


        serverPlayer.getCapability(WZCCapabilities.CIV_PLAYER_STATS).ifPresent(capability ->
        {
            capability.hasCiv = true;
            capability.civId = "CrazyCiv";
        });


        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}
