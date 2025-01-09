package com.lazarecki.asteroids.audio.oscilators;

import com.badlogic.gdx.math.MathUtils;

public final class Oscilators {
    public static final float hertzToRad = MathUtils.PI2;

    public static final Oscilator sine = (frequency, time) -> MathUtils.sin(frequency * hertzToRad * time);
    public static final Oscilator square = (frequency, time) -> Math.copySign(1.0f, MathUtils.sin(frequency * hertzToRad * time));
}
