package com.github.mahmudindev.mcmod.vanillaworld.forge;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;

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
