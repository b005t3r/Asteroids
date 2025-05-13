package com.lazarecki.asteroids.audio.filters;

public class CutOff implements Filter {
    private float limit;

    public CutOff(float limit) {
        this.limit = limit;
    }

    @Override
    public float filter(float amplitude, float time) {
        if(amplitude > limit)
            return limit;
        else if(amplitude < -limit)
            return -limit;

        return amplitude;
    }
}
