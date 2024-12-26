package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletComponent;

public class BulletCleanUpSystem extends IteratingSystem {
    private Vector2 center = new Vector2(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);

    public BulletCleanUpSystem() {
        super(Family.all(BulletComponent.class, PositionComponent.class).get(), Constants.bulletCleanUpPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(Mappers.position.get(entity).position.dst(center) < 2 * Math.max(Constants.gameWidth, Constants.gameHeight))
            return;

        getEngine().removeEntity(entity);
    }
}
