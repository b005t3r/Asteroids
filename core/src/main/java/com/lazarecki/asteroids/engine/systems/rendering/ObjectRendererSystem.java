package com.lazarecki.asteroids.engine.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ObjectRendererSystem extends IteratingSystem {
    private Pool<Vector2> vector2Pool = new Pool<>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };

    private Array<Vector2> rotated = new Array<>(32);

    private PolygonSpriteBatch batch;
    private ShapeDrawer drawer;
    private Viewport viewport;

    public ObjectRendererSystem(PolygonSpriteBatch batch, ShapeDrawer drawer, Viewport viewport) {
        super(Family.all(PositionComponent.class, RotationComponent.class, ShapeComponent.class).get(), Constants.gameObjectRenderingPriority);

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
        RotationComponent r = Mappers.rotation.get(entity);
        ShapeComponent s    = Mappers.shape.get(entity);

        for(Vector2 v : s.path)
            rotated.add(vector2Pool.obtain().set(v).rotateRad(r.rotation).add(p.position));

        drawer.setColor(Constants.lineColor);
        drawer.path(rotated, Constants.lineWidth, JoinType.SMOOTH, false);

        vector2Pool.freeAll(rotated);
        rotated.clear();
    }
}
