package com.lazarecki.asteroids.engine.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

// collision to be processed this step
public class DetectedCollisionComponent implements Component, Pool.Poolable {
    public final Array<Entity> collisions = new Array<>(32);

    @Override
    public void reset() {
        collisions.clear();
    }
}
