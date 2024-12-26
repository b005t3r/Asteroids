package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.collision.ProcessedCollisionComponent;
import com.lazarecki.asteroids.engine.components.collision.DetectedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.*;

public class CollisionDetectorSystem extends IteratingSystem {
    private Array<Entity> tmpCollisions = new Array<>(32);

    public CollisionDetectorSystem() {
        super(Family
                .all(PositionComponent.class, RotationComponent.class, ShapeComponent.class, BoundingRadiusComponent.class)
                .exclude(TeleportingComponent.class)
                .get(),
            Constants.collisionDetectorSystemPriority
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();
        PositionComponent p             = Mappers.position.get(entity);
        RotationComponent r             = Mappers.rotation.get(entity);
        ShapeComponent s                = Mappers.shape.get(entity);
        BoundingRadiusComponent b       = Mappers.boundingRadius.get(entity);
        ProcessedCollisionComponent pc  = Mappers.processedCollision.get(entity);;

        for(Entity otherEntity : entities) {
            if(entity == otherEntity || pc != null && pc.collisions.contains(otherEntity, true))
                continue;

            DetectedCollisionComponent oc = Mappers.detectedCollision.get(otherEntity);;

            if(oc != null && oc.collisions.contains(entity, true))
                continue;

            PositionComponent op        = Mappers.position.get(otherEntity);
            BoundingRadiusComponent ob  = Mappers.boundingRadius.get(otherEntity);

            if(p.position.dst(op.position) > b.radius + ob.radius)
                continue;

            RotationComponent or    = Mappers.rotation.get(otherEntity);
            ShapeComponent os       = Mappers.shape.get(otherEntity);

            if(! EngineUtils.collides(s.path, p.position, r.rotation, os.path, op.position, or.rotation))
                continue;

            tmpCollisions.add(otherEntity);

            if(oc == null)
                oc = otherEntity.addAndReturn(getEngine().createComponent(DetectedCollisionComponent.class));

            oc.collisions.add(entity);
        }

        if(tmpCollisions.isEmpty())
            return;

        DetectedCollisionComponent c = Mappers.detectedCollision.get(entity);

        if(c == null)
            c = entity.addAndReturn(getEngine().createComponent(DetectedCollisionComponent.class));

        c.collisions.addAll(tmpCollisions);

        tmpCollisions.clear();
    }
}
