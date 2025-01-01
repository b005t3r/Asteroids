package com.lazarecki.asteroids.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lazarecki.asteroids.shaders.AbstractShader;

public final class GraphicsUtils {
    private static final SpriteBatch batch = new SpriteBatch();
    private static final OrthographicCamera camera = new OrthographicCamera(1, 1);

    public static void copyFrameBuffer(FrameBuffer src, FrameBuffer dst) {
        copyFrameBuffer(src, dst, null, null, null);
    }

    public static void copyFrameBuffer(FrameBuffer src, FrameBuffer dst, AbstractShader shader) {
        copyFrameBuffer(src, dst, shader, null, null);
    }

    public static void copyFrameBuffer(FrameBuffer src, FrameBuffer dst, Texture.TextureFilter minFilter, Texture.TextureFilter maxFilter) {
        copyFrameBuffer(src, dst, null, minFilter, maxFilter);
    }

    public static void copyFrameBuffer(FrameBuffer src, FrameBuffer dst, AbstractShader shader, Texture.TextureFilter minFilter, Texture.TextureFilter maxFilter) {
        dst.begin();

        ScreenUtils.clear(Color.BLACK);

        Texture sourceTexture = src.getColorBufferTexture();
        sourceTexture.setFilter(
            minFilter != null ? minFilter : Texture.TextureFilter.Linear,
            maxFilter != null ? maxFilter : Texture.TextureFilter.Linear
        );

        camera.setToOrtho(false, dst.getWidth(), dst.getHeight());

        if(shader != null)
            shader.attach(batch);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(sourceTexture, 0, 0, dst.getWidth(), dst.getHeight(), 0, 0, 1, 1);
        batch.end();
        if(shader != null)
            shader.detach(batch);

        dst.end();
    }
}
