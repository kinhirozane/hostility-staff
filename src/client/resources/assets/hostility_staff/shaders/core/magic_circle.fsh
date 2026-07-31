#version 330

in vec2 texCoord;

out vec4 fragColor;

layout(std140) uniform HostilityGlobals {
    float time;
};

#define PI 3.14159265359
#define TWO_PI 6.28318530718

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
const float RING_RATIO = 0.9;
const float ALPHA_EPSILON = 0.01;

vec2 rotate(vec2 uv, float th) {
    return mat2(cos(th), sin(th), -sin(th), cos(th)) * uv;
}

vec3 getCircle(vec2 p, vec2 rp, float size, vec3 color, float rotationSpeed, float rotationOffset) {
    p += rp;
    p = rotate(p, rotationSpeed * time + rotationOffset);
    float c = step(distance(p, rp), size);
    c -= step(distance(p, rp), size * RING_RATIO);
    return c * color;
}

vec3 getPolynomial(vec2 p, vec2 rp, int sides, float size, vec3 color, float rotationSpeed, float rotationOffset) {
    p -= rp;
    p = rotate(p, rotationSpeed * time + rotationOffset);
    int N = sides;
    float a = atan(p.x, p.y) + PI;
    float r = TWO_PI / float(N);
    float d = cos(floor(0.5 + a / r) * r - a) * length(p);
    float t = 1.0 - step(size * 0.5, d);
    t -= 1.0 - step(size * 0.5 * RING_RATIO, d);
    return t * color;
}

vec3 war(vec3 color) {
    return mix(COLORS[0], color, WAR_MIX);
}

void main() {
    vec2 uv = texCoord;
    vec3 finalColor = vec3(0.0);
    vec3 triangle = war(COLORS[5]);
    vec3 rectangle1 = getPolynomial(uv, vec2(0.0), 4, 1.1, war(COLORS[4]), 1.0, 0.215);
    vec3 circle1 = getCircle(uv, vec2(0.0), 1.0, war(COLORS[6]), 1.0, 0.0);
    vec3 circle2 = getCircle(uv, vec2(0.0), 0.5, war(COLORS[2]), 1.0, 0.0);
    vec3 triangle1 = getPolynomial(uv, vec2(0.0), 3, 1.0, triangle, -1.0, 1.0);
    vec3 triangle2 = getPolynomial(uv, vec2(0.0), 3, 0.5, triangle, -0.33, 0.0);
    vec3 triangle3 = getPolynomial(uv, vec2(0.0), 3, 0.5, triangle, -0.33, 1.0);
    vec3 poly = getPolynomial(uv, vec2(0.0), 8, 1.6, war(COLORS[7]), 0.33, 0.2);
    finalColor = triangle1 + rectangle1 + circle1 + triangle2 + circle2 + poly + triangle3;
    fragColor = vec4(finalColor, step(ALPHA_EPSILON, length(finalColor)));
}
