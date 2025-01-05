package com.lazarecki.asteroids.engine.components.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ScoreCounterComponent implements Component, Pool.Poolable {
    public int score = -1;

    @Override
    public void reset() {
        score = -1;
    }
}
