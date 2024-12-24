package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lazarecki.asteroids.Constants;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class BackgroundRendererSystem extends EntitySystem {
    private PolygonSpriteBatch batch;
    private ShapeDrawer drawer;
    private Viewport viewport;

    public BackgroundRendererSystem(PolygonSpriteBatch batch, ShapeDrawer drawer, Viewport viewport) {
        super(Constants.gameBackgroundRenderingPriority);

        this.batch = batch;
        this.drawer = drawer;
        this.viewport = viewport;
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        drawer.setColor(Color.BLACK);
        drawer.filledRectangle(0, 0, Constants.gameWidth, Constants.gameHeight);
        batch.end();
    }
}
