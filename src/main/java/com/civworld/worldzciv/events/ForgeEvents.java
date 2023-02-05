package com.civworld.worldzciv.events;

import com.civworld.worldzciv.WorldZCiv;
import com.civworld.worldzciv.civcodecs.BiomeStatsManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = WorldZCiv.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event){
        event.addListener(new BiomeStatsManager());
    }
}

