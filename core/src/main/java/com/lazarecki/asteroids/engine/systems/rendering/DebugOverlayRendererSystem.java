package com.lazarecki.asteroids.engine.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.collision.DetectedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.BoundingRadiusComponent;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DebugOverlayRendererSystem extends IteratingSystem {
    private PolygonSpriteBatch batch;
    private ShapeDrawer drawer;
    private Viewport viewport;

    private Vector2 tmpVec = new Vector2();

    public DebugOverlayRendererSystem(PolygonSpriteBatch batch, ShapeDrawer drawer, Viewport viewport) {
        super(Family
                .all(PositionComponent.class)
                .one(BoundingRadiusComponent.class, LinearVelocityComponent.class)
                .get(),
            Constants.debugOverlayRenderingPriority);

        this.batch = batch;
        this.drawer = drawer;
        this.viewport = viewport;
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p             = Mappers.position.get(entity);
        LinearVelocityComponent lv      = Mappers.linearVel.get(entity);
        BoundingRadiusComponent b       = Mappers.boundingRadius.get(entity);
        DetectedCollisionComponent c    = Mappers.detectedCollision.get(entity);

        if(lv != null) {
            tmpVec.set(p.position).mulAdd(lv.velocity, 1.0f);

            drawer.setColor(Color.GREEN);
            drawer.filledCircle(p.position, 0.15f);
            drawer.line(p.position, tmpVec, 0.05f);
        }

        if(b != null) {
            drawer.setColor(c != null ? Color.RED : Color.GREEN);
            drawer.circle(p.position.x, p.position.y, b.radius, 0.05f);
        }
    }
}
