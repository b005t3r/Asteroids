package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearAccelerationComponent;

public class InputSystem extends IteratingSystem {
    public InputSystem() {
        super(Family.all(ShipComponent.class, LinearAccelerationComponent.class, AngularAccelerationComponent.class).get(), Constants.inputSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        boolean forward             = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean clockwise           = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean counterClockwise    = Gdx.input.isKeyPressed(Input.Keys.A);

        Mappers.linearAcc.get(entity).acceleration = forward ? Constants.linearAcceleration : 0;
        Mappers.angularAcc.get(entity).acceleration
            = (clockwise ? Constants.clockwiseAngularAcceleration : 0)
            + (counterClockwise ? Constants.counterClockwiseAngularAcceleration : 0)
        ;
    }
}
