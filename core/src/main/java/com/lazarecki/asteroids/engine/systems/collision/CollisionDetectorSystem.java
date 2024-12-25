package com.lazarecki.asteroids.engine.systems.collision;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.collision.CollisionComponent;
import com.lazarecki.asteroids.engine.components.location.*;

public class CollisionDetectorSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> positionMapper       = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rotationMapper       = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<ShapeComponent> shapeMapper             = ComponentMapper.getFor(ShapeComponent.class);
    private ComponentMapper<BoundingRadiusComponent> radiusMapper   = ComponentMapper.getFor(BoundingRadiusComponent.class);

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
        PositionComponent p         = positionMapper.get(entity);
        RotationComponent r         = rotationMapper.get(entity);
        ShapeComponent s            = shapeMapper.get(entity);
        BoundingRadiusComponent b   = radiusMapper.get(entity);

        for(Entity otherEntity : entities) {
            if(entity == otherEntity)
                continue;

            PositionComponent op        = positionMapper.get(otherEntity);
            BoundingRadiusComponent ob  = radiusMapper.get(otherEntity);

            if(p.position.dst(op.position) > b.radius + ob.radius)
                continue;

            RotationComponent or    = rotationMapper.get(otherEntity);
            ShapeComponent os       = shapeMapper.get(otherEntity);

            if(! EngineUtils.collides(s.path, p.position, r.rotation, os.path, op.position, or.rotation))
                continue;

            tmpCollisions.add(otherEntity);
        }

        if(tmpCollisions.isEmpty())
            return;

        entity.addAndReturn(getEngine().createComponent(CollisionComponent.class))
            .collisions.addAll(tmpCollisions);

        tmpCollisions.clear();
    }
}
