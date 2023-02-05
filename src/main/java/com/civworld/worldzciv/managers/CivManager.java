package com.civworld.worldzciv.managers;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CivManager extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public Map<String, Civ> civMap= new HashMap<>();





    public static CivManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        // Get the civ manager if it already exists. Otherwise create a new one. Note that both
        return storage.computeIfAbsent(CivManager::new, CivManager::new, "civmanager");
    }

    public Civ getCiv(String name){
        return civMap.get(name);
    }
    private void createCiv(String name, Civ object){
        civMap.put(name, object);
        setDirty();
    }

    public void createCiv(String name){
        createCiv(name, new Civ(1,1,1));
    }

    public void createCivWithObject(String name,Civ civ){
        createCiv(name, civ);
    }

    public void createCivWithStats(String name, int hammers, int beakers, int culture){
        createCiv(name, new Civ(hammers,beakers,culture));
    }
 // public void addHammers(int amount, String name){
 //     getCiv(name).addHammers(amount);
 //     setDirty();
 // }

 // public void addBeakers(int amount, String name){
 //     getCiv(name).addBeakers(amount);
 //     setDirty();
 // }
 //
 // public void addCulture(int amount, String name){
 //     getCiv(name).addCulture(amount);
 //     setDirty();
 // }

    public CivManager(){
    }

    public CivManager(CompoundTag nbt){
        ListTag saveDataList = nbt.getList("saveDataList", 10);
        for (int i = 0; i < saveDataList.size(); i++) {
            CompoundTag saveDataEntry = saveDataList.getCompound(i);
            String civName = saveDataEntry.getString("civName");
            Civ civ = Civ.serializer(saveDataEntry.getCompound("data"+civName));
            civMap.put(civName, civ);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        //CompoundTag saveData = new CompoundTag();
        ListTag saveDataList = new ListTag();
        for (Map.Entry<String, Civ> entry : civMap.entrySet()) {
            CompoundTag saveData = new CompoundTag();
            saveData.putString("civName", entry.getKey().toString());
            saveData.put("data"+entry.getKey(), entry.getValue().deserializer());
            saveDataList.add(saveData);
        }
        nbt.put("saveDataList", saveDataList);
        return nbt;
    }
}
