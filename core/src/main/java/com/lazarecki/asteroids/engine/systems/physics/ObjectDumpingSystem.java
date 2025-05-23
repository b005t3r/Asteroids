package com.lazarecki.asteroids.engine.systems.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.physics.AngularDumpingComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearDumpingComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class ObjectDumpingSystem extends IteratingSystem {
    private Vector2 tmp = new Vector2();

    public ObjectDumpingSystem() {
        super(Family
            .one(LinearDumpingComponent.class, AngularDumpingComponent.class)
            .get(),
            Constants.objectDumpingPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LinearDumpingComponent ld   = Mappers.linearDump.get(entity);
        LinearVelocityComponent lv  = Mappers.linearVel.get(entity);
        AngularDumpingComponent ad  = Mappers.angularDump.get(entity);
        AngularVelocityComponent av = Mappers.angularVel.get(entity);

        if(ld != null && lv != null) {
            tmp.set(lv.velocity).rotateRad(MathUtils.PI).scl(deltaTime * ld.dumping);
            lv.velocity.add(tmp);
        }

        if(ad != null && av != null) {
            float dumpingStep = ad.dumping * deltaTime * -av.velocity;

            if(Math.abs(dumpingStep) > Math.abs(av.velocity))
                av.velocity = 0;
            else
                av.velocity += dumpingStep;
        }
    }
}
