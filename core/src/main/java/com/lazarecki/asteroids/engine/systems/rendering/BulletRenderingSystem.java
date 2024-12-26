package com.lazarecki.asteroids.engine.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.PreviousPositionComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletComponent;
import com.lazarecki.asteroids.engine.components.physics.LinearVelocityComponent;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class BulletRenderingSystem extends IteratingSystem {
    private PolygonSpriteBatch batch;
    private ShapeDrawer drawer;
    private Viewport viewport;

    public BulletRenderingSystem(PolygonSpriteBatch batch, ShapeDrawer drawer, Viewport viewport) {
        super(
            Family
                .all(BulletComponent.class, PositionComponent.class, PreviousPositionComponent.class)
            .get(),
            Constants.bulletRenderingPriority
        );

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
        PositionComponent p = Mappers.position.get(entity);

        drawer.setColor(Constants.lineColor);
        drawer.filledCircle(p.position, Constants.bulletSize * 0.5f);
    }
}
