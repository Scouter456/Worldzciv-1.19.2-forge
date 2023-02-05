package com.civworld.worldzciv.setup;

import com.civworld.worldzciv.capabilities.WZCCapabilities;
import com.civworld.worldzciv.items.WZCItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.civworld.worldzciv.items.WZCItems.creativeTab;


public class Registration {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void init(){


        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        WZCItems.ITEMS.register(bus);

        WZCCapabilities.setupCapabilities();
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MysticalItemsConfig.CONFIG_BUILDER);


    }

    public static final Item.Properties defaultBuilder() {
        return new Item.Properties().tab(creativeTab);
    }


}
