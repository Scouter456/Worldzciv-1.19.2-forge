package com.civworld.worldzciv.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class AnimalStats implements INBTSerializable<CompoundTag> {

    private long milkTimer = 0;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putLong("milkTimer", this.milkTimer);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
          this.milkTimer = nbt.getLong("milkTimer");
    }

    public static void setMilkTimer(PlayerInteractEvent.EntityInteractSpecific event){
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof ServerPlayer player) || !(event.getTarget() instanceof Cow cow)) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        Long currentTime = serverLevel.getGameTime();
        LazyOptional<AnimalStats> cowStatsLazyOptional = cow.getCapability(WZCCapabilities.ANIMAL_STATS);
        cowStatsLazyOptional.ifPresent(capability ->{
            Long milkedTime = capability.milkTimer;
            //Todo translatable and perhaps make the cow timer a config option
            if( milkedTime > currentTime - 72000){
                int minutesLeft = (int)Math.ceil(((72000 - (currentTime - milkedTime))/ 72000f) * 60f);
                player.sendSystemMessage(Component.literal("Cannot milk this cow for " + minutesLeft));
                event.setCanceled(true);
                return;
            }
            capability.milkTimer = milkedTime;
        });


    }
}
