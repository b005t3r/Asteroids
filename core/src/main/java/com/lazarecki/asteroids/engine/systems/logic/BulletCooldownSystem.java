package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.FiredComponent;

public class BulletCooldownSystem extends IteratingSystem {
    public BulletCooldownSystem() {
        super(Family.all(FiredComponent.class).get(), Constants.bulletCooldownSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FiredComponent f = Mappers.fired.get(entity);

        f.cooldown -= deltaTime;

        if(f.cooldown <= 0)
            entity.remove(FiredComponent.class);
    }
}
