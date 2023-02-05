package com.civworld.worldzciv.events;

import com.civworld.worldzciv.WorldZCiv;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = WorldZCiv.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

}
