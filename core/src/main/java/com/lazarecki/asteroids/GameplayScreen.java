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
import com.badlogic.gdx.math.MathUtils;
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
import com.lazarecki.asteroids.utils.GraphicsUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameplayScreen implements Screen {
    private PolygonSpriteBatch batch;
    private SpriteBatch fboBatch;
    private ShapeDrawer shapeDrawer;
    private GameViewport gameViewport;

    private boolean fboEnabled = true;
    private FrameBuffer fboSuperSampling;
    private FitViewport fboLowRes3x3ToScreenViewport;
    private FrameBuffer fboLowRes1x1;
    private FrameBuffer fboLowRes3x3;
    private FrameBuffer fboLowRes3x3Blurred;
    private FrameBuffer fboLowRes3x3Crt;

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

        fboLowRes1x1 = new FrameBuffer(Pixmap.Format.RGBA8888, 320, 240, false);
        fboLowRes3x3 = new FrameBuffer(Pixmap.Format.RGBA8888, 3 * fboLowRes1x1.getWidth(), 3 * fboLowRes1x1.getHeight(), false);
        fboLowRes3x3Blurred = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes3x3.getWidth(), fboLowRes3x3.getHeight(), false);
        fboLowRes3x3Crt = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes3x3.getWidth(), fboLowRes3x3.getHeight(), false);
        fboLowRes3x3ToScreenViewport = new FitViewport(fboLowRes3x3.getWidth(), fboLowRes3x3.getHeight());

        fboSuperSampling = new FrameBuffer(Pixmap.Format.RGBA8888, fboLowRes1x1.getWidth() * 4, fboLowRes1x1.getHeight() * 4, false);

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

            GraphicsUtils.copyFrameBuffer(fboSuperSampling, fboLowRes1x1);
            GraphicsUtils.copyFrameBuffer(fboLowRes1x1, fboLowRes3x3, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            crtBlurShader.pixelSize.set(1.0f / fboLowRes3x3.getWidth(), 1.0f / fboLowRes3x3.getHeight());
            crtBlurShader.sigma = 0.7f;
            crtBlurShader.kernel = BlurShader.calculateKernel(crtBlurShader.sigma, crtBlurShader.kernel);
            GraphicsUtils.copyFrameBuffer(fboLowRes3x3, fboLowRes3x3Blurred, crtBlurShader, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            Texture blurredTex = fboLowRes3x3Blurred.getColorBufferTexture();
            blurredTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            Texture lowResTex = fboLowRes3x3.getColorBufferTexture();
            lowResTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            crtPostprocessShader.main = lowResTex;
            crtPostprocessShader.blurred = blurredTex;
            crtPostprocessShader.pixelSize.set(1.0f / lowResTex.getWidth(), 1.0f / lowResTex.getHeight());
            crtPostprocessShader.time = time;

            crtPostprocessShader.bleedDist = 0.75f;
            crtPostprocessShader.bleedStr = 0.5f;
            crtPostprocessShader.blurStr = 1.0f - 0.6f;
            crtPostprocessShader.rgbMaskSub = 0.6f;
            crtPostprocessShader.rgbMaskSep = 1.0f - 0.1f;
            crtPostprocessShader.rgbMaskStr = MathUtils.lerp(0.0f, 0.3f, 0.6f);

            crtPostprocessShader.colorNoiseMode = CrtPostprocessShader.NoiseMode.add;
            crtPostprocessShader.colorNoiseStr = MathUtils.lerp(0.0f, 0.4f, 0.15f);
            crtPostprocessShader.monoNoiseMode = CrtPostprocessShader.NoiseMode.max;
            crtPostprocessShader.monoNoiseStr = MathUtils.lerp(0.0f, 0.4f, 0.25f);

            crtPostprocessShader.colorMat = CrtPostprocessShader.calculateColorMatrix(
                MathUtils.lerp(0.8f, 1.2f, (0.2f + 1.0f) / 2.0f) - 1.0f,
                MathUtils.lerp(0.5f, 1.5f, (0.1f + 1.0f) / 2.0f),
                MathUtils.lerp(0.0f, 2.0f, (-0.05f + 1.0f) / 2.0f),
                crtPostprocessShader.colorMat
            );

            crtPostprocessShader.minLevels.set(Color.BLACK);
            crtPostprocessShader.maxLevels.set(Color.BLACK).lerp(Color.WHITE, 235.0f / 255.0f);
            crtPostprocessShader.blackPoint.set(Color.BLACK).lerp(Color.WHITE, 35.0f / 255.0f);
            crtPostprocessShader.whitePoint.set(Color.WHITE);

            crtPostprocessShader.interSpeed = 2.0f;
            crtPostprocessShader.interSplit = 0.55f;
            crtPostprocessShader.interStr = 0.05f;
            crtPostprocessShader.interWidth = 200.0f;

            crtPostprocessShader.aberStr = -1.25f;

            GraphicsUtils.copyFrameBuffer(fboLowRes3x3, fboLowRes3x3Crt, crtPostprocessShader, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            Texture outputTex = fboLowRes3x3Crt.getColorBufferTexture();
            outputTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            fboLowRes3x3ToScreenViewport.apply(true);
            fboBatch.setProjectionMatrix(fboLowRes3x3ToScreenViewport.getCamera().combined);
            fboBatch.begin();
            fboBatch.draw(outputTex, 0, 0, fboLowRes3x3.getWidth(), fboLowRes3x3.getHeight(), 0, 0, 1, 1);
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
            fboLowRes3x3ToScreenViewport.update(width, height, true);
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
