package com.lazarecki.asteroids.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public final class Pools {
    public static final Pool<Vector2> vector2 = new Pool<>(32) {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
}
