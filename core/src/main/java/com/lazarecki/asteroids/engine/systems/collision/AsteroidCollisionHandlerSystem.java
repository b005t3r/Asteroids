package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.collision.ProcessedCollisionComponent;
import com.lazarecki.asteroids.engine.components.collision.DetectedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;

public class AsteroidCollisionHandlerSystem extends IteratingSystem {
    public AsteroidCollisionHandlerSystem() {
        super(Family
                .all(AsteroidComponent.class, DetectedCollisionComponent.class,
                    PositionComponent.class, BoundingRadiusComponent.class,
                    LinearVelocityComponent.class)
                .get(),
            Constants.asteroidCollisionSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();

        PositionComponent p             = Mappers.position.get(entity);
        BoundingRadiusComponent b       = Mappers.boundingRadius.get(entity);
        LinearVelocityComponent lv      = Mappers.linearVel.get(entity);
        DetectedCollisionComponent dc   = Mappers.detectedCollision.get(entity);
        ProcessedCollisionComponent pc  = Mappers.processedCollision.get(entity);

        for(int i = dc.collisions.size - 1; i >= 0; --i) {
            Entity otherEntity = dc.collisions.removeIndex(i);

            if(! entities.contains(otherEntity, true))
                continue;

            PositionComponent op            = Mappers.position.get(otherEntity);
            BoundingRadiusComponent ob      = Mappers.boundingRadius.get(otherEntity);
            LinearVelocityComponent olv     = Mappers.linearVel.get(otherEntity);
            DetectedCollisionComponent odc  = Mappers.detectedCollision.get(otherEntity);
            ProcessedCollisionComponent opc = Mappers.processedCollision.get(otherEntity);

            odc.collisions.removeValue(entity, true);

            if(pc != null && pc.collisions.contains(otherEntity, true))
                return;

            EngineUtils.elasticCirclesCollision(
                p.position, b.radius, lv.velocity,
                op.position, ob.radius, olv.velocity
            );

            if(pc == null)
                pc = entity.addAndReturn(getEngine().createComponent(ProcessedCollisionComponent.class));

            pc.collisions.add(otherEntity);

            if(opc == null)
                opc = otherEntity.addAndReturn(getEngine().createComponent(ProcessedCollisionComponent.class));

            opc.collisions.add(entity);
        }
    }
}
