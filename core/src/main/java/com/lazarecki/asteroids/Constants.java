package com.lazarecki.asteroids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public final class Constants {
    // general
    public static final int gameWidth       = 20;
    public static final int gameHeight      = 15;
    public static final float lineWidth     = 0.07f; // 0.0333f;
    public static final Color lineColor     = new Color(0.75f, 0.75f, 1.0f, 1.0f);

    // system priorities
    public static final int asteroidSpawnerPriority             = 100;
    public static final int bulletCooldownPriority              = 200;
    public static final int bulletSpawnerPriority               = 201;
    public static final int bulletCleanUpPriority               = 202;
    public static final int collisionCleanUpPriority            = 399;
    public static final int collisionDetectorPriority           = 400;
    public static final int asteroidCollisionPriority           = 401;
    public static final int asteroidBulletCollisionPriority     = 402;
    public static final int objectMovementPriority              = 500;
    public static final int bulletMovementPriority              = 501;
    public static final int outOfBoundsPriority                 = 502;
    public static final int bulletCollisionHandlerPriority      = 503;
    public static final int objectDumpingPriority               = 505;
    public static final int motionPriority                      = 510;
    public static final int inputPriority                       = 600;
    public static final int gameBackgroundRenderingPriority     = 900;
    public static final int gameObjectRenderingPriority         = 1000;
    public static final int bulletRenderingPriority             = 1100;
    public static final int debugOverlayRenderingPriority       = 2000;

    // ship
    public static final float shipMaxLinearVelocity                         = 4.5f;
    public static final float shipLinearDumping                             = 0.5f;
    public static final float shipMaxCounterClockwiseAngularVelocity        = 180 * MathUtils.degreesToRadians;
    public static final float shipMaxClockwiseAngularVelocity               = -shipMaxCounterClockwiseAngularVelocity;
    public static final float shipAngularDumping                            = 6.0f;
    public static final float shipLinearAcceleration                        = 10.5f;
    public static final float shipCounterClockwiseAngularAcceleration       = 360 * 8 * MathUtils.degreesToRadians;
    public static final float shipClockwiseAngularAcceleration              = -shipCounterClockwiseAngularAcceleration;
    public static final Array<Vector2> shipShapeTemplate                    = createShipTemplate();

    private static Array<Vector2> createShipTemplate() {
        Vector2 front = new Vector2(0.5f, 0.0f);
        Vector2 right = new Vector2(-0.5f, 0.25f);
        Vector2 left = new Vector2(-0.5f, -0.25f);
        Vector2 back = new Vector2(-0.25f, 0.0f);

        return new Array<>(new Vector2[] {front, right, back, left});
    }

    // bullet
    public static final float bulletSize                                    = 0.1f;
    public static final float bulletMaxLinearVelocity                       = 15.0f;
    public static final float bulletCooldown                                = 0.2f;

    // asteroids
    public static final float asteroidSpawnerInterval                       = 1.0f;
    public static final int asteroidSpawnThreshold                          = 8;
    public static final float asteroidMinLinearVelocity                     = 4.5f * 0.05f;
    public static final float asteroidMaxLinearVelocity                     = 4.5f * 0.25f;
    public static final float asteroidMaxCounterClockwiseAngularVelocity    = 180 * MathUtils.degreesToRadians * 0.25f;
    public static final float asteroidMaxClockwiseAngularVelocity           = -asteroidMaxCounterClockwiseAngularVelocity;
    public static final ObjectMap<Constants.AsteroidType, Array<Array<Vector2>>> asteroidTemplates = createAsteroidTemplates();

    private static ObjectMap<Constants.AsteroidType, Array<Array<Vector2>>> createAsteroidTemplates() {
        final int templateCount = 32;
        final int minTemplateSize = 5;
        final int maxTemplateSize = 8;

        ObjectMap<Constants.AsteroidType, Array<Array<Vector2>>> result = new ObjectMap<>();

        for(AsteroidType at : AsteroidType.values()) {
            float radiusVar = at.radius * 0.35f;
            result.put(at, new Array<>(templateCount));

            for(int i = 0; i < templateCount; ++i) {
                int size = MathUtils.random(minTemplateSize, maxTemplateSize);
                Array<Vector2> template = new Array<>(size);
                float angleStep = MathUtils.PI2 / size;
                float angleStepVar = angleStep * 0.35f;

                for(int j = 0; j < size; ++j) {
                    template.add(
                        new Vector2(Vector2.X)
                            .rotateRad(angleStep * j - angleStepVar * 0.5f + MathUtils.random(angleStepVar))
                            .scl(at.radius - radiusVar + MathUtils.random(radiusVar))
                    );
                }

                result.get(at).add(template);
            }
        }

        return result;
    }

    public enum AsteroidType {
        epic(1.75f),
        large(epic.radius * 0.6f),
        medium(large.radius * 0.6f),
        small(medium.radius * 0.6f);

        public final float radius;

        AsteroidType(float radius) {
            this.radius = radius;
        }
    }
}
