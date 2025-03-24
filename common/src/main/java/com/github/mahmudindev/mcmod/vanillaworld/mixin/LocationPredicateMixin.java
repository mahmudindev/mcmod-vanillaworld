package com.github.mahmudindev.mcmod.vanillaworld.mixin;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocationPredicate.class)
public abstract class LocationPredicateMixin {
    @WrapOperation(
            method = "matches",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/resources/ResourceKey;)Z"
            )
    )
    private boolean matchesBiomeMask(
            Holder<Biome> instance,
            ResourceKey<Biome> resourceKey,
            Operation<Boolean> original
    ) {
        ResourceKey<Biome> resourceKeyX = instance.unwrapKey().orElse(null);
        if (resourceKeyX != null) {
            ResourceLocation resourceLocationX = resourceKeyX.location();
            if (resourceLocationX.getNamespace().equals(VanillaWorld.MOD_ID)) {
                ResourceLocation resourceLocation = resourceKey.location();
                if (resourceLocationX.getPath().equals(String.format(
                        "%s_%s",
                        resourceLocation.getNamespace(),
                        resourceLocation.getPath()
                ))) {
                    return true;
                }
            }
        }

        return original.call(instance, resourceKey);
    }
}
