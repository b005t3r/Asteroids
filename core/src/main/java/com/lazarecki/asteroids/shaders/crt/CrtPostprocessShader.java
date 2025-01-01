package com.lazarecki.asteroids.shaders.crt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.lazarecki.asteroids.shaders.AbstractShader;

public class CrtPostprocessShader extends AbstractShader {
    public enum NoiseMode { add, subtract, multiply, divide, max, min }

    private static Pool<Matrix4> matrixPool = new Pool<>() {
        @Override
        protected Matrix4 newObject() {
            return new Matrix4();
        }
    };

    /**
     *
     * @param brightness 0.0 is the neutral level
     * @param contrast 1.0 is the neutral level
     * @param saturation 1.0 is the neutral level
     * @param result
     * @return
     */
    public static Matrix4 calculateColorMatrix(float brightness, float contrast, float saturation, Matrix4 result) {
        if(result == null)
            result = new Matrix4();

        Matrix4 brightnessMat = matrixPool.obtain();
        brightnessMat.idt();
        brightnessMat.val[Matrix4.M03] = brightness;
        brightnessMat.val[Matrix4.M13] = brightness;
        brightnessMat.val[Matrix4.M23] = brightness;

        float ct = (1.0f - contrast) / 2.0f;

        Matrix4 contrastMat = matrixPool.obtain();
        contrastMat.idt();
        contrastMat.val[Matrix4.M00] = contrast;
        contrastMat.val[Matrix4.M11] = contrast;
        contrastMat.val[Matrix4.M22] = contrast;
        contrastMat.val[Matrix4.M03] = ct;
        contrastMat.val[Matrix4.M13] = ct;
        contrastMat.val[Matrix4.M23] = ct;

        float luminanceX = 0.3086f;
        float luminanceY = 0.6094f;
        float luminanceZ = 0.0820f;
        float st = 1.0f - saturation;

        Matrix4 saturationMat = matrixPool.obtain();
        saturationMat.idt();
        saturationMat.val[Matrix4.M00] = luminanceX * st + saturation;
        saturationMat.val[Matrix4.M10] = luminanceX * st;
        saturationMat.val[Matrix4.M20] = luminanceX * st;
        saturationMat.val[Matrix4.M01] = luminanceY * st;
        saturationMat.val[Matrix4.M11] = luminanceY * st + saturation;
        saturationMat.val[Matrix4.M21] = luminanceY * st;
        saturationMat.val[Matrix4.M02] = luminanceZ * st;
        saturationMat.val[Matrix4.M12] = luminanceZ * st;
        saturationMat.val[Matrix4.M22] = luminanceZ * st + saturation;

        result.set(brightnessMat).mul(contrastMat).mul(saturationMat);

        matrixPool.free(brightnessMat);
        matrixPool.free(contrastMat);
        matrixPool.free(saturationMat);

        return result;
    }

    public Texture main = null;
    public Texture blurred = null;

    public Vector2 pixelSize = new Vector2(Float.NaN, Float.NaN);
    public float time = Float.NaN;

    public float bleedDist = Float.NaN;
    public float bleedStr = Float.NaN;
    public float blurStr = Float.NaN;
    public float rgbMaskSub = Float.NaN;
    public float rgbMaskSep = Float.NaN;
    public float rgbMaskStr = Float.NaN;
    public NoiseMode colorNoiseMode = null;
    public float colorNoiseStr = Float.NaN;
    public NoiseMode monoNoiseMode = null;
    public float monoNoiseStr = Float.NaN;

    public Matrix4 colorMat = new Matrix4();

    public Color minLevels = new Color();
    public Color maxLevels = new Color();
    public Color blackPoint = new Color();
    public Color whitePoint = new Color();

    public float interWidth = Float.NaN;
    public float interSpeed = Float.NaN;
    public float interStr = Float.NaN;
    public float interSplit = Float.NaN;

    public float aberStr = Float.NaN;

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

        program.setUniformf("u_pixelSize", pixelSize);
        program.setUniformf("u_time", time);

        program.setUniformf("u_bleedDist", bleedDist);
        program.setUniformf("u_bleedStr", bleedStr);
        program.setUniformf("u_blurStr", blurStr);
        program.setUniformf("u_rgbMaskSub", rgbMaskSub);
        program.setUniformf("u_rgbMaskSep", rgbMaskSep);
        program.setUniformf("u_rgbMaskStr", rgbMaskStr);

        program.setUniformi("u_colorNoiseMode", colorNoiseMode.ordinal());
        program.setUniformf("u_colorNoiseStr", colorNoiseStr);
        program.setUniformi("u_monoNoiseMode", monoNoiseMode.ordinal());
        program.setUniformf("u_monoNoiseStr", monoNoiseStr);

        program.setUniformMatrix("u_colorMat", colorMat);

        program.setUniformf("u_minLevels", minLevels.r, minLevels.g, minLevels.b);
        program.setUniformf("u_maxLevels", maxLevels.r, maxLevels.g, maxLevels.b);
        program.setUniformf("u_blackPoint", blackPoint.r, blackPoint.g, blackPoint.b);
        program.setUniformf("u_whitePoint", whitePoint.r, whitePoint.g, whitePoint.b);

        program.setUniformf("u_interWidth", interWidth);
        program.setUniformf("u_interSpeed", interSpeed);
        program.setUniformf("u_interStr", interStr);
        program.setUniformf("u_interSplit", interSplit);

        program.setUniformf("u_aberStr", aberStr);
    }
}
