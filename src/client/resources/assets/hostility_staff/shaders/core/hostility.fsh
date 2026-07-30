#version 330

layout(std140) uniform HostilityGlobals {
    float time;
};

uniform sampler2D Sampler0;

in vec2 texCoord;

out vec4 fragColor;

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

void main() {
    vec4 mask = texture(Sampler0, texCoord);
    if (mask.a < 0.01) discard;
    float t = clamp(texCoord.x * 0.6 + texCoord.y * 0.4 + sin(time * 3.0) * 0.3, 0.0, 1.0);
    float fi = t * 7.0;
    int i0 = int(fi);
    int i1 = min(i0 + 1, 7);
    float f = fract(fi);
    vec3 gradient = mix(COLORS[i0], COLORS[i1], f);
    fragColor = vec4(gradient, mask.a);
}
