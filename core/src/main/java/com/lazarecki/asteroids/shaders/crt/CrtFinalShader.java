package com.lazarecki.asteroids.shaders.crt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.lazarecki.asteroids.shaders.Shader;

public class CrtFinalShader extends Shader {
    public enum MaskMode { thin, dense, denser, thinScanline, scanline, denseScanline }

    public Vector2 pixelSize = new Vector2(Float.NaN, Float.NaN);

    public MaskMode maskMode = null;
    public float maskStrength = Float.NaN;
    public float vignetteStrength = Float.NaN;
    public float vignetteSize = Float.NaN;
    public Vector2 crtBend = new Vector2(Float.NaN, Float.NaN);
    public float crtOverscan = Float.NaN;

    @Override
    protected ShaderProgram create() {
        return new ShaderProgram(defaultVertexShader, Gdx.files.internal("shaders/crt/final.frag").readString());
    }

    @Override
    protected void setUniforms(ShaderProgram program) {
        program.setUniformf("u_pixelSize", pixelSize);
        program.setUniformi("u_maskMode", maskMode.ordinal());
        program.setUniformf("u_maskStr", maskStrength);
        program.setUniformf("u_vignetteStr", vignetteStrength);
        program.setUniformf("u_vignetteSize", vignetteSize);
        program.setUniformf("u_crtBend", crtBend);
        program.setUniformf("u_crtOverscan", crtOverscan);
    }
}
