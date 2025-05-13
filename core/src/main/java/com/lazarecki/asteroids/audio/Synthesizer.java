package com.lazarecki.asteroids.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.lazarecki.asteroids.audio.filters.Filter;
import com.lazarecki.asteroids.audio.oscilators.Oscillator;
import com.lazarecki.asteroids.audio.waveforms.Waveform;

public class Synthesizer {
    public static int calculateSampleSize(float duration, int sampleRate, int channelCount) {
        return ((int) Math.floor(sampleRate * duration)) * channelCount;
    }

    private int sampleRate;
    private int channelCount;
    private Oscillator oscillator;
    private Waveform waveform;
    private Array<Filter> filters = new Array<>(true, 32, Filter.class);

    public Synthesizer(Oscillator oscillator, Waveform waveform, int sampleRate, int channelCount) {
        this.oscillator = oscillator;
        this.waveform = waveform;
        this.sampleRate = sampleRate;
        this.channelCount = channelCount;
    }

    public Synthesizer addFilter(Filter filter) {
        filters.add(filter);

        return this;
    }

    public ShortArray synthesize(float startTime, float endTime, ShortArray result) {
        int sampleSize = calculateSampleSize(endTime - startTime, sampleRate, channelCount);

        if(result == null)
            result = new ShortArray(sampleSize);

        result.setSize(sampleSize);

        final float dt = 1.0f / sampleRate;
        float totalTime = 0.0f;
        for(int i = 0; i < sampleSize; i += channelCount) {
            float frequency = oscillator.oscillate(startTime + totalTime);

            float amplitude = waveform.produce(frequency, startTime + totalTime);

            for(int f = 0; f < filters.size; ++f)
                amplitude = filters.items[f].filter(amplitude, totalTime);

            short shortValue = (short) MathUtils.lerp(Short.MIN_VALUE, Short.MAX_VALUE, (amplitude + 1) * 0.5f);

            for(int c = 0; c < channelCount; ++c)
                result.items[i + c] = shortValue;

            totalTime += dt;
        }

        return result;
    }

}
