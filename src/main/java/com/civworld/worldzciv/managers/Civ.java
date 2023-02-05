package com.civworld.worldzciv.managers;

import net.minecraft.nbt.CompoundTag;

public class Civ {

    private int hammer;
    private int beakers;
    private int culture;


    Civ(int hammers, int beakers, int culture) {
        this.hammer = hammers;
        this.beakers = beakers;
        this.culture = culture;
    }

    public CompoundTag deserializer(){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("hammers", this.hammer);
        nbt.putInt("beakers", this.beakers);
        nbt.putInt("culture", this.culture);
        return nbt;
    }

    public static Civ serializer(CompoundTag nbt){
        return new Civ(nbt.getInt("hammers"),nbt.getInt("beakers"),nbt.getInt("culture"));
    }

    public int getHammers(){
        return this.hammer;
    }

    public int getBeakers(){
        return this.beakers;
    }

    public int getCulture(){
        return this.culture;
    }

    public void addHammers(int addAmount){
        this.hammer = this.hammer + addAmount;
    }

    public void addBeakers(int addAmount){
        this.beakers = this.beakers + addAmount;
    }

    public void addCulture(int addAmount){
      this.culture = this.culture + addAmount;
    }
}

