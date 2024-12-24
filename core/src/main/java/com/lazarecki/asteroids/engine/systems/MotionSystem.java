package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LateralAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LateralVelocityComponent;

public class MotionSystem extends IteratingSystem {
    private ComponentMapper<LateralVelocityComponent> latVelMapper      = ComponentMapper.getFor(LateralVelocityComponent.class);
    private ComponentMapper<AngularVelocityComponent> angVelMapper      = ComponentMapper.getFor(AngularVelocityComponent.class);
    private ComponentMapper<LateralAccelerationComponent> latAccMapper  = ComponentMapper.getFor(LateralAccelerationComponent.class);
    private ComponentMapper<AngularAccelerationComponent> angAccMapper  = ComponentMapper.getFor(AngularAccelerationComponent.class);

    public MotionSystem() {
        super(Family.all(
                LateralVelocityComponent.class, AngularVelocityComponent.class,
                LateralAccelerationComponent.class, AngularAccelerationComponent.class
            ).get(),
            Constants.motionSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LateralVelocityComponent lv     = latVelMapper.get(entity);
        AngularVelocityComponent av     = angVelMapper.get(entity);
        LateralAccelerationComponent la = latAccMapper.get(entity);
        AngularAccelerationComponent aa = angAccMapper.get(entity);

        lv.velocity.mulAdd(la.acceleration, deltaTime).clamp(Constants.minLateralVelocity, Constants.maxLateralVelocity);
        av.velocity += aa.acceleration * deltaTime;
        av.velocity = MathUtils.clamp(av.velocity, Constants.maxClockwiseAngularVelocity, Constants.maxCounterClockwiseAngularVelocity);

        // outside allowed arc
    }
}
