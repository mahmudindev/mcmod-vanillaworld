package com.github.mahmudindev.mcmod.vanillaworld.packs;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorldExpectPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
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
                PackLocationInfo packLocation = new PackLocationInfo(
                        VanillaWorld.MOD_ID + "/" + path,
                        Component.translatable("Vanilla World: " + path.getFileName()),
                        PackSource.create(component -> component, false),
                        Optional.empty()
                );
                try (PathPackResources resources = new PathPackResources(packLocation, path)) {
                    Pack pack = Pack.readMetaAndCreate(
                            packLocation,
                            new Pack.ResourcesSupplier() {
                                @Override
                                public PackResources openPrimary(PackLocationInfo packLocationInfo) {
                                    return resources;
                                }

                                @Override
                                public PackResources openFull(
                                        PackLocationInfo packLocationInfo,
                                        Pack.Metadata metadata
                                ) {
                                    return resources;
                                }
                            },
                            PackType.SERVER_DATA,
                            new PackSelectionConfig(false, Pack.Position.TOP, false)
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
