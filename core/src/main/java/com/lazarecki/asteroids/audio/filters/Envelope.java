package com.lazarecki.asteroids.audio.filters;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public class Envelope implements Filter {
    private final FloatArray periods = new FloatArray(10);
    private final FloatArray levels = new FloatArray(10);
    private final Array<Interpolation> interpolations = new Array<>(true, 10, Interpolation.class);

    private float initialLevel;

    public Envelope(float initialLevel) {
        this.initialLevel = initialLevel;
    }

    public Envelope addStep(float duration, float level, Interpolation interpolation) {
        periods.add(duration);
        levels.add(level);
        interpolations.add(interpolation);

        return this;
    }

    @Override
    public float filter(float value, float time) {
        if(time < 0)
            return initialLevel * value;
        else if(time >= periods.items[periods.size - 1])
            return levels.items[levels.size - 1];

        // find the correct period
        float periodStartLevel = initialLevel;
        float periodStartTime = 0.0f;
        float periodEndLevel = periods.items[0];
        float periodEndTime = periods.items[0];
        Interpolation interpolation = interpolations.items[0];

        for(int i = 0; i < periods.size; ++i) {
            float period = periods.items[i];

            if(time > period) {
                periodStartLevel = levels.items[i];
                periodStartTime = period;
                continue;
            }

            periodEndLevel = levels.items[i];
            periodEndTime = period;
            interpolation = interpolations.items[i];
            break;
        }

        return value * interpolation.apply(
            periodStartLevel, periodEndLevel,
            (time - periodStartTime) / (periodEndTime - periodStartTime)
        );
    }
}
