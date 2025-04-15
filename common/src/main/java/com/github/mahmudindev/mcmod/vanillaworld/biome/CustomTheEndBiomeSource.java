package com.github.mahmudindev.mcmod.vanillaworld.biome;

import com.github.mahmudindev.mcmod.vanillaworld.VanillaWorld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.stream.Stream;

public class CustomTheEndBiomeSource extends BiomeSource {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            VanillaWorld.MOD_ID,
            String.format("%s_%s", ResourceLocation.DEFAULT_NAMESPACE, "the_end")
    );
    public static final MapCodec<CustomTheEndBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryOps.retrieveGetter(Registries.BIOME),
            Codec.BOOL.optionalFieldOf(
                    "override_biomes",
                    false
            ).forGetter(customTheEndBiomeSource -> customTheEndBiomeSource.overrideBiomes)
    ).apply(i, i.stable(CustomTheEndBiomeSource::new)));

    private final Holder<Biome> theEnd;
    private final Holder<Biome> endHighlands;
    private final Holder<Biome> endMidlands;
    private final Holder<Biome> smallEndIslands;
    private final Holder<Biome> endBarrens;
    private final boolean overrideBiomes;
    private final HolderGetter<Biome> holderGetter;

    public CustomTheEndBiomeSource(
            HolderGetter<Biome> holderGetter,
            boolean overrideBiomes
    ) {
        this.theEnd = holderGetter.getOrThrow(Biomes.THE_END);
        this.endHighlands = holderGetter.getOrThrow(Biomes.END_HIGHLANDS);
        this.endMidlands = holderGetter.getOrThrow(Biomes.END_MIDLANDS);
        this.smallEndIslands = holderGetter.getOrThrow(Biomes.SMALL_END_ISLANDS);
        this.endBarrens = holderGetter.getOrThrow(Biomes.END_BARRENS);
        this.overrideBiomes = overrideBiomes;
        this.holderGetter = holderGetter;
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
                ResourceLocation.fromNamespaceAndPath(VanillaWorld.MOD_ID, String.format(
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
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(
                this.theEnd,
                this.endHighlands,
                this.endMidlands,
                this.smallEndIslands,
                this.endBarrens
        ).map(this::getVanillaBiomes);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        int l = QuartPos.toBlock(i);
        int m = QuartPos.toBlock(j);
        int n = QuartPos.toBlock(k);
        int o = SectionPos.blockToSectionCoord(l);
        int p = SectionPos.blockToSectionCoord(n);
        if ((long) o * (long) o + (long) p * (long) p <= 4096L) {
            return this.getVanillaBiomes(this.theEnd);
        } else {
            int q = (SectionPos.blockToSectionCoord(l) * 2 + 1) * 8;
            int r = (SectionPos.blockToSectionCoord(n) * 2 + 1) * 8;
            double d = sampler.erosion().compute(new DensityFunction.SinglePointContext(q, m, r));
            if (d > 0.25) {
                return this.getVanillaBiomes(this.endHighlands);
            } else if (d >= -0.0625) {
                return this.getVanillaBiomes(this.endMidlands);
            } else if (d < -0.21875) {
                return this.getVanillaBiomes(this.smallEndIslands);
            } else {
                return this.getVanillaBiomes(this.endBarrens);
            }
        }
    }
}
