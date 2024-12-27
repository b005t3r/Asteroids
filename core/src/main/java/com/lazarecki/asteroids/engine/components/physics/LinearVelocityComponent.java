package com.lazarecki.asteroids.engine.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class LinearVelocityComponent implements Component, Pool.Poolable {
    public final Vector2 velocity = new Vector2(Float.NaN, Float.NaN);

    @Override
    public void reset() {
        velocity.set(Float.NaN, Float.NaN);
    }
}
