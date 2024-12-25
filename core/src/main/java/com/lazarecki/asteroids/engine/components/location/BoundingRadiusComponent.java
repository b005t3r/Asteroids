package com.lazarecki.asteroids.engine.components.location;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BoundingRadiusComponent implements Component, Pool.Poolable {
    public float radius = Float.NaN;

    @Override
    public void reset() {
        radius = Float.NaN;
    }
}
