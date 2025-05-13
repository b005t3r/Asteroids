package com.lazarecki.asteroids.audio.filters;

public class Volume implements Filter {
    private float volume;

    public Volume(float volume) {
        this.volume = volume;
    }

    @Override
    public float filter(float amplitude, float time) {
        return amplitude * volume;
    }
}
