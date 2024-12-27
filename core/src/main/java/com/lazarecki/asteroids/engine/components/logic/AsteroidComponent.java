package com.lazarecki.asteroids.engine.components.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.lazarecki.asteroids.Constants;

public class AsteroidComponent implements Component, Pool.Poolable {
    public Constants.AsteroidType asteroidType = null;

    @Override
    public void reset() {
        asteroidType = null;
    }
}
