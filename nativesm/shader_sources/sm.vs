#version 320 es

layout(location=0) in vec2 a_pos;

layout(location=0) out vec3 v_ray_dir;

layout(push_constant) uniform PushInfo {
    mediump vec4 eye_pos;
    mediump vec4 eye_at;
    mediump vec4 eye_up;
    // Spread, maxDist
    mediump vec4 cfg;
} push;

void main() {
    vec3 dir = normalize(push.eye_at.xyz - push.eye_pos.xyz);
    vec2 sp = a_pos.xy * push.cfg.x;
    vec3 dx = normalize(cross(dir, push.eye_up.xyz));
    vec3 dy = normalize(cross(dir, dx));
    v_ray_dir = normalize(dir + dx*sp.x + dy*sp.y);

    gl_Position = vec4(a_pos.x, -a_pos.y, 0.0, 1.0);
}
