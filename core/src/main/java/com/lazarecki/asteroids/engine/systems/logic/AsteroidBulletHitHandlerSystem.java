package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletHitComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;
import com.lazarecki.asteroids.utils.Pools;

public class AsteroidBulletHitHandlerSystem extends IteratingSystem {
    public AsteroidBulletHitHandlerSystem() {
        super(Family.all(
                AsteroidComponent.class, BulletHitComponent.class,
                PositionComponent.class, BoundingRadiusComponent.class,
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

        Constants.AsteroidType nextAsteroidType = Constants.AsteroidType.values()[a.asteroidType.ordinal() + 1];
        BulletHitComponent asteroidBulletHit = Mappers.bulletHit.get(asteroid);
        PositionComponent asteroidPosition = Mappers.position.get(asteroid);
        BoundingRadiusComponent asteroidRadius = Mappers.boundingRadius.get(asteroid);
        LinearVelocityComponent asteroidLinVel = Mappers.linearVel.get(asteroid);
        AngularVelocityComponent asteroidAngVel = Mappers.angularVel.get(asteroid);

        final float splitAngle = MathUtils.HALF_PI * 0.35f;
        final float splitDist = asteroidRadius.radius * 1.1f;

        Vector2 bulletAngle = Pools.vector2.obtain();
        Vector2 fromHitAngle = Pools.vector2.obtain();
        Vector2 firstFragmentCenter = Pools.vector2.obtain();
        Vector2 secondFragmentCenter = Pools.vector2.obtain();
        Vector2 firstFragmentLinVel = Pools.vector2.obtain();
        Vector2 secondFragmentLinVel = Pools.vector2.obtain();

        try {
            fromHitAngle.set(asteroidPosition.position).sub(asteroidBulletHit.hitLocation).nor();
            bulletAngle.set(Vector2.X).rotateRad(asteroidBulletHit.angle);
            float energyTransferRatio = Math.abs(bulletAngle.dot(fromHitAngle));

            firstFragmentCenter
                .set(fromHitAngle)
                .rotateRad(splitAngle)
                .scl(splitDist)
                .add(asteroidPosition.position);

            secondFragmentCenter
                .set(fromHitAngle)
                .rotateRad(-splitAngle)
                .scl(splitDist)
                .add(asteroidPosition.position);

            float fragmentMass = asteroidRadius.radius * asteroidRadius.radius;
            float bulletRadius = Constants.AsteroidType.small.radius * 0.75f;
            float bulletMass = bulletRadius * bulletRadius;
            float bulletLinVelTransfer = (bulletMass / (2 * fragmentMass)) * energyTransferRatio * Constants.bulletMaxLinearVelocity;

            firstFragmentLinVel
                .set(fromHitAngle)
                .rotateRad(splitAngle * energyTransferRatio)
                .scl(bulletLinVelTransfer)
                .add(asteroidLinVel.velocity);

            secondFragmentLinVel
                .set(fromHitAngle)
                .rotateRad(-splitAngle * energyTransferRatio)
                .scl(bulletLinVelTransfer)
                .add(asteroidLinVel.velocity);

            // first fragment
            Entity firstAsteroid = EngineUtils.createAsteroidEntity(nextAsteroidType, getEngine());
            Mappers.position.get(firstAsteroid).position.set(firstFragmentCenter);
            Mappers.linearVel.get(firstAsteroid).velocity.set(firstFragmentLinVel);
            Mappers.angularVel.get(firstAsteroid).velocity = -asteroidAngVel.velocity;

            // second fragment
            Entity secondAsteroid = EngineUtils.createAsteroidEntity(nextAsteroidType, getEngine());
            Mappers.position.get(secondAsteroid).position.set(secondFragmentCenter);
            Mappers.linearVel.get(secondAsteroid).velocity.set(secondFragmentLinVel);
            Mappers.angularVel.get(secondAsteroid).velocity = asteroidAngVel.velocity;

            getEngine().removeEntity(asteroid);
            getEngine().addEntity(firstAsteroid);
            getEngine().addEntity(secondAsteroid);
        }
        finally {
            Pools.vector2.free(fromHitAngle);
            Pools.vector2.free(bulletAngle);
            Pools.vector2.free(firstFragmentCenter);
            Pools.vector2.free(secondFragmentCenter);
            Pools.vector2.free(firstFragmentLinVel);
            Pools.vector2.free(secondFragmentLinVel);
        }
    }
}
