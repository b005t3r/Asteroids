package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class MotionSystem extends IteratingSystem {
    private Vector2 tmp = new Vector2();

    public MotionSystem() {
        super(Family.all(
                LinearVelocityComponent.class, AngularVelocityComponent.class,
                LinearAccelerationComponent.class, AngularAccelerationComponent.class,
                RotationComponent.class
            ).get(),
            Constants.motionSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LinearVelocityComponent lv      = Mappers.linearVel.get(entity);
        AngularVelocityComponent av     = Mappers.angularVel.get(entity);
        LinearAccelerationComponent la  = Mappers.linearAcc.get(entity);
        AngularAccelerationComponent aa = Mappers.angularAcc.get(entity);
        RotationComponent r             = Mappers.rotation.get(entity);

        tmp.set(Vector2.Zero).mulAdd(Vector2.X, la.acceleration * deltaTime).setAngleRad(r.rotation);

        lv.velocity.add(tmp).clamp(0, Constants.shipMaxLinearVelocity);
        av.velocity += aa.acceleration * deltaTime;
        av.velocity = MathUtils.clamp(av.velocity, Constants.shipMaxClockwiseAngularVelocity, Constants.shipMaxCounterClockwiseAngularVelocity);
    }
}
