package com.civworld.worldzciv.civcodecs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.civworld.worldzciv.WorldZCiv.prefix;

public class BiomeStatsManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Map<ResourceKey<Biome>, Map<BiomeStats.StatType, Integer>> biomeStatsMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(BiomeStats.class, new BiomeStats.Deserializer()).create();
    public BiomeStatsManager() {
        super(GSON, prefix("biome_stats").getPath());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profile) {
        biomeStatsMap.clear();
        Map<ResourceKey<Biome>, Map<BiomeStats.StatType, Integer>> builder = new HashMap<>();

        jsonMap.forEach((resourceLocation, jsonElement) -> {
            try {
                BiomeStats biomeStats = GSON.fromJson(jsonElement, BiomeStats.class);
                if (biomeStats == null) {
                    LOGGER.error("biomeStats is null for resource {}", resourceLocation);
                    return;
                }

                biomeStats.biomeResourceValues.forEach((biome, integer) -> {
                    if (integer == null) {
                        LOGGER.error("Invalid value for biome {} in resource {}", biome, resourceLocation);
                        return;
                    }
                    builder.put(biome, integer);
                });

            } catch (Exception exception) {
                LOGGER.error("Couldn't parse biomeStats {} due to exception: {}", resourceLocation, exception.getMessage());
            }
        });

        biomeStatsMap.putAll(builder);
        if (biomeStatsMap == null || biomeStatsMap.isEmpty()) {
            LOGGER.error("No valid values found in map, make sure no illegal(value < 0) or duplicate values are in the biome stats");
            return;
        }
    }

    private static Optional<Integer> getValueFor(ResourceKey<Biome> biomeKey, BiomeStats.StatType statType) {
        try {
            return Optional.ofNullable(biomeStatsMap.get(biomeKey))
                    .map(map -> map.get(statType))
                    .filter(value -> value >= 0);
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting value for biome {} with key {}: {}", biomeKey.location(), statType, e.getMessage());
            return Optional.empty();
        }
    }

    public static int getHammersFor(ResourceKey<Biome> biomeKey) {
        return getValueFor(biomeKey, BiomeStats.StatType.HAMMERS).orElseGet(() -> {
            LOGGER.error("No hammer value found for biome {}", biomeKey.location());
            return 0;
        });
    }

    public static int getCultureFor(ResourceKey<Biome> biomeKey) {
        return getValueFor(biomeKey, BiomeStats.StatType.CULTURE).orElseGet(() -> {
            LOGGER.error("No culture value found for biome {}", biomeKey.location());
            return 0;
        });
    }

    public static int getBeakersFor(ResourceKey<Biome> biomeKey) {
        return getValueFor(biomeKey, BiomeStats.StatType.BEAKERS).orElseGet(() -> {
            LOGGER.error("No beaker value found for biome {}", biomeKey.location());
            return 0;
        });
    }
}
