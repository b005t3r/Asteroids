package com.lazarecki.asteroids.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.location.*;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.*;

public final class EngineUtils {
    public static Entity createShipEntity(Engine engine) {
        Entity entity = engine.createEntity();

        entity.add(engine.createComponent(ShipComponent.class));

        entity.addAndReturn(engine.createComponent(PositionComponent.class))
            .position.set(Constants.gameWidth * 0.5f, Constants.gameHeight * 0.5f);

        entity.addAndReturn(engine.createComponent(RotationComponent.class))
            .rotation = 0;

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
            .dumping = Constants.linearDumping;

        entity.addAndReturn(engine.createComponent(AngularDumpingComponent.class))
            .dumping = Constants.angularDumping;

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

    private static ComponentMapper<PositionComponent> posMapper            = ComponentMapper.getFor(PositionComponent.class);
    private static ComponentMapper<LinearVelocityComponent> linVelMapper   = ComponentMapper.getFor(LinearVelocityComponent.class);
    private static ComponentMapper<AngularVelocityComponent> angVelMapper  = ComponentMapper.getFor(AngularVelocityComponent.class);

    private static Vector2 spawnTmpVec = new Vector2();

    public static void spawnAsteroid(Entity asteroid, Engine engine) {
        PositionComponent p         = posMapper.get(asteroid);
        LinearVelocityComponent lv  = linVelMapper.get(asteroid);
        AngularVelocityComponent av = angVelMapper.get(asteroid);

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
            .scl(MathUtils.random(Constants.maxLinearVelocity * 0.05f, Constants.maxLinearVelocity * 0.25f))
            .rotateRad(angle);

        av.velocity = MathUtils.random(
            Constants.maxClockwiseAngularVelocity * 0.25f,
            Constants.maxCounterClockwiseAngularVelocity * 0.25f
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

        s1p1.set(s1.get(0)).rotateRad(r1).add(p1);
        s2p1.set(s2.get(0)).rotateRad(r2).add(p2);

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
}
