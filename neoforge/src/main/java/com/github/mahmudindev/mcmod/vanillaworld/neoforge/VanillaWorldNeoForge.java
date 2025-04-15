package com.github.mahmudindev.mcmod.vanillaworld.neoforge;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomMultiNoiseBiomeSource;
import com.github.mahmudindev.mcmod.vanillaworld.biome.CustomTheEndBiomeSource;
import com.github.mahmudindev.mcmod.vanillaworld.packs.DataPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(VanillaWorld.MOD_ID)
public final class VanillaWorldNeoForge {
    public VanillaWorldNeoForge(IEventBus eventBus) {
        // Run our common setup.
        VanillaWorld.init();

        eventBus.addListener((RegisterEvent event) -> {
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

        eventBus.addListener((AddPackFindersEvent event) -> {
            if (event.getPackType() != PackType.SERVER_DATA) {
                return;
            }

            event.addRepositorySource(DataPack::loadPacks);
        });
    }
}
