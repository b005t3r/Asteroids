#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

uniform sampler2D u_texture;

uniform vec2 u_pixelSize;
uniform float u_sigma;
uniform vec3 u_kernel; // x = cornerWeight, y = edgeWeight, z = blurZ

varying vec2 v_texCoords;
varying vec4 v_color;

vec4 blur(vec2 uv, float sigma) {
    vec3 finalColor = vec3(0.0);

    float cornerWeight = u_kernel.x * u_kernel.x;
    float edgeWeight = u_kernel.x * u_kernel.y;
    float centerWeight = u_kernel.y * u_kernel.y;

    finalColor += texture2D(u_texture, uv + vec2(-u_pixelSize.x, -u_pixelSize.y)).rgb * cornerWeight;
    finalColor += texture2D(u_texture, uv + vec2(0.0, -u_pixelSize.y)).rgb * edgeWeight;
    finalColor += texture2D(u_texture, uv + vec2(u_pixelSize.x, -u_pixelSize.y)).rgb * cornerWeight;

    finalColor += texture2D(u_texture, uv + vec2(-u_pixelSize.x, 0.0)).rgb * edgeWeight;
    finalColor += texture2D(u_texture, uv).rgb * centerWeight;
    finalColor += texture2D(u_texture, uv + vec2(u_pixelSize.x, 0.0)).rgb * edgeWeight;

    finalColor += texture2D(u_texture, uv + vec2(-u_pixelSize.x, u_pixelSize.y)).rgb * cornerWeight;
    finalColor += texture2D(u_texture, uv + vec2(0.0, u_pixelSize.y)).rgb * edgeWeight;
    finalColor += texture2D(u_texture, uv + vec2(u_pixelSize.x, u_pixelSize.y)).rgb * cornerWeight;

    return vec4(finalColor / vec3(u_kernel.z), 1.0);
}

void main() {
    gl_FragColor = blur(v_texCoords, u_sigma) * v_color;
}
