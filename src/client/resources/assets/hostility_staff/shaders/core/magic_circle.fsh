#version 330

in vec2 texCoord;

out vec4 fragColor;

layout(std140) uniform HostilityGlobals {
    float time;
};

#define PI 3.14159265359
#define TWO_PI 6.28318530718

vec2 rotate(vec2 uv, float th) {
    return mat2(cos(th), sin(th), -sin(th), cos(th)) * uv;
}

vec3 getCircle(vec2 p, vec2 rp, float size, vec3 color, float rotationSpeed, float rotationOffset) {
    p += rp;
    p = rotate(p, rotationSpeed * time + rotationOffset);
    float c = step(distance(p, rp), size);
    c -= step(distance(p, rp), size * 0.9);
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
    t -= 1.0 - step(size * 0.5 * 0.9, d);
    return t * color;
}

vec3 war(vec3 color) {
    vec3 c1 = vec3(35.0 / 255.0, 1.0 / 255.0, 6.0 / 255.0);
    return mix(c1, color, 0.75);
}

void main() {
    vec2 uv = texCoord;
    vec3 finalColor = vec3(0.0);
    vec3 c2 = vec3(75.0 / 255.0, 0.0 / 255.0, 18.0 / 255.0);
    vec3 c3 = vec3(106.0 / 255.0, 19.0 / 255.0, 8.0 / 255.0);
    vec3 c4 = vec3(123.0 / 255.0, 42.0 / 255.0, 18.0 / 255.0);
    vec3 c5 = vec3(143.0 / 255.0, 26.0 / 255.0, 26.0 / 255.0);
    vec3 c6 = vec3(194.0 / 255.0, 24.0 / 255.0, 26.0 / 255.0);
    vec3 c7 = vec3(228.0 / 255.0, 54.0 / 255.0, 42.0 / 255.0);
    vec3 c8 = vec3(1.0, 92.0 / 255.0, 58.0 / 255.0);
    vec3 triangle = war(c6);
    vec3 rectangle1 = getPolynomial(uv, vec2(0.0), 4, 1.1, war(c5), 1.0, 0.215);
    vec3 circle1 = getCircle(uv, vec2(0.0), 1.0, war(c7), 1.0, 0.0);
    vec3 circle2 = getCircle(uv, vec2(0.0), 0.5, war(c3), 1.0, 0.0);
    vec3 triangle1 = getPolynomial(uv, vec2(0.0), 3, 1.0, triangle, -1.0, 1.0);
    vec3 triangle2 = getPolynomial(uv, vec2(0.0), 3, 0.5, triangle, -0.33, 0.0);
    vec3 triangle3 = getPolynomial(uv, vec2(0.0), 3, 0.5, triangle, -0.33, 1.0);
    vec3 poly = getPolynomial(uv, vec2(0.0), 8, 1.6, war(c8), 0.33, 0.2);
    finalColor = triangle1 + rectangle1 + circle1 + triangle2 + circle2 + poly + triangle3;
    fragColor = vec4(finalColor, 1.0);
}
