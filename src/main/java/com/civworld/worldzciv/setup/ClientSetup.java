package com.civworld.worldzciv.setup;


import com.civworld.worldzciv.WorldZCiv;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WorldZCiv.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event){
        //EntityRenderers.register(MIEntity.SWITCH_ARROW.get(), SwitchArrowRenderer::new);

    }

    @SubscribeEvent
    public static void registerParticleTypes(RegisterParticleProvidersEvent event){
    }
}

