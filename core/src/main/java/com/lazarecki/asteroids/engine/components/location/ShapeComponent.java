package com.lazarecki.asteroids.engine.components.location;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ShapeComponent implements Component, Pool.Poolable {
    public final Array<Vector2> path = new Array<>();

    @Override
    public void reset() {
        path.clear();
    }
}
