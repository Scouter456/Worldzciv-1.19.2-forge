package com.civworld.worldzciv;

import com.civworld.worldzciv.events.ClientEvents;
import com.civworld.worldzciv.events.ForgeEvents;
import com.civworld.worldzciv.setup.ClientSetup;
import com.civworld.worldzciv.setup.ModSetup;
import com.civworld.worldzciv.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import java.util.Locale;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WorldZCiv.MODID)
public class WorldZCiv
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "worldzciv";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldZCiv()
    {
        Registration.init();
        ModSetup.setup();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(ClientEvents.class);
            // static method with no client-only classes in method signature
        }
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);


        GeckoLib.initialize();

    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }
}
