package com.civworld.worldzciv.managers;

import com.civworld.worldzciv.WorldZCiv;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BiomeStats {
    private static final Logger LOGGER = LogUtils.getLogger();
    public Map<ResourceKey<Biome>, Map<StatType, Integer>> biomeResourceValues = new HashMap<>();

    public BiomeStats(Map<ResourceKey<Biome>, Map<StatType, Integer>> holderMap) {
        biomeResourceValues.putAll(holderMap);
    }

    public enum StatType {
        HAMMERS, BEAKERS, CULTURE
    }

    public static class Deserializer implements JsonDeserializer<BiomeStats> {

        @Override
        public BiomeStats deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<ResourceKey<Biome>, Map<StatType, Integer>> biomeResourceValues = new HashMap<>();

            JsonArray json = jsonElement.getAsJsonArray();
            for (int i = 0; i < json.size(); i++) {
                Map<StatType, Integer> stats = new HashMap<>();
                JsonObject object = json.get(i).getAsJsonObject();
                JsonElement statsArrayElement = object.get("stats");

                if (statsArrayElement == null) {
                    LOGGER.error("No stats array found, skipping");
                    continue;
                }

                JsonObject statsArray = statsArrayElement.getAsJsonArray().get(0).getAsJsonObject();

                ResourceKey<Biome> biomeHolder = ForgeRegistries.BIOMES.getHolder(new ResourceLocation(object.get("biomes").getAsString())).get().unwrapKey().get();

                if (biomeResourceValues.containsKey(biomeHolder)) {
                    LOGGER.error("[" + WorldZCiv.MODID.toUpperCase() + "]" + ": Biome with key " + biomeHolder.location() + " is a duplicate, skipping");
                    continue;
                }

                int beakers = statsArray.get("beakers").getAsInt();
                int culture = statsArray.get("culture").getAsInt();
                int hammers = statsArray.get("hammers").getAsInt();

                if (beakers < 0 || culture < 0 || hammers < 0) {
                    LOGGER.error("Value for " + biomeHolder.location() + " is negative, skipping!");
                    continue;
                }

                stats.put(StatType.HAMMERS, hammers);
                stats.put(StatType.BEAKERS, beakers);
                stats.put(StatType.CULTURE, culture);

                biomeResourceValues.put(biomeHolder, stats);
            }
            return new BiomeStats(biomeResourceValues);
        }
    }
}
