package com.github.mahmudindev.mcmod.vanillaworld.forge;

import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomMultiNoiseBiomeSource;
import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomTheEndBiomeSource;
import com.github.mahmudindev.mcmod.vanillaworld.packs.DataPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.common.Mod;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(VanillaWorld.MOD_ID)
public final class VanillaWorldForge {
    public VanillaWorldForge() {
        // Run our common setup.
        VanillaWorld.init();

        // When using the constructor, it crashes on the recommended v47.3.0
        //noinspection removal
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();

        context.getModEventBus().addListener((RegisterEvent event) -> {
            event.register(Registries.BIOME_SOURCE, helper -> {
                helper.register(
                        CustomMultiNoiseBiomeSource.ID,
                        CustomMultiNoiseBiomeSource.CODEC
                );
                helper.register(
                        CustomTheEndBiomeSource.ID,
                        CustomTheEndBiomeSource.CODEC
                );
            });
        });

        context.getModEventBus().addListener((AddPackFindersEvent event) -> {
            if (event.getPackType() != PackType.SERVER_DATA) {
                return;
            }

            event.addRepositorySource(DataPack::loadPacks);
        });
    }
}
