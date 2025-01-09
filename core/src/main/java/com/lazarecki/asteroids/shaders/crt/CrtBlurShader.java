package com.lazarecki.asteroids.shaders.crt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lazarecki.asteroids.shaders.Shader;

public class CrtBlurShader extends Shader {
    public Vector2 pixelSize = new Vector2(Float.NaN, Float.NaN);
    public float sigma = Float.NaN;
    public Vector3 kernel = new Vector3(Float.NaN, Float.NaN, Float.NaN);

    public static Vector3 calculateKernel(float sigma, Vector3 result) {
        if(result == null)
            result = new Vector3();

        final int kSize	= 1;

        result.z = 0.0f;
        for(int j = 0; j <= kSize; ++j) {
            float normal            = calculateBlurWeight(j, sigma);
            //blurKernel[kSize - j]	= normal;

            if(j > 0) {
                result.x = normal;
                result.z += 2 * normal;
            }
            else {
                result.y = normal;
                result.z += normal;
            }
        }

        result.z *= result.z;

        return result;
    }

    private static float calculateBlurWeight(float x, float sigma) {
        return (float) (0.39894f * Math.exp(-0.5f * x * x / (sigma * sigma)) / sigma);
    }

    @Override
    protected ShaderProgram create() {
        ShaderProgram program = new ShaderProgram(defaultVertexShader, Gdx.files.internal("shaders/crt/blur.frag").readString());

        return program;
    }

    @Override
    protected void setUniforms(ShaderProgram program) {
        program.setUniformf("u_sigma", sigma);
        program.setUniformf("u_pixelSize", pixelSize);
        program.setUniformf("u_kernel", kernel);
    }
}
