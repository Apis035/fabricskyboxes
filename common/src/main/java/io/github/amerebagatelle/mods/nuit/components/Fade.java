package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;

import java.util.Map;

public class Fade {
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn),
            CodecUtils.getClampedLong(1, Long.MAX_VALUE).optionalFieldOf("duration", 24000L).forGetter(Fade::getDuration),
            CodecUtils.unboundedMapFixed(Long.class, CodecUtils.getClampedFloat(0F, 1F), Long2FloatOpenHashMap::new)
                    .optionalFieldOf("keyFrames", CodecUtils.fastUtilLong2FloatOpenHashMap())
                    .forGetter(Fade::getKeyFrames)
    ).apply(instance, Fade::new));
    private final boolean alwaysOn;
    private final long duration;

    private final Map<Long, Float> keyFrames;

    public Fade(boolean alwaysOn, long duration, Map<Long, Float> keyFrames) {
        this.alwaysOn = alwaysOn || keyFrames.isEmpty();
        this.duration = duration;
        this.keyFrames = keyFrames;
        this.validateKeyFrames();
    }

    private void validateKeyFrames() {
        // Validate that there is at least 1 keyframe if alwaysOn is false
        if (this.keyFrames.isEmpty() && !this.alwaysOn) {
            throw new IllegalArgumentException("Keyframes must have at least 1 entries");
        }

        // Validate that the keyframes are between 0 and the duration
        for (Long keyFrame : this.keyFrames.keySet()) {
            try {
                if (keyFrame < 0 || keyFrame >= this.duration) {
                    throw new IllegalArgumentException("Keyframes must be between 0 and duration");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Keyframes must be numeric", e);
            }
        }
    }

    public boolean isAlwaysOn() {
        return this.alwaysOn;
    }

    public long getDuration() {
        return this.duration;
    }

    public Map<Long, Float> getKeyFrames() {
        return this.keyFrames;
    }

    public static Fade of() {
        return new Fade(true, 24000L, CodecUtils.fastUtilLong2FloatOpenHashMap());
    }
}