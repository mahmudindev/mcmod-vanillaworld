package com.github.mahmudindev.mcmod.vanillaworld.mixin;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(SurfaceRules.BiomeConditionSource.class)
public abstract class SurfaceRulesMixin {
    @Shadow @Final private List<ResourceKey<Biome>> biomes;
    @Shadow @Final @Mutable Predicate<ResourceKey<Biome>> biomeNameTest;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(List<ResourceKey<Biome>> list, CallbackInfo ci) {
        Set<ResourceKey<Biome>> set = Set.copyOf(this.biomes.stream().map(resourceKey -> {
            ResourceLocation resourceLocation = resourceKey.location();
            return ResourceKey.create(Registries.BIOME, new ResourceLocation(
                    VanillaWorld.MOD_ID,
                    String.format(
                            "%s_%s",
                            resourceLocation.getNamespace(),
                            resourceLocation.getPath()
                    )
            ));
        }).toList());
        Predicate<ResourceKey<Biome>> biomeNameTest = this.biomeNameTest;
        this.biomeNameTest = resourceKey -> {
            if (biomeNameTest.test(resourceKey)) {
                return true;
            }

            return set.contains(resourceKey);
        };
    }
}
