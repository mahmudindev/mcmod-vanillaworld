package com.github.mahmudindev.mcmod.vanillaworld.mixin;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
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
                    target = "Lnet/minecraft/core/HolderSet;contains(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private boolean matchesBiomeMask(
            HolderSet<Biome> instance,
            Holder<Biome> biomeHolder,
            Operation<Boolean> original
    ) {
        Boolean matched = original.call(instance, biomeHolder);
        if (!matched) {
            ResourceKey<Biome> resourceKey = biomeHolder.unwrapKey().orElse(null);
            if (resourceKey != null) {
                ResourceLocation resourceLocation = resourceKey.location();
                if (resourceLocation.getNamespace().equals(VanillaWorld.MOD_ID)) {
                    for (Holder<Biome> biomeHolderX : instance) {
                        ResourceKey<Biome> resourceKeyX = biomeHolderX.unwrapKey().orElse(null);
                        if (resourceKeyX == null) {
                            continue;
                        }

                        ResourceLocation resourceLocationX = resourceKeyX.location();
                        if (resourceLocation.getPath().equals(String.format(
                                "%s_%s",
                                resourceLocationX.getNamespace(),
                                resourceLocationX.getPath()
                        ))) {
                            matched = true;
                            break;
                        }
                    }
                }
            }
        }

        return matched;
    }
}
