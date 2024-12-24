package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearAccelerationComponent;

public class InputSystem extends IteratingSystem {
    private ComponentMapper<AngularAccelerationComponent> angAccMapper = ComponentMapper.getFor(AngularAccelerationComponent.class);
    private ComponentMapper<LinearAccelerationComponent> linAccMapper   = ComponentMapper.getFor(LinearAccelerationComponent.class);

    public InputSystem() {
        super(Family.all(ShipComponent.class, LinearAccelerationComponent.class, AngularAccelerationComponent.class).get(), Constants.inputSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        boolean forward             = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean clockwise           = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean counterClockwise    = Gdx.input.isKeyPressed(Input.Keys.A);

        linAccMapper.get(entity).acceleration = forward ? Constants.linearAcceleration : 0;
        angAccMapper.get(entity).acceleration
            = (clockwise ? Constants.clockwiseAngularAcceleration : 0)
            + (counterClockwise ? Constants.counterClockwiseAngularAcceleration : 0)
        ;
    }
}
