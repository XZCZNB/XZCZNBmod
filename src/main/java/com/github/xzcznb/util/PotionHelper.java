package com.github.xzcznb.util;

public class PotionHelper {

    public static class PotionResult {
        public final int amplifier;
        public final int duration;

        public PotionResult(int amplifier, int duration) {
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }

    public static PotionResult potionCalculation(int oldDuration, int oldAmplifier, int newDuration, int newAmplifier) {
        float oldLevel = oldAmplifier + 1;
        float newLevel = newAmplifier + 1;
        int resultAmplifier;
        float resultDuration;
        if (oldLevel >= newLevel) {
            resultAmplifier = oldAmplifier;
            resultDuration = oldDuration + newLevel * newDuration / oldLevel;
        } else {
            resultAmplifier = newAmplifier;
            resultDuration = newDuration + oldLevel * oldDuration / newLevel;
        }
        return new PotionResult(resultAmplifier, (int) resultDuration);
    }
}
