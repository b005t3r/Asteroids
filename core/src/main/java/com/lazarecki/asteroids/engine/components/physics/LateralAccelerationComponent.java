package com.lazarecki.asteroids.engine.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class LateralAccelerationComponent implements Component, Pool.Poolable {
    public Vector2 acceleration = new Vector2(Float.NaN, Float.NaN);

    @Override
    public void reset() {
        acceleration.set(Float.NaN, Float.NaN);
    }
}
