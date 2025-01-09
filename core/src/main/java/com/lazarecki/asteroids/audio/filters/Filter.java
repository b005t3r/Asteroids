package com.lazarecki.asteroids.audio.filters;

public interface Filter {
    float filter(float value, float time);
}
