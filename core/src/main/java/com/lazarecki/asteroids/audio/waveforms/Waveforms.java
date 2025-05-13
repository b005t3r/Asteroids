package com.lazarecki.asteroids.audio.waveforms;

import com.badlogic.gdx.math.MathUtils;

public final class Waveforms {
    public static final float hertzToRad = MathUtils.PI2;

    public static final Waveform sine = (frequency, time) -> MathUtils.sin(frequency * hertzToRad * time);
    public static final Waveform square = (frequency, time) -> Math.copySign(1.0f, MathUtils.sin(frequency * hertzToRad * time));
}
