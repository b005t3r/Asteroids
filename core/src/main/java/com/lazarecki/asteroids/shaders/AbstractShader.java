package com.lazarecki.asteroids.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractShader implements Disposable {
    static {
        ShaderProgram.pedantic = false;
    }

    public static final String defaultVertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
        + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
        + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
        + "uniform mat4 u_projTrans;\n" //
        + "varying vec4 v_color;\n" //
        + "varying vec2 v_texCoords;\n" //
        + "\n" //
        + "void main()\n" //
        + "{\n" //
        + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
        + "   v_color.a = v_color.a * (255.0/254.0);\n" //
        + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
        + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
        + "}\n";

    public static final String defaultFragmentShader = "#ifdef GL_ES\n" //
        + "#define LOWP lowp\n" //
        + "precision mediump float;\n" //
        + "#else\n" //
        + "#define LOWP \n" //
        + "#endif\n" //
        + "varying LOWP vec4 v_color;\n" //
        + "varying vec2 v_texCoords;\n" //
        + "uniform sampler2D u_texture;\n" //
        + "void main()\n"//
        + "{\n" //
        + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
        + "}";


    protected ShaderProgram program;

    public AbstractShader() {
        this.program = create();

        if(! this.program.isCompiled())
            Gdx.app.error(getClass().getName(), "Shader compilation FAILED:\n" + program.getLog());
    }

    public void attach(Batch batch) {
        if(! program.isCompiled())
            return;

        batch.setShader(program);
        program.bind();

        setUniforms(program);
    }

    public void detach(Batch batch) {
        if(! program.isCompiled())
            return;

        batch.setShader(null);
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    protected abstract ShaderProgram create();
    protected abstract void setUniforms(ShaderProgram program);
}
