package com.lazarecki.asteroids.engine.components.location;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {
    public final Vector2 position = new Vector2(Float.NaN, Float.NaN);

    @Override
    public void reset() {
        position.set(Float.NaN, Float.NaN);
    }
}
