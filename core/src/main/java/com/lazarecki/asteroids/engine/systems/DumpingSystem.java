package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.physics.AngularDumpingComponent;
import com.lazarecki.asteroids.engine.components.physics.AngularVelocityComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearDumpingComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class DumpingSystem extends IteratingSystem {
    private ComponentMapper<LinearDumpingComponent> linDumpMapper   = ComponentMapper.getFor(LinearDumpingComponent.class);
    private ComponentMapper<AngularDumpingComponent> angDumpMapper  = ComponentMapper.getFor(AngularDumpingComponent.class);
    private ComponentMapper<LinearVelocityComponent> linVelMapper   = ComponentMapper.getFor(LinearVelocityComponent.class);
    private ComponentMapper<AngularVelocityComponent> angVelMapper  = ComponentMapper.getFor(AngularVelocityComponent.class);

    private Vector2 tmp = new Vector2();

    public DumpingSystem() {
        super(Family
            .all(LinearDumpingComponent.class, LinearVelocityComponent.class)
            .all(AngularDumpingComponent.class, AngularVelocityComponent.class)
            .get(),
            Constants.dumpingSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LinearDumpingComponent ld   = linDumpMapper.get(entity);
        LinearVelocityComponent lv  = linVelMapper.get(entity);
        AngularDumpingComponent ad  = angDumpMapper.get(entity);
        AngularVelocityComponent av = angVelMapper.get(entity);

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
