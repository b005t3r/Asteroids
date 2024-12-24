package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.location.TeleportingComponent;

public class OutOfBoundsTeleporterSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rotationMapper = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<ShapeComponent> shapeMapper = ComponentMapper.getFor(ShapeComponent.class);
    private ComponentMapper<TeleportingComponent> teleMapper = ComponentMapper.getFor(TeleportingComponent.class);

    private final Rectangle boundsRect    = new Rectangle(0, 0, Constants.gameWidth, Constants.gameHeight);
    private final Vector2 center          = boundsRect.getCenter(new Vector2());

    private Rectangle tmpRect = new Rectangle();
    private Vector2 tmpVec = new Vector2();

    public OutOfBoundsTeleporterSystem() {
        super(Family.all(PositionComponent.class, RotationComponent.class, ShapeComponent.class).get(), Constants.outOfBoundsSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ShapeComponent s    = shapeMapper.get(entity);
        RotationComponent r = rotationMapper.get(entity);
        PositionComponent p = positionMapper.get(entity);
        TeleportingComponent t = teleMapper.get(entity);


        // bounding rect
        tmpRect.set(p.position.x, p.position.y, 0, 0);

        for(Vector2 v : s.path) {
            tmpVec.set(v).rotateRad(r.rotation).add(p.position);

            tmpRect.merge(tmpVec);
        }

        // still in bounds?
        if(boundsRect.overlaps(tmpRect)) {
            if(t != null)
                entity.remove(TeleportingComponent.class);
        }
        // out of bounds and not teleporting
        else if(t == null) {
            // reposition to the other side of the screen
            if(p.position.x < boundsRect.x || p.position.x > boundsRect.x + boundsRect.width)
                p.position.x += 2.0f * (center.x - p.position.x);

            if(p.position.y < boundsRect.y || p.position.y > boundsRect.y + boundsRect.height)
                p.position.y += 2.0f * (center.y - p.position.y);

            //p.position.rotateAroundRad(center, MathUtils.PI);

            // mark as teleporting
            entity.add(getEngine().createComponent(TeleportingComponent.class));
        }
    }
}
