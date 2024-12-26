package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class MovementSystem extends IteratingSystem {
    private Vector2 tmp = new Vector2();

    public MovementSystem() {
        super(Family.all(
                PositionComponent.class, RotationComponent.class,
                LinearVelocityComponent.class, AngularVelocityComponent.class
            ).get(),
            Constants.movementSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LinearVelocityComponent lv  = Mappers.linearVel.get(entity);
        AngularVelocityComponent av = Mappers.angularVel.get(entity);
        PositionComponent p         = Mappers.position.get(entity);
        RotationComponent r         = Mappers.rotation.get(entity);

        tmp.set(lv.velocity);
        tmp.scl(deltaTime);

        p.position.add(tmp);
        r.rotation += av.velocity * deltaTime;
    }
}
