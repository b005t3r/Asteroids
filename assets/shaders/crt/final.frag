#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform sampler2D u_texture;
uniform vec2 u_pixelSize;

uniform int u_maskMode;
uniform float u_maskStr;
uniform float u_vignetteStr;
uniform float u_vignetteSize;
uniform vec2 u_crtBend;
uniform float u_crtOverscan;

varying vec2 v_texCoords;
varying vec4 v_color;

vec4 alphaBlend(vec4 top, vec4 bottom) {
    vec4 result;
    result.a = top.a + bottom.a * (1.0 - top.a);
    result.rgb = (top.rgb * top.a + bottom.rgb * bottom.a * (1.0 - top.a)) / result.a;
    return result;
}

vec3 vignette(vec2 uv) {
    float outer = 1.0;
    float inner = u_vignetteSize;
    vec2 center = vec2(0.5, 0.5);// Center of screen

    float dist = distance(center, uv) * 1.414213;// Normalize to range 0.0-1.0
    float vig = clamp((outer - dist) / (outer - inner), 0.0, 1.0);

    return vec3(vig, vig, vig);
}

vec2 crt(vec2 coord, float bendX, float bendY) {
    coord = (coord - 0.5) * 2.0 / (u_crtOverscan + 1.0);
    coord *= 1.1;// Slight overscan
    coord.x *= 1.0 + pow(abs(coord.y) / bendX, 3.0);
    coord.y *= 1.0 + pow(abs(coord.x) / bendY, 3.0);
    coord = (coord / 2.0) + 0.5;
    return coord;
}

void main() {
    vec2 crtCoords = crt(v_texCoords, u_crtBend.x, u_crtBend.y);

    if (crtCoords.x < 0.0 || crtCoords.x > 1.0 || crtCoords.y < 0.0 || crtCoords.y > 1.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
        return;
    }

    vec4 finalColor = texture2D(u_texture, crtCoords);
    vec3 mask = vec3(1.0);

    if (u_maskMode == 0) {
        float moduloX = floor(mod(v_texCoords.x / u_pixelSize.x, 6.0));
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 4.0));

        if (moduloX < 3.0) {
            if (moduloY < 3.0) mask = vec3(1.0);
            else mask = vec3(0.0);
        }
        else {
            if (moduloY == 1.0) mask = vec3(0.0);
            else mask = vec3(1.0);
        }
    }
    else if (u_maskMode == 1) {
        float moduloX = floor(mod(v_texCoords.x / u_pixelSize.x, 6.0));
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 6.0));

        if (moduloX < 3.0) {
            if (moduloY == 0.0 || moduloY == 5.0) mask = vec3(0.0);
            else mask = vec3(1.0);
        }
        else {
            if (moduloY == 2.0 || moduloY == 3.0) mask = vec3(0.0);
            else mask = vec3(1.0);
        }
    }
    else if (u_maskMode == 2) {
        float moduloX = floor(mod(v_texCoords.x / u_pixelSize.x, 6.0));
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 5.0));

        if (moduloX < 3.0) {
            if (moduloY < 3.0) mask = vec3(1.0);
            else mask = vec3(0.0);
        }
        else {
            if (moduloY < 2.0) mask = vec3(0.0);
            else mask = vec3(1.0);
        }
    }
    else if (u_maskMode == 3) {
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 4.0));
        if (moduloY < 1.0) mask = vec3(0.0);
        else mask = vec3(1.0);
    }
    else if (u_maskMode == 4) {
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 4.0));
        if (moduloY < 2.0) mask = vec3(0.0);
        else mask = vec3(1.0);
    }
    else if (u_maskMode == 5) {
        float moduloY = floor(mod(v_texCoords.y / u_pixelSize.y, 5.0));
        if (moduloY < 2.0) mask = vec3(1.0);
        else mask = vec3(0.0);
    }

    vec3 maskedColor = finalColor.rgb * mask;
    finalColor.rgb = alphaBlend(vec4(maskedColor, u_maskStr), finalColor).rgb;

    vec3 vignetteEffect = finalColor.rgb * vignette(crtCoords);
    finalColor.rgb = alphaBlend(vec4(vignetteEffect, u_vignetteStr), finalColor).rgb;

    gl_FragColor = finalColor;
}
