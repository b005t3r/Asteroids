package com.lazarecki.asteroids.shaders.crt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lazarecki.asteroids.shaders.AbstractShader;

public class CrtPostprocessShader extends AbstractShader {
    public Texture main;
    public Texture blurred;

    @Override
    protected ShaderProgram create() {
        return new ShaderProgram(defaultVertexShader, Gdx.files.internal("shaders/crt/crt_postprocess.frag").readString());
    }

    @Override
    protected void setUniforms(ShaderProgram program) {
        blurred.bind(1);
        program.setUniformi("u_blurTexture", 1);

        main.bind(0);
        program.setUniformi("u_texture", 0);
    }
}
