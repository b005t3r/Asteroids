package com.lazarecki.asteroids.engine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.*;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletComponent;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.*;

public final class EngineUtils {
    public static Entity createShipEntity(Engine engine) {
        Entity entity = engine.createEntity();

        entity.add(engine.createComponent(ShipComponent.class));

        entity.addAndReturn(engine.createComponent(PositionComponent.class))
            .position.set(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);

        entity.addAndReturn(engine.createComponent(RotationComponent.class))
            .rotation = MathUtils.degreesToRadians * 90.0f;

        entity.addAndReturn(engine.createComponent(ShapeComponent.class))
            .path.addAll(Constants.shipShapeTemplate);

        entity.addAndReturn(engine.createComponent(BoundingRadiusComponent.class))
            .radius = getBoundingRadius(Constants.shipShapeTemplate);

        entity.addAndReturn(engine.createComponent(LinearVelocityComponent.class))
            .velocity.set(Vector2.Zero);

        entity.addAndReturn(engine.createComponent(AngularVelocityComponent.class))
            .velocity = 0;

        entity.addAndReturn(engine.createComponent(LinearAccelerationComponent.class))
            .acceleration = 0;

        entity.addAndReturn(engine.createComponent(AngularAccelerationComponent.class))
            .acceleration = 0;

        entity.addAndReturn(engine.createComponent(LinearDumpingComponent.class))
            .dumping = Constants.shipLinearDumping;

        entity.addAndReturn(engine.createComponent(AngularDumpingComponent.class))
            .dumping = Constants.shipAngularDumping;

        return entity;
    }

    public static Entity createAsteroidEntity(Constants.AsteroidType asteroidType, Engine engine) {
        Array<Array<Vector2>> allTemplates = Constants.asteroidTemplates.get(asteroidType);
        Array<Vector2> template = allTemplates.get(MathUtils.random(0, allTemplates.size - 1));

        Entity entity = engine.createEntity();
        entity.add(engine.createComponent(AsteroidComponent.class));

        entity.addAndReturn(engine.createComponent(PositionComponent.class))
            .position.set(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);

        entity.addAndReturn(engine.createComponent(RotationComponent.class))
            .rotation = MathUtils.random(MathUtils.PI2);

        entity.addAndReturn(engine.createComponent(ShapeComponent.class))
            .path.addAll(template);

        entity.addAndReturn(engine.createComponent(BoundingRadiusComponent.class))
            .radius = getBoundingRadius(template);

        entity.addAndReturn(engine.createComponent(LinearVelocityComponent.class))
            .velocity.set(Vector2.Zero);

        entity.addAndReturn(engine.createComponent(AngularVelocityComponent.class))
            .velocity = 0;

        return entity;
    }

    private static Vector2 spawnTmpVec = new Vector2();

    public static void spawnAsteroid(Entity asteroid, Engine engine) {
        PositionComponent p         = Mappers.position.get(asteroid);
        LinearVelocityComponent lv  = Mappers.linearVel.get(asteroid);
        AngularVelocityComponent av = Mappers.angularVel.get(asteroid);

        spawnTmpVec.set(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);
        p.position
            .set(Constants.gameWidth * 1.2f, Constants.gameHeight * 0.5f)
            .rotateAroundRad(spawnTmpVec, MathUtils.random(MathUtils.PI2));

        spawnTmpVec.set(
            MathUtils.random(Constants.gameWidth * 0.1f, Constants.gameWidth * 0.9f),
            MathUtils.random(Constants.gameHeight * 0.1f, Constants.gameHeight * 0.9f)
        );

        float angle = spawnTmpVec.sub(p.position).angleRad();

        lv.velocity
            .set(Vector2.X)
            .scl(MathUtils.random(Constants.asteroidMinLinearVelocity, Constants.asteroidMaxLinearVelocity))
            .rotateRad(angle);

        av.velocity = MathUtils.random(
            Constants.asteroidMaxClockwiseAngularVelocity,
            Constants.asteroidMaxCounterClockwiseAngularVelocity
        );

        // it's "teleported" out of bounds
        asteroid.add(engine.createComponent(TeleportingComponent.class));
    }

    private static final Vector2 boundsTmpVec = new Vector2();

    public static Rectangle getBoundingBox(Array<Vector2> shape, Vector2 position, float rotation, Rectangle result) {
        result.set(position.x, position.y, 0, 0);

        for(Vector2 v : shape) {
            boundsTmpVec.set(v).rotateRad(rotation).add(position);

            result.merge(boundsTmpVec);
        }

        return result;
    }

    public static float getBoundingRadius(Array<Vector2> shape) {
        // assume shape is build around (0, 0)

        float r = 0;

        for(Vector2 v : shape)
            r = Math.max(v.len(), r);

        return r;
    }

    private static final Vector2 s1p1 = new Vector2();
    private static final Vector2 s1p2 = new Vector2();
    private static final Vector2 s2p1 = new Vector2();
    private static final Vector2 s2p2 = new Vector2();
    public static boolean collides(Array<Vector2> s1, Vector2 p1, float r1, Array<Vector2> s2, Vector2 p2, float r2) {
        if(s1.size == 0 || s2.size == 0)
            return false;

        s1p1.set(s1.get(0)).rotateRad(r1).add(p1).sub(p2);
        s2p1.set(s2.get(0)).rotateRad(r2).add(p2).sub(p1);

        if(Intersector.isPointInPolygon(s1, s2p1) || Intersector.isPointInPolygon(s2, s1p1))
            return true;

        for(int i = 0; i < s1.size; ++i) {
            s1p1.set(s1.get(i)).rotateRad(r1).add(p1);
            s1p2.set(s1.get((i + 1) % s1.size)).rotateRad(r1).add(p1);

            for(int j = 0; j < s2.size; ++j) {
                s2p1.set(s2.get(j)).rotateRad(r2).add(p2);
                s2p2.set(s2.get((j + 1) % s2.size)).rotateRad(r2).add(p2);

                if(Intersector.intersectSegments(s1p1, s1p2, s2p1, s2p2, null))
                    return true;
            }
        }

        return false;
    }

    private static final Vector2 p1p2Nor = new Vector2();
    private static final Vector2 p2p1Nor = new Vector2();
    private static final Vector2 p1p2v1 = new Vector2();
    private static final Vector2 p2p1v2 = new Vector2();
    private static final Vector2 p1p2v1New = new Vector2();
    private static final Vector2 p2p1v2New = new Vector2();

    public static void elasticCirclesCollision(Vector2 p1, float r1, Vector2 v1, Vector2 p2, float r2, Vector2 v2) {
        p1p2Nor.set(p2).sub(p1).nor();
        p2p1Nor.set(p1).sub(p2).nor();

        p1p2v1.set(p1p2Nor).scl(p1p2Nor.dot(v1));
        p2p1v2.set(p2p1Nor).scl(p2p1Nor.dot(v2));

        float m1 = MathUtils.PI * r1 * r1;
        float m2 = MathUtils.PI * r2 * r2;
        float tm = m1 + m2;

        p1p2v1New.set(
            (p1p2v1.x * (m1 - m2) + (2 * m2 * p2p1v2.x)) / tm,
            (p1p2v1.y * (m1 - m2) + (2 * m2 * p2p1v2.y)) / tm
        );

        p2p1v2New.set(
            (p2p1v2.x * (m2 - m1) + (2 * m1 * p1p2v1.x)) / tm,
            (p2p1v2.y * (m2 - m1) + (2 * m1 * p1p2v1.y)) / tm
        );

        v1.sub(p1p2v1).add(p1p2v1New);
        v2.sub(p2p1v2).add(p2p1v2New);
    }

    public static void spawnBullet(Vector2 p, float r, Engine engine) {
        Entity bullet = engine.createEntity();
        bullet.add(engine.createComponent(BulletComponent.class));
        bullet.addAndReturn(engine.createComponent(PositionComponent.class))
            .position.set(p);
        bullet.addAndReturn(engine.createComponent(LinearVelocityComponent.class))
            .velocity.set(Vector2.X).rotateRad(r).scl(Constants.bulletMaxLinearVelocity);

        engine.addEntity(bullet);
    }
}
