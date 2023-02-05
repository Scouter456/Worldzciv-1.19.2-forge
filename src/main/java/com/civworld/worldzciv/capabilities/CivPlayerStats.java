package com.civworld.worldzciv.capabilities;

import com.civworld.worldzciv.civcodecs.Civ;
import com.civworld.worldzciv.civcodecs.CivManager;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import org.slf4j.Logger;

public class CivPlayerStats implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public boolean hasCiv = false;
    public String civId = "empty";
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("hasCiv", this.hasCiv);
        nbt.putString("civId", this.civId);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.hasCiv = nbt.getBoolean("hasCiv");
        this.civId = nbt.getString("civId");
    }

    public static void getCiv(TickEvent.PlayerTickEvent event){
        if(event.player.level.isClientSide || !(event.player instanceof ServerPlayer serverPlayer) || (event.player.level.getGameTime() & 200) != 0) return;
        CivManager civManager = CivManager.get(event.player.level);

        LazyOptional<CivPlayerStats> capabilityOptional = serverPlayer.getCapability(WZCCapabilities.CIV_PLAYER_STATS);
        capabilityOptional.ifPresent(capability ->{
            if(capability.civId == "empty") return;
            Civ civ = civManager.getCiv(capability.civId);
            if(civ == null)
            {
                return;
            }LOGGER.info("civs " + civ);

            serverPlayer.sendSystemMessage(Component.literal("Your civ has " + civ.getBeakers() + " Beakers"));
            serverPlayer.sendSystemMessage(Component.literal("Your civ has " + civ.getHammers() + " Hammers"));
            serverPlayer.sendSystemMessage(Component.literal("Your civ has " + civ.getCulture() + " Culture"));
               });

    }
}
