package com.lazarecki.asteroids.audio.oscilators;

import com.badlogic.gdx.Gdx;
import com.lazarecki.asteroids.audio.waveforms.Waveforms;

public class Vibrato implements Oscillator {
    private Oscillator oscillator;

    private float frequency;
    private float amplitude;

    public Vibrato(Oscillator oscillator, float frequency, float amplitude) {
        this.oscillator = oscillator;
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    @Override
    public float oscillate(float time) {
//        Gdx.app.log("Vibrato", String.valueOf(Waveforms.sine.produce(frequency, time)));

        return oscillator.oscillate(time) * (1.0f + Waveforms.sine.produce(frequency, time) * amplitude);
    }
}
