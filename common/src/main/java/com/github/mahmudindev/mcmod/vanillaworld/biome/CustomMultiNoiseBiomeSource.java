package com.github.mahmudindev.mcmod.vanillaworld.biome;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;

import java.util.stream.Stream;

public class CustomMultiNoiseBiomeSource extends BiomeSource {
    public static final ResourceLocation ID = new ResourceLocation(
            VanillaWorld.MOD_ID,
            String.format("%s_%s", ResourceLocation.DEFAULT_NAMESPACE, "multi_noise")
    );
    public static final Codec<CustomMultiNoiseBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.mapEither(
                    Climate.ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("biomes"),
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable())
            ).forGetter(customMultiNoiseBiomeSource -> customMultiNoiseBiomeSource.parameters),
            Codec.BOOL.optionalFieldOf(
                    "override_biomes",
                    false
            ).forGetter(customMultiNoiseBiomeSource -> customMultiNoiseBiomeSource.overrideBiomes),
            RegistryOps.retrieveGetter(Registries.BIOME)
    ).apply(instance, CustomMultiNoiseBiomeSource::new));

    private final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;
    private final boolean overrideBiomes;
    private final HolderGetter<Biome> holderGetter;

    public CustomMultiNoiseBiomeSource(
            Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters,
            boolean overrideBiomes,
            HolderGetter<Biome> holderGetter
    ) {
        this.parameters = parameters ;
        this.overrideBiomes = overrideBiomes;
        this.holderGetter = holderGetter;
    }

    public Climate.ParameterList<Holder<Biome>> getParameters() {
        return this.parameters.map(
                parameterList -> parameterList,
                holder -> holder.value().parameters()
        );
    }

    private Holder<Biome> getVanillaBiomes(Holder<Biome> holder) {
        if (!this.overrideBiomes) {
            return holder;
        }

        ResourceKey<Biome> resourceKey = holder.unwrapKey().orElse(null);
        if (resourceKey == null) {
            return holder;
        }

        ResourceLocation resourceLocation = resourceKey.location();
        if (resourceLocation.getNamespace().equals(VanillaWorld.MOD_ID)) {
            return holder;
        }

        Holder.Reference<Biome> vanillaHolder = holderGetter.get(ResourceKey.create(
                Registries.BIOME,
                new ResourceLocation(VanillaWorld.MOD_ID, String.format(
                        "%s_%s",
                        resourceLocation.getNamespace(),
                        resourceLocation.getPath()
                ))
        )).orElse(null);
        if (vanillaHolder != null) {
            return vanillaHolder;
        }

        return holder;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.getParameters().values().stream().map(parameterPointHolderPair -> {
            return this.getVanillaBiomes(parameterPointHolderPair.getSecond());
        });
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return this.getVanillaBiomes(this.getParameters().findValue(sampler.sample(i, j, k)));
    }
}
