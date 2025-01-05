package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.collision.ProcessedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.logic.DebrisComponent;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearDumpingComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class ShipCollisionHandlerSystem extends IteratingSystem {
    public ShipCollisionHandlerSystem() {
        super(Family.all(
                ShipComponent.class,
                PositionComponent.class, RotationComponent.class, ShapeComponent.class,
                LinearVelocityComponent.class, ProcessedCollisionComponent.class
            ).get(),
            Constants.shipAsteroidCollisionPriority
        );
    }

    @Override
    protected void processEntity(Entity ship, float deltaTime) {
        final Engine engine = getEngine();

        PositionComponent position = Mappers.position.get(ship);
        RotationComponent rotation = Mappers.rotation.get(ship);
        ShapeComponent shape = Mappers.shape.get(ship);
        LinearVelocityComponent linVel = Mappers.linearVel.get(ship);

        int pieceCount = shape.path.size;

        for(int i = 0; i < pieceCount; ++i) {
            Vector2 start = new Vector2(shape.path.get(i));
            Vector2 end = new Vector2(shape.path.get((i + 1) % pieceCount));

            Entity debris = engine.createEntity();
            debris.add(engine.createComponent(DebrisComponent.class));
            debris.addAndReturn(engine.createComponent(PositionComponent.class))
                .position.set(position.position);
            debris.addAndReturn(engine.createComponent(RotationComponent.class))
                .rotation = rotation.rotation;
            debris.addAndReturn(engine.createComponent(BoundingRadiusComponent.class))
                .radius = Math.max(start.dst(Vector2.Zero), end.dst(Vector2.Zero));
            debris.addAndReturn(engine.createComponent(LinearVelocityComponent.class))
                .velocity.set(linVel.velocity);
            debris.addAndReturn(engine.createComponent(LinearDumpingComponent.class))
                .dumping = Constants.shipLinearDumping;
            debris.addAndReturn(engine.createComponent(AngularVelocityComponent.class))
                .velocity = 0.35f * MathUtils.random(Constants.shipMaxClockwiseAngularVelocity, Constants.shipMaxCounterClockwiseAngularVelocity);
            ShapeComponent debrisShape = debris.addAndReturn(engine.createComponent(ShapeComponent.class));
            debrisShape.path.add(start);
            debrisShape.path.add(end);

            engine.addEntity(debris);
        }

        engine.removeEntity(ship);
    }
}
