package com.lazarecki.asteroids;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.logic.ScoreCounterComponent;
import com.lazarecki.asteroids.engine.systems.*;
import com.lazarecki.asteroids.engine.systems.collision.*;
import com.lazarecki.asteroids.engine.systems.logic.*;
import com.lazarecki.asteroids.engine.systems.physics.*;
import com.lazarecki.asteroids.engine.systems.rendering.*;
import com.lazarecki.asteroids.shaders.crt.CrtBlurShader;
import com.lazarecki.asteroids.shaders.crt.CrtFinalShader;
import com.lazarecki.asteroids.shaders.crt.CrtPostprocessShader;
import com.lazarecki.asteroids.utils.GraphicsUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameplayScreen implements Screen {
    private PolygonSpriteBatch batch;
    private SpriteBatch fboBatch;
    private ShapeDrawer shapeDrawer;
    private GameViewport gameViewport;

    private FrameBuffer fboSuperSampling;
    private FitViewport fboLowRes3x3ToScreenViewport;
    private FrameBuffer fboLowRes1x1;
    private FrameBuffer fboLowRes3x3;
    private FrameBuffer fboLowRes3x3Blurred;
    private FrameBuffer fboLowRes3x3Crt;
    private Color fboClearColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    private Color bgColor = new Color(Color.DARK_GRAY).mul(Color.DARK_GRAY);

    private boolean crtEnabled = true;
    private CrtBlurShader crtBlurShader;
    private CrtPostprocessShader crtPostprocessShader;
    private CrtFinalShader crtFinalShader;

    private Skin skin;
    private ScreenViewport uiViewport;
    private Stage stage;
    private Label scoreLabel;

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

        crtBlurShader = new CrtBlurShader();
        crtPostprocessShader = new CrtPostprocessShader();
        crtFinalShader = new CrtFinalShader();

        skin = new Skin(Gdx.files.internal("skins/commodore64ui/uiskin.json"));
        uiViewport = new ScreenViewport();
        uiViewport.setUnitsPerPixel(2);
        stage = new Stage(uiViewport);
        scoreLabel = new Label("00000", skin, "optional");
        scoreLabel.setColor(Constants.lineColor);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(scoreLabel).expand().top().right().padRight(8).padTop(8);

        debugRendererSystem = new DebugOverlayRendererSystem(batch, shapeDrawer, gameViewport);

        engine = new PooledEngine();

        Entity score = engine.createEntity();
        score.addAndReturn(engine.createComponent(ScoreCounterComponent.class)).score = 0;
        engine.addEntity(score);

        engine.addEntity(EngineUtils.createShipEntity(engine));

        engine.addSystem(new RunawayAsteroidCleanUpSystem());
        engine.addSystem(new AsteroidSpawnerSystem());
        engine.addSystem(new BulletCooldownSystem());
        engine.addSystem(new BulletSpawnerSystem());
        engine.addSystem(new BulletCleanUpSystem());
        engine.addSystem(new CollisionCleanUpSystem());
        engine.addSystem(new CollisionDetectorSystem());
        engine.addSystem(new CollisionHandlerSystem());
        engine.addSystem(new ScoreCounterSystem());
        engine.addSystem(new AsteroidBulletHitHandlerSystem());
        engine.addSystem(new ShipCollisionHandlerSystem());
        engine.addSystem(new ObjectMovementSystem());
        engine.addSystem(new BulletMovementSystem());
        engine.addSystem(new BulletCollisionHandlerSystem());
        engine.addSystem(new OutOfBoundsTeleporterSystem());
        engine.addSystem(new ObjectDumpingSystem());
        engine.addSystem(new MotionSystem());
        engine.addSystem(new InputSystem());
        engine.addSystem(new BackgroundRendererSystem(batch, shapeDrawer, gameViewport));
        engine.addSystem(new ObjectRendererSystem(batch, shapeDrawer, gameViewport));
        engine.addSystem(new ScoreRendererSystem(scoreLabel));
        engine.addSystem(new BulletRenderingSystem(batch, shapeDrawer, gameViewport));
    }

    @Override
    public void render(float delta) {
        time += delta;

        stage.act();

        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            show();
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            crtEnabled = ! crtEnabled;
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

        ScreenUtils.clear(bgColor);

        if(! Float.isFinite(gameViewport.getDebugZoom())) {
            shapeDrawer.setDefaultSnap(true);
            fboSuperSampling.begin();
            engine.update(delta);
            fboSuperSampling.end();

            GraphicsUtils.copyFrameBuffer(fboSuperSampling, fboLowRes1x1, fboClearColor/*, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest*/);

            fboLowRes1x1.begin();
            stage.draw();
            fboLowRes1x1.end();

            GraphicsUtils.copyFrameBuffer(fboLowRes1x1, fboLowRes3x3, fboClearColor, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            if(crtEnabled) {
                crtBlurShader.pixelSize.set(1.0f / fboLowRes3x3.getWidth(), 1.0f / fboLowRes3x3.getHeight());
                crtBlurShader.sigma = 0.7f;
                crtBlurShader.kernel = CrtBlurShader.calculateKernel(crtBlurShader.sigma, crtBlurShader.kernel);
                GraphicsUtils.copyFrameBuffer(fboLowRes3x3, fboLowRes3x3Blurred, fboClearColor, crtBlurShader);

                Texture blurredTex = fboLowRes3x3Blurred.getColorBufferTexture();
                blurredTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

                Texture lowResTex = fboLowRes3x3.getColorBufferTexture();
                lowResTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

                crtPostprocessShader.main = lowResTex;
                crtPostprocessShader.blurred = blurredTex;
                crtPostprocessShader.pixelSize.set(1.0f / fboLowRes3x3.getWidth(), 1.0f / fboLowRes3x3.getHeight());
                crtPostprocessShader.time = time;

                crtPostprocessShader.bleedDist = 0.85f; // 0.75f;
                crtPostprocessShader.bleedStr = 0.95f; // 0.5f;
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
                crtPostprocessShader.blackPoint.set(Color.BLACK).lerp(Color.WHITE, 25.0f / 255.0f);
                crtPostprocessShader.whitePoint.set(Color.WHITE);

                crtPostprocessShader.interSpeed = 2.0f;
                crtPostprocessShader.interSplit = 0.55f;
                crtPostprocessShader.interStr = 0.05f;
                crtPostprocessShader.interWidth = 200.0f;

                crtPostprocessShader.aberStr = -1.0f; // -1.25f;

                GraphicsUtils.copyFrameBuffer(fboLowRes3x3, fboLowRes3x3Crt, fboClearColor, crtPostprocessShader);

                float realCurvatureX = MathUtils.lerp(0.25f, 0.45f, 0.4f);
                float realCurvatureY = MathUtils.lerp(0.25f, 0.45f, 0.4f);
                crtFinalShader.pixelSize.set(1.0f / fboLowRes3x3.getWidth(), 1.0f / fboLowRes3x3.getHeight());
                crtFinalShader.maskMode = CrtFinalShader.MaskMode.denser;
                crtFinalShader.maskStrength = 0.35f / 10.0f;
                crtFinalShader.vignetteSize = 1.0f - 0.35f;
                crtFinalShader.vignetteStrength = 0.1f;
                crtFinalShader.crtBend.set(
                    MathUtils.lerp(1.0f, 100.0f, (float) ((1.0f - realCurvatureX) / Math.exp(10.0f * realCurvatureX))),
                    MathUtils.lerp(1.0f, 100.0f, (float) ((1.0f - realCurvatureY) / Math.exp(10.0f * realCurvatureY)))
                );
                crtFinalShader.crtOverscan = MathUtils.lerp(0.05f, 0.25f, 0.1f);

                GraphicsUtils.copyFrameBuffer(fboLowRes3x3Crt, fboLowRes3x3, fboClearColor, crtFinalShader);
            }

            Texture outputTex = fboLowRes3x3.getColorBufferTexture();
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
        if(! Float.isFinite(gameViewport.getDebugZoom())) {
            gameViewport.update(fboSuperSampling.getWidth(), fboSuperSampling.getHeight(), true);
            uiViewport.update(fboLowRes1x1.getWidth(), fboLowRes1x1.getHeight(), true);
            fboLowRes3x3ToScreenViewport.update(width, height, true);
        }
        else {
            gameViewport.update(width, height, true);
            uiViewport.update(width, height, true);
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
