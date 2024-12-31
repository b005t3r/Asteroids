#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform sampler2D u_texture;
uniform sampler2D u_blurTexture;

uniform vec2 u_pixelSize;
uniform float u_seconds;

uniform float u_bleedDist;
uniform float u_bleedStr;
uniform float u_blurStr;
uniform float u_rgbMaskSub;
uniform float u_rgbMaskSep;
uniform float u_rgbMaskStr;
uniform int u_colorNoiseMode;
uniform float u_colorNoiseStr;
uniform int u_monoNoiseMode;
uniform float u_monoNoiseScale;
uniform float u_monoNoiseStr;

uniform mat4 u_colorMat;

uniform vec3 u_minLevels;
uniform vec3 u_maxLevels;
uniform vec3 u_blackPoint;
uniform vec3 u_whitePoint;

uniform float u_interWidth;
uniform float u_interSpeed;
uniform float u_interStr;
uniform float u_interSplit;

uniform float u_aberStr;

varying vec2 v_texCoords;
varying vec4 v_color;

vec4 alphaBlend(vec4 top, vec4 bottom) {
    vec4 result;
    result.a = top.a + bottom.a * (1.0 - top.a);
    result.rgb = (top.rgb * top.a + bottom.rgb * bottom.a * (1.0 - top.a)) / result.a;
    return result;
}

vec4 blur(vec2 uv) {
    if (u_aberStr == 0.0) {
        return texture2D(u_blurTexture, uv);
    } else {
        return vec4(
            texture2D(u_blurTexture, uv + vec2(-u_pixelSize.x * u_aberStr, 0.0)).r,
            texture2D(u_blurTexture, uv).g,
            texture2D(u_blurTexture, uv + vec2(u_pixelSize.x * u_aberStr, 0.0)).b,
            1.0
        );
    }
}

vec4 bleed(vec2 uv) {
    vec4 a = blur(uv + vec2(0.0, u_bleedDist * u_pixelSize.y));
    vec4 b = blur(uv + vec2(0.0, -u_bleedDist * u_pixelSize.y));
    vec4 c = blur(uv + vec2(u_bleedDist * u_pixelSize.x, 0.0));
    vec4 d = blur(uv + vec2(-u_bleedDist * u_pixelSize.x, 0.0));
    return max(max(a, b), max(c, d));
}

float noise(float n) {
    return fract(cos(n * 89.42) * 343.42);
}

vec3 interference(vec2 coord, vec3 screen) {
    screen.r += sin((u_interSplit * u_pixelSize.y + coord.y / (u_interWidth * u_pixelSize.y) + (u_seconds * u_interSpeed))) * u_interStr;
    screen.g += sin((coord.y / (u_interWidth * u_pixelSize.y) + (u_seconds * u_interSpeed))) * u_interStr;
    screen.b += sin((-u_interSplit + coord.y / (u_interWidth * u_pixelSize.y) + (u_seconds * u_interSpeed))) * u_interStr;
    return clamp(screen, vec3(0.0), vec3(1.0));
}

void main() {
    float alpha = 0.9;
    gl_FragColor = (1.0 - alpha) * texture2D(u_texture, v_texCoords) + alpha * texture2D(u_blurTexture, v_texCoords);

//    vec4 zero = vec4(0.0);
//    vec4 one = vec4(1.0);
//
//    vec4 base = texture2D(u_texture, v_texCoords);
//    vec4 blurred = blur(v_texCoords);
//    vec4 bleeded = bleed(v_texCoords);
//    vec4 finalColor;

//    finalColor.a = 1.0;
//    vec3 tmp;
//
//    // 1. Lighten blend blurred with base
//    tmp = max(base.rgb, blurred.rgb);
//    finalColor.rgb = alphaBlend(vec4(tmp, u_blurStr), blurred).rgb;
//
//    // 2. Lighten blend bleeded with result
//    tmp = max(bleeded.rgb, finalColor.rgb);
//    finalColor.rgb = alphaBlend(vec4(tmp, u_bleedStr), finalColor).rgb;
//
//    float delta = mod(u_seconds, 60.0);
//
//    // 3. Add color noise
//    vec3 colorNoise = vec3(
//        noise(sin(v_texCoords.x / u_pixelSize.x) * v_texCoords.y / u_pixelSize.y + delta),
//        noise(sin(v_texCoords.y / u_pixelSize.y) * v_texCoords.x / u_pixelSize.x + delta),
//        noise(sin(v_texCoords.x / u_pixelSize.x) * sin(v_texCoords.y / u_pixelSize.y) + delta)
//    );
//
//    if (u_colorNoiseMode == 0)
//        tmp = finalColor.rgb + colorNoise;
//    else if (u_colorNoiseMode == 1)
//        tmp = finalColor.rgb - colorNoise;
//    else if (u_colorNoiseMode == 2)
//        tmp = finalColor.rgb * colorNoise;
//    else if (u_colorNoiseMode == 3)
//        tmp = finalColor.rgb / colorNoise;
//    else if (u_colorNoiseMode == 4)
//        tmp = max(colorNoise, finalColor.rgb);
//    else if (u_colorNoiseMode == 5)
//        tmp = min(colorNoise, finalColor.rgb);
//
//    tmp = clamp(tmp, zero.rgb, one.rgb);
//    finalColor.rgb = alphaBlend(vec4(tmp, u_colorNoiseStr), finalColor).rgb;
//
//    // 4. Add monochromatic noise
//    float monoNoiseVal = noise(sin(v_texCoords.x / u_pixelSize.x) * v_texCoords.y / u_pixelSize.y + delta);
//    vec3 monoNoise = vec3(monoNoiseVal);
//
//    if (u_monoNoiseMode == 0)
//        tmp = finalColor.rgb + monoNoise;
//    else if (u_monoNoiseMode == 1)
//        tmp = finalColor.rgb - monoNoise;
//    else if (u_monoNoiseMode == 2)
//        tmp = finalColor.rgb * monoNoise;
//    else if (u_monoNoiseMode == 3)
//        tmp = finalColor.rgb / monoNoise;
//    else if (u_monoNoiseMode == 4)
//        tmp = max(monoNoise, finalColor.rgb);
//    else if (u_monoNoiseMode == 5)
//        tmp = min(monoNoise, finalColor.rgb);
//
//    tmp = clamp(tmp, zero.rgb, one.rgb);
//    finalColor.rgb = alphaBlend(vec4(tmp, u_monoNoiseStr), finalColor).rgb;
//
//    // 5. RGB mask
//    float modulo = mod(floor(v_texCoords.x / u_pixelSize.x), 3.0);
//    tmp = finalColor.rgb;
//
//    if (modulo == 0.0)
//        tmp -= vec3(0.0, u_rgbMaskSub * u_rgbMaskSep, u_rgbMaskSub * u_rgbMaskSep * 2.0);
//    else if (modulo == 1.0)
//        tmp -= vec3(u_rgbMaskSub * u_rgbMaskSep, 0.0, u_rgbMaskSub * u_rgbMaskSep);
//    else
//        tmp -= vec3(u_rgbMaskSub * u_rgbMaskSep * 2.0, u_rgbMaskSub * u_rgbMaskSep, 0.0);
//
//    finalColor.rgb = alphaBlend(vec4(tmp, u_rgbMaskStr), finalColor).rgb;
//
//    // 6. Interference
//    finalColor.rgb = interference(v_texCoords, finalColor.rgb);
//
//    // 7. Color adjustment
//    finalColor = u_colorMat * finalColor;
//
//    // 8. Levels adjustment
//    finalColor.rgb = mix(
//        zero.rgb,
//        one.rgb,
//        (finalColor.rgb - u_minLevels) / (u_maxLevels - u_minLevels)
//    );
//
//    finalColor.rgb = clamp(finalColor.rgb, u_blackPoint, u_whitePoint);

//    gl_FragColor = finalColor;
}
