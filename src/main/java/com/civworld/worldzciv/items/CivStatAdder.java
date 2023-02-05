package com.civworld.worldzciv.items;

import com.civworld.worldzciv.managers.Civ;
import com.civworld.worldzciv.managers.CivManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CivStatAdder extends Item {
    public CivStatAdder(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide || !(pPlayer instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));

        ServerLevel serverLevel = (ServerLevel) pLevel;



        CivManager civManager = CivManager.get(serverLevel);
        Civ civ = civManager.getCiv("CrazyCiv");
        civ.addBeakers(10);
        civ.addHammers(10);
        civ.addCulture(2);

        //Dont forget to setDirty otherwise it wont save
        civManager.setDirty();
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}
