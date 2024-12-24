package com.lazarecki.asteroids.engine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LateralAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LateralVelocityComponent;

public final class EngineUtils {
    public static Entity createShipEntity(Engine engine) {
        Entity entity = engine.createEntity();

        entity.addAndReturn(engine.createComponent(PositionComponent.class))
            .position.set(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);

        entity.addAndReturn(engine.createComponent(RotationComponent.class))
            .rotation = 0;

        entity.addAndReturn(engine.createComponent(ShapeComponent.class))
            .path.addAll(Constants.shipShapeTemplate);

        entity.addAndReturn(engine.createComponent(LateralVelocityComponent.class))
            .velocity.set(Vector2.Zero);

        entity.addAndReturn(engine.createComponent(AngularVelocityComponent.class))
            .velocity = 0;

        entity.addAndReturn(engine.createComponent(LateralAccelerationComponent.class))
            .acceleration.set(Vector2.Zero);

        entity.addAndReturn(engine.createComponent(AngularAccelerationComponent.class))
            .acceleration = 0;

        return entity;
    }
}
