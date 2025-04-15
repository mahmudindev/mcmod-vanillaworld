package com.github.mahmudindev.mcmod.vanillaworld.neoforge;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;

public class VanillaWorldExpectPlatformImpl {
    public static Path getResource(String modId, String path) {
        ModList modList = ModList.get();
        if (modList == null) {
            return null;
        }

        ModContainer modContainer = modList.getModContainerById(modId).orElse(null);
        if (modContainer == null) {
            return null;
        }

        IModFile modFile = modContainer.getModInfo().getOwningFile().getFile();

        return modFile.findResource(path);
    }
}
