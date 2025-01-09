package com.lazarecki.asteroids.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.lazarecki.asteroids.audio.filters.Filter;
import com.lazarecki.asteroids.audio.oscilators.Oscilator;

public class Synthesizer {
    public static int calculateSampleSize(float duration, int sampleRate, int channelCount) {
        return ((int) Math.floor(sampleRate * duration)) * channelCount;
    }

    private int samplingRate;
    private int channelCount;
    private Oscilator oscilator;
    private Array<Filter> filters = new Array<>(true, 32, Filter.class);

    public Synthesizer(Oscilator oscilator, int samplingRate, int channelCount) {
        this.samplingRate = samplingRate;
        this.channelCount = channelCount;
        this.oscilator = oscilator;
    }

    public Synthesizer addFilter(Filter filter) {
        filters.add(filter);

        return this;
    }

    public ShortArray synthesize(float frequency, float startTime, float endTime, ShortArray result) {
        int sampleSize = calculateSampleSize(endTime - startTime, samplingRate, channelCount);

        if(result == null)
            result = new ShortArray(sampleSize);

        result.setSize(sampleSize);

        final float dt = 1.0f / samplingRate;
        float totalTime = 0.0f;
        for(int i = 0; i < sampleSize; i += channelCount) {
            float value = oscilator.produce(frequency, startTime + totalTime);

            for(int f = 0; f < filters.size; ++f)
                value = filters.items[f].filter(value, totalTime);

            short shortValue = (short) MathUtils.lerp(Short.MIN_VALUE, Short.MAX_VALUE, (value + 1) * 0.5f);

            for(int c = 0; c < channelCount; ++c)
                result.items[i + c] = shortValue;

            totalTime += dt;
        }

        return result;
    }

}
