package io.github.amerebagatelle.mods.nuit.components;

import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UtilsTest {
    @Test
    public void testCalculateInterpolatedAlpha() {
        Assertions.assertEquals(0.5F, Utils.calculateInterpolatedAlpha(15000, 24000, 12000, 18000, 0F, 1F));
        Assertions.assertEquals(1F, Utils.calculateInterpolatedAlpha(15000, 24000, 12000, 18000, 1F, 1F));
        Assertions.assertEquals(0F, Utils.calculateInterpolatedAlpha(15000, 24000, 12000, 18000, 0F, 0F));
        Assertions.assertEquals(0.5F, Utils.calculateInterpolatedAlpha(0, 24000, 22000, 2000, 0F, 1F));
        Assertions.assertEquals(0.25F, Utils.calculateInterpolatedAlpha(1000, 24000, 22000, 2000, 1F, 0F));
        Assertions.assertEquals(0.75F, Utils.calculateInterpolatedAlpha(1000, 24000, 22000, 2000, 0F, 1F));
        Assertions.assertEquals(0.5F, Utils.calculateInterpolatedAlpha(3000, 24000, 22000, 8000, 0F, 1F));
    }

    @Test
    public void testFindClosestKeyframes() {
        Map<Long, Float> keyFrames = new HashMap<>();
        keyFrames.put(1000L, 0.1f);
        keyFrames.put(2000L, 0.2f);
        keyFrames.put(3000L, 0.3f);
        keyFrames.put(4000L, 0.4f);

        // Test when currentTime is exactly at a keyframe
        Tuple<Long, Long> result = Utils.findClosestKeyframes(keyFrames, 2000L);
        Assertions.assertEquals(2000L, result.getA());
        Assertions.assertEquals(3000L, result.getB());

        // Test when currentTime is between keyframes
        result = Utils.findClosestKeyframes(keyFrames, 2500L);
        Assertions.assertEquals(2000L, result.getA());
        Assertions.assertEquals(3000L, result.getB());

        // Test when currentTime is before the first keyframe
        result = Utils.findClosestKeyframes(keyFrames, 500L);
        Assertions.assertEquals(4000L, result.getA());
        Assertions.assertEquals(1000L, result.getB());

        // Test when currentTime is after the last keyframe
        result = Utils.findClosestKeyframes(keyFrames, 4500L);
        Assertions.assertEquals(4000L, result.getA());
        Assertions.assertEquals(1000L, result.getB());

        // Test with no keyframes
        result = Utils.findClosestKeyframes(new HashMap<>(), 2500L);
        Assertions.assertNull(result);

        // Test with one keyframe
        keyFrames.clear();
        keyFrames.put(1000L, 0.1f);
        result = Utils.findClosestKeyframes(keyFrames, 2500L);
        Assertions.assertEquals(1000L, result.getA());
        Assertions.assertEquals(1000L, result.getB());
    }
}
