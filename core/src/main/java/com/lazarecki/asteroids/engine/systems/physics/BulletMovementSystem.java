package com.lazarecki.asteroids.engine.systems.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class BulletMovementSystem extends IteratingSystem {
    public BulletMovementSystem() {
        super(Family
                .all(BulletComponent.class, PositionComponent.class, LinearVelocityComponent.class)
                .get(),
            Constants.bulletMovementPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p = Mappers.position.get(entity);
        LinearVelocityComponent lv = Mappers.linearVel.get(entity);

        p.position.mulAdd(lv.velocity, deltaTime);
    }
}
