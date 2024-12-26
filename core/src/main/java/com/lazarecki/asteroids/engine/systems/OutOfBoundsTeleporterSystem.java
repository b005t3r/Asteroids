package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import com.lazarecki.asteroids.engine.components.location.TeleportingComponent;

public class OutOfBoundsTeleporterSystem extends IteratingSystem {
    private final Rectangle boundsRect    = new Rectangle(0, 0, Constants.gameWidth, Constants.gameHeight);
    private final Vector2 center          = boundsRect.getCenter(new Vector2());

    private Rectangle tmpRect = new Rectangle();
    private Vector2 tmpVec = new Vector2();
    private Circle tmpCircle = new Circle();

    public OutOfBoundsTeleporterSystem() {
        super(Family.all(PositionComponent.class, ShapeComponent.class, BoundingRadiusComponent.class).get(), Constants.outOfBoundsSystemPriority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p         = Mappers.position.get(entity);
        BoundingRadiusComponent b   = Mappers.boundingRadius.get(entity);
        TeleportingComponent t      = Mappers.teleporting.get(entity);

        // bounding circle
        tmpCircle.set(p.position, b.radius);

        // still in bounds?
        if(Intersector.overlaps(tmpCircle, boundsRect)) {
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

            // mark as teleporting
            entity.add(getEngine().createComponent(TeleportingComponent.class));
        }
    }
}
