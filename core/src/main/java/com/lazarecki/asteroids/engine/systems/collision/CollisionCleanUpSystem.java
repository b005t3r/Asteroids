package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.collision.CollisionComponent;

public class CollisionCleanUpSystem extends IteratingSystem {
    public CollisionCleanUpSystem() {
        super(Family.all(CollisionComponent.class).get(), Constants.collisionCleanUpSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        entity.remove(CollisionComponent.class);
    }
}
