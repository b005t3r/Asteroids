package com.lazarecki.asteroids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public final class Constants {
    public static final int gameWidth       = 20;
    public static final int gameHeight      = 15;
    public static final float lineWidth     = 0.0333f;

    public static final float spawnerInterval               = 0.1f;
    public static final int spawnThreshold                  = 30;

    public static final int spawnerSystemPriority           = 100;
    public static final int collisionCleanUpSystemPriority  = 399;
    public static final int collisionDetectorSystemPriority = 400;
    public static final int asteroidCollisionSystemPriority = 401;
    public static final int movementSystemPriority          = 500;
    public static final int outOfBoundsSystemPriority       = 501;
    public static final int dumpingSystemPriority           = 505;
    public static final int motionSystemPriority            = 510;
    public static final int inputSystemPriority             = 600;
    public static final int gameBackgroundRenderingPriority = 900;
    public static final int gameObjectRenderingPriority     = 1000;
    public static final int debugOverlayRenderingPriority   = 2000;

    public static final float minLinearVelocity                         = 0.0f;
    public static final float maxLinearVelocity                         = 4.5f;
    public static final float linearDumping                             = 0.5f;
    public static final float maxCounterClockwiseAngularVelocity        = 180 * MathUtils.degreesToRadians;
    public static final float maxClockwiseAngularVelocity               = -maxCounterClockwiseAngularVelocity;
    public static final float angularDumping                            = 6.0f;

    public static final float linearAcceleration                        = 10.5f;
    public static final float counterClockwiseAngularAcceleration       = 360 * 8 * MathUtils.degreesToRadians;
    public static final float clockwiseAngularAcceleration              = -counterClockwiseAngularAcceleration;

    public static final Array<Vector2> shipShapeTemplate = createShipTemplate();
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

    private static Array<Vector2> createShipTemplate() {
        Vector2 front = new Vector2(0.0f, -0.5f);
        Vector2 right = new Vector2(0.25f, 0.5f);
        Vector2 left = new Vector2(-0.25f, 0.5f);
        Vector2 back = new Vector2(0.0f, 0.25f);

        return new Array<>(new Vector2[] {front, right, back, left});
    }
}
