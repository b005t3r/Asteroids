package com.lazarecki.asteroids;

import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class GameViewport extends ScalingViewport {
    private float debugZoom = Float.NaN;

    public GameViewport(float worldWidth, float worldHeight) {
        super(Scaling.fit, worldWidth, worldHeight);
    }

    public float getDebugZoom() {
        return debugZoom;
    }

    public void setDebugZoom(float debugZoom) {
        this.debugZoom = debugZoom;
    }

    @Override
    public void apply(boolean centerCamera) {
        HdpiUtils.glViewport(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
        getCamera().viewportWidth = getWorldWidth();
        getCamera().viewportHeight = getWorldHeight();

        if(Float.isFinite(getDebugZoom())) {
            getCamera().viewportWidth *= getDebugZoom();
            getCamera().viewportHeight *= getDebugZoom();
        }

        if (centerCamera)
            getCamera().position.set(getWorldWidth() / 2, getWorldHeight() / 2, 0);

        getCamera().update();
    }
}
