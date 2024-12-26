package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.collision.ProcessedCollisionComponent;
import com.lazarecki.asteroids.engine.components.collision.DetectedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;

public class CollisionCleanUpSystem extends IteratingSystem {
    public CollisionCleanUpSystem() {
        super(Family
                .all(DetectedCollisionComponent.class)
                .all(ProcessedCollisionComponent.class, ShapeComponent.class, PositionComponent.class, BoundingRadiusComponent.class)
            .get(),
            Constants.collisionCleanUpPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // remove imminent collisions set on the previous step, all should be handled at this point
        entity.remove(DetectedCollisionComponent.class);

        ProcessedCollisionComponent pc = Mappers.processedCollision.get(entity);

        if(pc == null)
            return;

        for(int i = pc.collisions.size - 1; i >= 0; --i) {
            Entity otherEntity = pc.collisions.get(i);

            boolean collides =
                EngineUtils.isShapeColliding(
                    Mappers.shape.get(entity).path,
                    Mappers.position.get(entity).position,
                    Mappers.boundingRadius.get(entity).radius,
                    Mappers.shape.get(otherEntity).path,
                    Mappers.position.get(otherEntity).position,
                    Mappers.boundingRadius.get(otherEntity).radius
                );

            // the collision is still happening
            if(collides)
                continue;

            pc.collisions.removeValue(otherEntity, true);

            if(pc.collisions.isEmpty())
                entity.remove(ProcessedCollisionComponent.class);

            ProcessedCollisionComponent opc = Mappers.processedCollision.get(otherEntity);
            opc.collisions.removeValue(entity, true);

            if(opc.collisions.isEmpty())
                otherEntity.remove(ProcessedCollisionComponent.class);
        }
    }
}
