package com.civworld.worldzciv.items;

import com.civworld.worldzciv.WorldZCiv;
import com.civworld.worldzciv.setup.Registration;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WZCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WorldZCiv.MODID);

    public static CreativeModeTab creativeTab = new CreativeModeTab("worldzciv") {

        public static final RegistryObject<Item> CIVMAKER = ITEMS.register("civmaker", () -> new CivMaker(Registration.defaultBuilder().fireResistant()));

        public static final RegistryObject<Item> CIVSTATADDER = ITEMS.register("civstatadder", () -> new CivStatAdder(Registration.defaultBuilder().fireResistant()));


        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.SAND);
        }
    };
}
