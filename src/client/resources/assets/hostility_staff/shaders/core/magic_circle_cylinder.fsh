#version 330

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

layout(std140) uniform HostilityGlobals {
    float time;
};

#define TAU 6.28318530718

const vec3 COLORS[8] = vec3[](
    vec3(0.137, 0.004, 0.024),
    vec3(0.294, 0.000, 0.071),
    vec3(0.416, 0.075, 0.031),
    vec3(0.482, 0.165, 0.071),
    vec3(0.561, 0.102, 0.102),
    vec3(0.761, 0.094, 0.102),
    vec3(0.894, 0.212, 0.165),
    vec3(1.000, 0.361, 0.227)
);

const float WAR_MIX = 0.75;
const float FLAME_EDGE = 0.1;

vec3 war(vec3 color) {
    return mix(COLORS[0], color, WAR_MIX);
}

vec3 palette(float t) {
    float p = t * 6.0;
    int i = clamp(int(floor(p)), 0, 6);
    int i0 = min(i, 5);
    int i1 = min(i + 1, 6);
    float f = fract(p);
    return mix(war(COLORS[i0 + 1]), war(COLORS[i1 + 1]), f);
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
    float flameMask = 1.0 - smoothstep(flameHeight - FLAME_EDGE, flameHeight + FLAME_EDGE, v);
    float intensity = baseIntensity * flameMask;
    fragColor = vec4(color * intensity * vertexColor.a, intensity);
}
