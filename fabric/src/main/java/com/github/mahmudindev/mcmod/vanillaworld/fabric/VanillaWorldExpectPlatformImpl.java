package com.github.mahmudindev.mcmod.vanillaworld.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

public class VanillaWorldExpectPlatformImpl {
    public static Path getResource(String modId, String path) {
        FabricLoader loader = FabricLoader.getInstance();

        ModContainer modContainer = loader.getModContainer(modId).orElse(null);
        if (modContainer == null) {
            return null;
        }

        return modContainer.findPath(path).orElse(null);
    }
}
