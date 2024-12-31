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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.systems.*;
import com.lazarecki.asteroids.engine.systems.collision.*;
import com.lazarecki.asteroids.engine.systems.logic.*;
import com.lazarecki.asteroids.engine.systems.physics.*;
import com.lazarecki.asteroids.engine.systems.rendering.*;
import com.lazarecki.asteroids.shaders.crt.BlurShader;
import com.lazarecki.asteroids.shaders.crt.CrtPostprocessShader;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameplayScreen implements Screen {
    private PolygonSpriteBatch batch;
    private SpriteBatch fboBatch;
    private ShapeDrawer shapeDrawer;
    private GameViewport gameViewport;

    private boolean fboEnabled = true;
    private FitViewport fboSuperSamplingViewport;
    private FrameBuffer fboSuperSampling;
    private FitViewport fboLowResToScreenViewport;
    private FrameBuffer fboLowRes;
    private FrameBuffer fboLowResBlurred;
    private FrameBuffer fboLowResCrt;
    private FrameBuffer fboLowResFinal;
    private FitViewport lowResToLowResViewport;

    private BlurShader crtBlurShader;
    private CrtPostprocessShader crtPostprocessShader;

    private Engine engine;
    private DebugOverlayRendererSystem debugRendererSystem;

    private float time = 0;

    @Override
    public void show() {
        batch = new PolygonSpriteBatch();
        fboBatch = new SpriteBatch();

        // TODO: change to texture later
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, 0xFFFFFFFF);
        shapeDrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(pixmap)));

        gameViewport = new GameViewport(Constants.gameWidth, Constants.gameHeight);

        fboLowRes = new FrameBuffer(Pixmap.Format.RGBA8888, 320, 240, false);
        fboLowResBlurred = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes.getWidth(), fboLowRes.getHeight(), false);
        fboLowResCrt = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes.getWidth(), fboLowRes.getHeight(), false);
        fboLowResToScreenViewport = new FitViewport(fboLowRes.getWidth(), fboLowRes.getHeight());

        fboSuperSampling = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes.getWidth() * 4, fboLowRes.getHeight() * 4, false);
        fboSuperSamplingViewport = new FitViewport(fboSuperSampling.getWidth(), fboSuperSampling.getHeight());

        lowResToLowResViewport = new FitViewport(fboLowRes.getWidth(), fboLowRes.getHeight());
        lowResToLowResViewport.update(fboLowRes.getWidth(), fboLowRes.getHeight(), true);

        crtBlurShader = new BlurShader();
        crtPostprocessShader = new CrtPostprocessShader();
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
        time += delta;

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

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        ScreenUtils.clear(Color.DARK_GRAY);

        if(fboEnabled && ! Float.isFinite(gameViewport.getDebugZoom())) {
            fboSuperSampling.begin();
            engine.update(delta);
            fboSuperSampling.end();

            Texture fboSuperSamplingTex = fboSuperSampling.getColorBufferTexture();
            fboSuperSamplingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            fboLowRes.begin();
            lowResToLowResViewport.apply(true);
            fboBatch.setProjectionMatrix(lowResToLowResViewport.getCamera().combined);
            fboBatch.begin();
            fboBatch.draw(fboSuperSamplingTex, 0, 0, fboLowRes.getWidth(), fboLowRes.getHeight(), 0, 0, 1, 1);
            fboBatch.end();
            fboLowRes.end();

            Texture lowResTex = fboLowRes.getColorBufferTexture();
            lowResTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            crtBlurShader.pixelSize.set(1.0f / lowResTex.getWidth(), 1.0f / lowResTex.getHeight());
            crtBlurShader.sigma = 0.9f;
            crtBlurShader.kernel = BlurShader.calculateKernel(crtBlurShader.sigma, crtBlurShader.kernel);

            fboLowResBlurred.begin();
            crtBlurShader.attach(fboBatch);
            lowResToLowResViewport.apply(true);
            fboBatch.setProjectionMatrix(lowResToLowResViewport.getCamera().combined);
            fboBatch.begin();
            fboBatch.draw(lowResTex, 0, 0, lowResTex.getWidth(), lowResTex.getHeight(), 0, 0, 1, 1);
            fboBatch.end();
            crtBlurShader.detach(fboBatch);
            fboLowResBlurred.end();

            Texture blurredTex = fboLowResBlurred.getColorBufferTexture();
            blurredTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            crtPostprocessShader.main = lowResTex;
            crtPostprocessShader.blurred = blurredTex;

            fboLowResCrt.begin();
            //blurredTex.bind(1);
            crtPostprocessShader.attach(fboBatch);
            //lowResTex.bind(0);
            lowResToLowResViewport.apply(true);
            fboBatch.setProjectionMatrix(lowResToLowResViewport.getCamera().combined);
            fboBatch.begin();
            fboBatch.draw(lowResTex, 0, 0, lowResTex.getWidth(), lowResTex.getHeight(), 0, 0, 1, 1);
            fboBatch.end();
            crtPostprocessShader.detach(fboBatch);
            fboLowResCrt.end();

            Texture outputTex = fboLowResCrt.getColorBufferTexture();
            outputTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            fboLowResToScreenViewport.apply(true);
            fboBatch.setProjectionMatrix(fboLowResToScreenViewport.getCamera().combined);
            fboBatch.begin();
            fboBatch.draw(outputTex, 0, 0, fboLowRes.getWidth(), fboLowRes.getHeight(), 0, 0, 1, 1);
            fboBatch.end();
        }
        else {
            engine.update(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        if(fboEnabled && ! Float.isFinite(gameViewport.getDebugZoom())) {
            gameViewport.update(fboSuperSampling.getWidth(), fboSuperSampling.getHeight(), true);
            fboLowResToScreenViewport.update(width, height, true);
        }
        else {
            gameViewport.update(width, height, true);
        }
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
