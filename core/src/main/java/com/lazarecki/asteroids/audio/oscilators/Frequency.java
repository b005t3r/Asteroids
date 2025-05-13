package com.lazarecki.asteroids.audio.oscilators;

import com.badlogic.gdx.utils.Pool;

public class Frequency implements Oscillator {
    public float frequency = Float.NaN;

    public Frequency(float frequency) {
        this.frequency = frequency;
    }

    @Override
    public float oscillate(float time) {
        return frequency;
    }
}
