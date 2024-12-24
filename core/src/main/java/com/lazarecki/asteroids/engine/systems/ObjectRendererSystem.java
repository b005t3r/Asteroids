package com.lazarecki.asteroids.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.location.PositionComponent;
import com.lazarecki.asteroids.engine.components.location.RotationComponent;
import com.lazarecki.asteroids.engine.components.location.ShapeComponent;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ObjectRendererSystem extends IteratingSystem {
    private PolygonSpriteBatch batch;
    private ShapeDrawer drawer;
    private Viewport viewport;

    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rotationMapper = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<ShapeComponent> shapeMapper = ComponentMapper.getFor(ShapeComponent.class);

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
        PositionComponent p = positionMapper.get(entity);
        RotationComponent r = rotationMapper.get(entity);
        ShapeComponent s    = shapeMapper.get(entity);

        Array<Vector2> rotated = new Array<>(s.path.size);
        for(Vector2 v : s.path)
            rotated.add(new Vector2(v).rotateRad(r.rotation).add(p.position));

        drawer.setColor(0.825f, 0.825f, 1.0f, 1.0f);
        drawer.path(rotated, Constants.lineWidth, JoinType.SMOOTH, false);
    }
}