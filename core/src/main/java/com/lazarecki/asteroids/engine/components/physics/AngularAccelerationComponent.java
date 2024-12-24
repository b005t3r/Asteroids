package com.lazarecki.asteroids.engine.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class AngularAccelerationComponent implements Component, Pool.Poolable {
    public float acceleration = Float.NaN;

    @Override
    public void reset() {
        acceleration = Float.NaN;
    }
}
