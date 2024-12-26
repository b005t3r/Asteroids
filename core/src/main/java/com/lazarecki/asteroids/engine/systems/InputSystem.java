package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.FiringComponent;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularAccelerationComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearAccelerationComponent;

public class InputSystem extends IteratingSystem {
    public InputSystem() {
        super(Family.all(ShipComponent.class, LinearAccelerationComponent.class, AngularAccelerationComponent.class).get(), Constants.inputPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        boolean forward             = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean clockwise           = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean counterClockwise    = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean fire                = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        Mappers.linearAcc.get(entity).acceleration = forward ? Constants.shipLinearAcceleration : 0;
        Mappers.angularAcc.get(entity).acceleration
            = (clockwise ? Constants.shipClockwiseAngularAcceleration : 0)
            + (counterClockwise ? Constants.shipCounterClockwiseAngularAcceleration : 0)
        ;

        if(fire && ! Mappers.fired.has(entity))
            entity.add(getEngine().createComponent(FiringComponent.class));
    }
}
