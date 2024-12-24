package com.lazarecki.asteroids.engine.components.location;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class RotationComponent implements Component, Pool.Poolable {
    public float rotation = Float.NaN;

    @Override
    public void reset() {
        rotation = Float.NaN;
    }
}
