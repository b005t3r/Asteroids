package com.lazarecki.asteroids.engine.components.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class BulletHitComponent implements Component, Pool.Poolable {
    public final Vector2 hitLocation = new Vector2(Float.NaN, Float.NaN);
    public float angle = Float.NaN;

    @Override
    public void reset() {
        hitLocation.set(Float.NaN, Float.NaN);
        angle = Float.NaN;
    }
}
