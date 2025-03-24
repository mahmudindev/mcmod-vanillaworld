package com.github.mahmudindev.mcmod.vanillaworld;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class VanillaWorldExpectPlatform {
    @ExpectPlatform
    public static Path getResource(String modId, String path) {
        return null;
    }
}
