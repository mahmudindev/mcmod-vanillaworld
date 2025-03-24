package com.github.mahmudindev.mcmod.vanillaworld.packs;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorldExpectPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DataPack {
    public static void loadPacks(Consumer<Pack> consumer) {
        Path resourcesPath = VanillaWorldExpectPlatform.getResource(
                VanillaWorld.MOD_ID,
                "packs"
        );
        if (resourcesPath == null) {
            return;
        }

        try (Stream<Path> walk = Files.list(resourcesPath)) {
            walk.forEach(path -> {
                try (PathPackResources resources = new PathPackResources(
                        VanillaWorld.MOD_ID + "/packs/" + path.getFileName(),
                        path,
                        true
                )) {
                    Pack pack = Pack.readMetaAndCreate(
                            resources.packId(),
                            Component.translatable("Vanilla World: " + path.getFileName()),
                            false,
                            ignored -> resources,
                            PackType.SERVER_DATA,
                            Pack.Position.TOP,
                            PackSource.create(component -> component, false)
                    );
                    if (pack == null) {
                        return;
                    }

                    consumer.accept(pack);
                }
            });
        } catch (IOException e) {
            VanillaWorld.LOGGER.error("Failed to load packs file", e);
        }
    }
}
