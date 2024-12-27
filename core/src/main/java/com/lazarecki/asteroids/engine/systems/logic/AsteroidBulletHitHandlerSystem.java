package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletHitComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

import java.util.Map;

public class AsteroidBulletHitHandlerSystem extends IteratingSystem {
    private Vector2 tmpHitAngle = new Vector2();
    private Vector2 tmp = new Vector2();

    public AsteroidBulletHitHandlerSystem() {
        super(Family.all(
                AsteroidComponent.class, BulletHitComponent.class,
                PositionComponent.class, RotationComponent.class, ShapeComponent.class,
                LinearVelocityComponent.class, AngularVelocityComponent.class
            ).get(),
            Constants.asteroidBulletCollisionPriority
        );
    }

    @Override
    protected void processEntity(Entity asteroid, float deltaTime) {
        AsteroidComponent a = Mappers.asteroid.get(asteroid);

        if(a.asteroidType == Constants.AsteroidType.small) {
            getEngine().removeEntity(asteroid);
            return;
        }

        BulletHitComponent bh = Mappers.bulletHit.get(asteroid);
        PositionComponent p = Mappers.position.get(asteroid);
        RotationComponent r = Mappers.rotation.get(asteroid);
        ShapeComponent s = Mappers.shape.get(asteroid);
        LinearVelocityComponent lv = Mappers.linearVel.get(asteroid);
        AngularVelocityComponent av = Mappers.angularVel.get(asteroid);

        tmpHitAngle.set(Vector2.X).rotateRad(bh.angle);
        tmp.set(Vector2.Zero).sub(bh.hitLocation);

        float dist = tmp.len();
        float angleToCenter = tmp.angleRad();
        float angleFromCenter = bh.angle - angleToCenter;

        Constants.AsteroidType nextType = Constants.AsteroidType.values()[a.asteroidType.ordinal() + 1];

        // first fragment
        tmp.set(Vector2.X).rotateRad(angleToCenter).scl(dist).add(bh.hitLocation).add(p.position);
        Entity firstAsteroid = EngineUtils.createAsteroidEntity(nextType, getEngine());
        Mappers.position.get(firstAsteroid).position.set(tmp);
        Mappers.linearVel.get(firstAsteroid).velocity.set(lv.velocity).rotateRad(angleToCenter);
        Mappers.angularVel.get(firstAsteroid).velocity = av.velocity;

        // second fragment
        tmp.set(Vector2.X).rotateRad(angleFromCenter).scl(dist).add(bh.hitLocation).add(p.position);
        Entity secondAsteroid = EngineUtils.createAsteroidEntity(nextType, getEngine());
        Mappers.position.get(secondAsteroid).position.set(tmp);
        Mappers.linearVel.get(secondAsteroid).velocity.set(lv.velocity).rotateRad(angleFromCenter);
        Mappers.angularVel.get(secondAsteroid).velocity = -av.velocity;

        getEngine().removeEntity(asteroid);
        getEngine().addEntity(firstAsteroid);
        getEngine().addEntity(secondAsteroid);
    }
}
