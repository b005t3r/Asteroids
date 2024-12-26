package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.logic.FiredComponent;
import com.lazarecki.asteroids.engine.components.logic.FiringComponent;
import com.lazarecki.asteroids.engine.components.logic.ShipComponent;

public class BulletSpawnerSystem extends IteratingSystem {
    private Vector2 tmp = new Vector2();

    public BulletSpawnerSystem() {
        super(Family
                .all(ShipComponent.class, FiringComponent.class, PositionComponent.class, RotationComponent.class, BoundingRadiusComponent.class)
                .exclude(FiredComponent.class)
            .get(),
            Constants.bulletSpawnerSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p = Mappers.position.get(entity);
        RotationComponent r = Mappers.rotation.get(entity);
        BoundingRadiusComponent b = Mappers.boundingRadius.get(entity);

        tmp.set(Vector2.X).scl(b.radius).rotateRad(r.rotation).add(p.position);

        EngineUtils.spawnBullet(tmp, r.rotation, getEngine());

        entity.remove(FiringComponent.class);
        entity.addAndReturn(getEngine().createComponent(FiredComponent.class))
            .cooldown = Constants.bulletCooldown;
    }
}
