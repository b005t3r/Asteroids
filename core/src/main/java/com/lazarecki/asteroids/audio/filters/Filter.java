package com.lazarecki.asteroids.audio.filters;

public interface Filter {
    float filter(float amplitude, float time);
}
