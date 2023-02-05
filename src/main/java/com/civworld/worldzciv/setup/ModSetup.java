package com.civworld.worldzciv.setup;

import com.civworld.worldzciv.WorldZCiv;
import com.civworld.worldzciv.capabilities.CivChunkStats;
import com.civworld.worldzciv.capabilities.CivPlayerStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = WorldZCiv.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
        });
    }

    public static void setup(){
        IEventBus bus = MinecraftForge.EVENT_BUS;
        civStats(bus);
    }



    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event){

    }

    public static void civStats(IEventBus bus){
        bus.addListener(EventPriority.HIGH, CivChunkStats::tellStats);
        bus.addListener(EventPriority.HIGH, CivPlayerStats::getCiv);
    }
}
