package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.*;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletComponent;

public class BulletCollisionHandlerSystem extends IteratingSystem {
    private ImmutableArray<Entity> asteroids;

    private Vector2 tmpP = new Vector2();
    private Vector2 tmpPP = new Vector2();

    public BulletCollisionHandlerSystem() {
        super(Family.all(
                BulletComponent.class, PositionComponent.class, PreviousPositionComponent.class
            ).get(),
            Constants.bulletCollisionHandlerPriority
        );
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        asteroids = engine.getEntitiesFor(
            Family.all(
                AsteroidComponent.class,
                PositionComponent.class, RotationComponent.class, ShapeComponent.class, BoundingRadiusComponent.class
            ).get()
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p = Mappers.position.get(entity);
        PreviousPositionComponent pp = Mappers.prevPosition.get(entity);

        for(int i = asteroids.size() - 1; i >= 0; --i) {
            Entity asteroid = asteroids.get(i);

            PositionComponent ap = Mappers.position.get(asteroid);
            RotationComponent ar = Mappers.rotation.get(asteroid);
            ShapeComponent as = Mappers.shape.get(asteroid);
            BoundingRadiusComponent ab = Mappers.boundingRadius.get(asteroid);

            tmpP.set(p.position).sub(ap.position);
            tmpPP.set(pp.position).sub(ap.position);

            if(! Intersector.intersectSegmentCircle(tmpP, tmpPP, Vector2.Zero, ab.radius * ab.radius))
                continue;

            if(! EngineUtils.isBulletColliding(p.position, pp.position, as.path, ap.position, ar.rotation, null))
                continue;

            getEngine().removeEntity(entity);
            return;
        }
    }
}
