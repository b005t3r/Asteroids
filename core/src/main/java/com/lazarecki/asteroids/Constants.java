package com.lazarecki.asteroids;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public final class Constants {
    public static final int gameWidth       = 16;
    public static final int gameHeight      = 12;
    public static final float lineWidth     = 0.0333f;

    public static final int movementSystemPriority          = 500;
    public static final int motionSystemPriority            = 510;
    public static final int gameBackgroundRenderingPriority = 900;
    public static final int gameObjectRenderingPriority     = 1000;

    public static final float minLateralVelocity                        = 0.0f;
    public static final float maxLateralVelocity                        = 1.5f;
    public static final float maxCounterClockwiseAngularVelocity        = 120 * MathUtils.degreesToRadians;
    public static final float maxClockwiseAngularVelocity               = -120 * MathUtils.degreesToRadians;

    public static final float lateralAcceleration                       = 5.0f;
    public static final float maxCounterClockwiseAngularAcceleration    = 160 * MathUtils.degreesToRadians;
    public static final float maxClockwiseAngularAcceleration           = -160 * MathUtils.degreesToRadians;

    public static final Array<Vector2> shipShapeTemplate = createShipTemplate();

    private static Array<Vector2> createShipTemplate() {
        Vector2 front = new Vector2(0.0f, -0.5f);
        Vector2 right = new Vector2(0.25f, 0.5f);
        Vector2 left = new Vector2(-0.25f, 0.5f);
        Vector2 back = new Vector2(0.0f, 0.25f);

        return new Array<>(new Vector2[] {front, right, back, left});
    }
}
