package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;

public class RunawayAsteroidCleanUpSystem extends IteratingSystem {
    public RunawayAsteroidCleanUpSystem() {
        super(
            Family.all(AsteroidComponent.class, PositionComponent.class).get(),
            Constants.runawayAsteroidCleanUpPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.position.get(entity);

        final float maxDist = Math.max(Constants.gameWidth, Constants.gameHeight) * 0.7f;
        if(position.position.dst(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f) < maxDist)
            return;

        getEngine().removeEntity(entity);
    }
}
