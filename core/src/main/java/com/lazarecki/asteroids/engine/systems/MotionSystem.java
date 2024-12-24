package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class MotionSystem extends IteratingSystem {
    private ComponentMapper<LinearVelocityComponent> linVelMapper = ComponentMapper.getFor(LinearVelocityComponent.class);
    private ComponentMapper<AngularVelocityComponent> angVelMapper      = ComponentMapper.getFor(AngularVelocityComponent.class);
    private ComponentMapper<LinearAccelerationComponent> linAccMapper = ComponentMapper.getFor(LinearAccelerationComponent.class);
    private ComponentMapper<AngularAccelerationComponent> angAccMapper  = ComponentMapper.getFor(AngularAccelerationComponent.class);
    private ComponentMapper<RotationComponent> rotationMapper = ComponentMapper.getFor(RotationComponent.class);

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
        LinearVelocityComponent lv      = linVelMapper.get(entity);
        AngularVelocityComponent av     = angVelMapper.get(entity);
        LinearAccelerationComponent la  = linAccMapper.get(entity);
        AngularAccelerationComponent aa = angAccMapper.get(entity);
        RotationComponent r             = rotationMapper.get(entity);

        tmp.set(Vector2.Zero).mulAdd(Vector2.X, la.acceleration * deltaTime).setAngleRad(r.rotation).rotate90(-1);

        lv.velocity.add(tmp).clamp(Constants.minLinearVelocity, Constants.maxLinearVelocity);
        av.velocity += aa.acceleration * deltaTime;
        av.velocity = MathUtils.clamp(av.velocity, Constants.maxClockwiseAngularVelocity, Constants.maxCounterClockwiseAngularVelocity);

        // outside allowed arc
    }
}
