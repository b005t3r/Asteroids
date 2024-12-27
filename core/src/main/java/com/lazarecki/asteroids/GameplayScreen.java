package com.lazarecki.asteroids;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.systems.*;
import com.lazarecki.asteroids.engine.systems.collision.*;
import com.lazarecki.asteroids.engine.systems.logic.*;
import com.lazarecki.asteroids.engine.systems.physics.*;
import com.lazarecki.asteroids.engine.systems.rendering.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameplayScreen implements Screen {
    private PolygonSpriteBatch batch;
    private ShapeDrawer shapeDrawer;
    private GameViewport gameViewport;

    private Engine engine;
    private DebugOverlayRendererSystem debugRendererSystem;

    @Override
    public void show() {
        batch = new PolygonSpriteBatch();

        // TODO: change to texture later
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, 0xFFFFFFFF);
        shapeDrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(pixmap)));

        //gameViewport = new FitViewport(Constants.gameWidth, Constants.gameHeight);
        gameViewport = new GameViewport(Constants.gameWidth, Constants.gameHeight);

        debugRendererSystem = new DebugOverlayRendererSystem(batch, shapeDrawer, gameViewport);

        engine = new PooledEngine();
        engine.addEntity(EngineUtils.createShipEntity(engine));
        engine.addSystem(new AsteroidSpawnerSystem());
        engine.addSystem(new BulletCooldownSystem());
        engine.addSystem(new BulletSpawnerSystem());
        engine.addSystem(new BulletCleanUpSystem());
        engine.addSystem(new CollisionCleanUpSystem());
        engine.addSystem(new CollisionDetectorSystem());
        engine.addSystem(new CollisionHandlerSystem());
        engine.addSystem(new AsteroidBulletHitHandlerSystem());
        engine.addSystem(new ObjectMovementSystem());
        engine.addSystem(new BulletMovementSystem());
        engine.addSystem(new BulletCollisionHandlerSystem());
        engine.addSystem(new OutOfBoundsTeleporterSystem());
        engine.addSystem(new ObjectDumpingSystem());
        engine.addSystem(new MotionSystem());
        engine.addSystem(new InputSystem());
        engine.addSystem(new BackgroundRendererSystem(batch, shapeDrawer, gameViewport));
        engine.addSystem(new ObjectRendererSystem(batch, shapeDrawer, gameViewport));
        engine.addSystem(new BulletRenderingSystem(batch, shapeDrawer, gameViewport));
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            show();
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            float debugZoom = gameViewport.getDebugZoom();

            if(Float.isFinite(debugZoom)) {
                gameViewport.setDebugZoom(Float.NaN);
                engine.removeSystem(debugRendererSystem);
            }
            else {
                gameViewport.setDebugZoom(1.5f);
                engine.addSystem(debugRendererSystem);
            }
        }

        ScreenUtils.clear(Color.DARK_GRAY);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        engine = null;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
