#version 330

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

layout(std140) uniform HostilityGlobals {
    float time;
};

#define TAU 6.28318530718

vec3 war(vec3 color) {
    vec3 c1 = vec3(35.0 / 255.0, 1.0 / 255.0, 6.0 / 255.0);
    return mix(c1, color, 0.75);
}

vec3 palette(float t) {
    vec3 c0 = war(vec3(75.0 / 255.0, 0.0 / 255.0, 18.0 / 255.0));
    vec3 c1 = war(vec3(106.0 / 255.0, 19.0 / 255.0, 8.0 / 255.0));
    vec3 c2 = war(vec3(123.0 / 255.0, 42.0 / 255.0, 18.0 / 255.0));
    vec3 c3 = war(vec3(143.0 / 255.0, 26.0 / 255.0, 26.0 / 255.0));
    vec3 c4 = war(vec3(194.0 / 255.0, 24.0 / 255.0, 26.0 / 255.0));
    vec3 c5 = war(vec3(228.0 / 255.0, 54.0 / 255.0, 42.0 / 255.0));
    vec3 c6 = war(vec3(1.0, 92.0 / 255.0, 58.0 / 255.0));
    float p = t * 6.0;
    int i = int(floor(p));
    float f = fract(p);
    if (i == 0) return mix(c0, c1, f);
    if (i == 1) return mix(c1, c2, f);
    if (i == 2) return mix(c2, c3, f);
    if (i == 3) return mix(c3, c4, f);
    if (i == 4) return mix(c4, c5, f);
    if (i == 5) return mix(c5, c6, f);
    return c6;
}

void main() {
    float u = texCoord.x;
    float v = texCoord.y;
    vec3 color = palette(u);
    float heightFade = v;
    float spiral = sin(u * TAU + v * TAU * 3.0 + time * 2.0) * 0.5 + 0.5;
    float pulse = sin(v * TAU * 4.0 - time * 3.0) * 0.5 + 0.5;
    float glow = sin(u * TAU * 6.0 + time * 1.5) * 0.5 + 0.5;
    float baseIntensity = heightFade * (spiral * 0.5 + pulse * 0.3 + glow * 0.2);
    float flameNoise = sin(u * TAU * 5.0 + time * 3.0) * 0.3 + sin(u * TAU * 8.0 + time * 4.7) * 0.2 + sin(u * TAU * 13.0 + time * 1.3) * 0.15;
    float flameHeight = 0.5 + flameNoise * 0.7;
    float flameMask = 1.0 - smoothstep(flameHeight - 0.1, flameHeight + 0.1, v);
    float intensity = baseIntensity * flameMask;
    fragColor = vec4(color * intensity, vertexColor.a * intensity);
}
