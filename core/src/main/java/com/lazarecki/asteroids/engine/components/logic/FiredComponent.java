package com.lazarecki.asteroids.engine.components.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class FiredComponent implements Component, Pool.Poolable {
    public float cooldown = Float.NaN;

    @Override
    public void reset() {
        cooldown = Float.NaN;
    }
}
