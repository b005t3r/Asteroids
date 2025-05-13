package com.lazarecki.asteroids.audio.oscilators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.lazarecki.asteroids.audio.filters.Envelope;

public class Slider implements Oscillator {
    private Oscillator oscillator;

    private float rate;

    public Slider(Oscillator oscillator, float rate) {
        this.oscillator = oscillator;
        this.rate = rate;
    }

    @Override
    public float oscillate(float time) {
        float freq = oscillator.oscillate(time);
        return (float) (freq * Math.pow(2, rate * time));
        //return envelope.filter(1.0f, time) * oscillator.oscillate(time);
    }
}
