package com.github.mahmudindev.mcmod.vanillaworld.fabric;

import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomMultiNoiseBiomeSource;
import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomTheEndBiomeSource;
import net.fabricmc.api.ModInitializer;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class VanillaWorldFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        VanillaWorld.init();

        Registry.register(
                BuiltInRegistries.BIOME_SOURCE,
                CustomMultiNoiseBiomeSource.ID,
                CustomMultiNoiseBiomeSource.CODEC
        );
        Registry.register(
                BuiltInRegistries.BIOME_SOURCE,
                CustomTheEndBiomeSource.ID,
                CustomTheEndBiomeSource.CODEC
        );
    }
}
