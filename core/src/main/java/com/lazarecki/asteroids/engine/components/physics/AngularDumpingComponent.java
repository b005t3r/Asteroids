package com.lazarecki.asteroids.engine.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class AngularDumpingComponent implements Component, Pool.Poolable {
    public float dumping = Float.NaN;

    @Override
    public void reset() {
        dumping = Float.NaN;
    }
}
