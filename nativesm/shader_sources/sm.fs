#version 320 es
precision highp float;

layout(location=0) in vec3 v_ray_dir;

layout(location=0) out highp vec4 out_color;

layout(push_constant) uniform PushInfo {
    mediump vec4 eye_pos;
    mediump vec4 eye_at;
    mediump vec4 eye_up;
    // Spread, maxDist
    mediump vec4 cfg;
} push;

////////////////////////////////////////////////////
// Structs
////////////////////////////////////////////////////

// Material info
struct Material {
    vec3 diffuse;
    vec3 reflective;
};

// State of ray, distance to closest object and its material
struct RayState {
    float dist;
    Material material;
};

// Result of forward casting. In case of !hit, material is BACKGROUND_MATERIAL
struct RaycastResult {
    bool hit;
    int steps;
    float distance_traveled;
    Material material;
};

////////////////////////////////////////////////////
// Constants
////////////////////////////////////////////////////

const float PI  = 3.14159265;
const float PHI = 0.6180339;

// Ray data
const float R_HIT_MARGIN        = 0.001;
const float R_REFLECT_OFFSET    = 0.1;
const float R_SHADOW_OFFSET     = 0.1;
const int   R_MAX_STEP_COUNT    = 256;
const int   R_MAX_SHADOW_STEP   = 64;
const float R_STEP_RADIUS       = 0.75;

// Color data
const vec3 C_RED   = vec3(1.0, 0.0, 0.0);
const vec3 C_GREEN = vec3(0.0, 1.0, 0.0);
const vec3 C_BLUE  = vec3(0.0, 0.0, 1.0);
const vec3 C_BLACK = vec3(0.0, 0.0, 0.0);
const vec3 C_WHITE = vec3(1.0, 1.0, 1.0);
const vec3 C_GRAY  = vec3(0.5, 0.5, 0.5);

// Material data
const Material M_BACKGROUND = Material(C_BLACK, vec3(0.0));

////////////////////////////////////////////////////
// Material factory
////////////////////////////////////////////////////

Material mat(vec3 col) {
    return Material(col, vec3(0.0));
}

Material mat_ref(vec3 col, vec3 ref) {
    return Material(col, ref);
}

////////////////////////////////////////////////////
// Primitive SD functions
////////////////////////////////////////////////////

float sd_sphere(vec3 p, float r) {
    return length(p) - r;
}

float sd_box(vec3 p, vec3 hs) {
    vec3 d = abs(p) - hs;
    return min(max(d.x, max(d.y, d.z)), 0.0) + length(max(d, 0.0));
}

////////////////////////////////////////////////////
// Modifiers
////////////////////////////////////////////////////

vec3 mod_tr(vec3 p, vec3 tr) {
    return p + tr;
}

vec3 mod_sc(vec3 p, vec3 sc) {
    return p / sc;
}

////////////////////////////////////////////////////
// Operations
////////////////////////////////////////////////////

RayState op_u(RayState world, Material c2, float d2) {
    return (world.dist <= d2) ? world : RayState(d2, c2);
}

RayState op_s(RayState world, float d2) {
    return (world.dist >= -d2) ? world : RayState(-d2, world.material);
}

RayState op_s_inv(RayState world, float d2) {
    return (-world.dist <= d2) ? world : RayState(-d2, world.material);
}

RayState op_i(RayState world, float d2) {
    return (world.dist > d2) ? world : RayState(d2, world.material);
}

////////////////////////////////////////////////////
// Scene composition and SDF's
////////////////////////////////////////////////////

RayState render_floor_plane(RayState world, vec3 point) {
    if (world.dist < point.y) {
        return world;
    }

    // https://www.desmos.com/calculator/gkpafxsyz0
    float arg = 0.1;

    float l = sin(point.x * PI) * cos(point.z * PI + sin(point.x * PI * PHI));
    l = l*l;

    vec3 diff_color = l > arg ? C_GRAY : C_WHITE;

    return RayState(point.y, mat(diff_color));
}

RayState render_rgb_cut_sphere(RayState world, vec3 p) {
    if (sd_sphere(p, 1.1) < world.dist) {
        // Top red
        vec3 q = mod_tr(p, vec3(0.0, -0.75, 0.0));
        vec3 s = vec3(1.1, 0.25, 1.1);
        float b = sd_box(q, s);
        if (b < world.dist) {
            float sd = max(b, sd_sphere(p, 1.0));
            world = op_u(world, mat(C_RED), sd);
        }

        // Middle green
        q = p;
        b = sd_box(q, s);
        if (b < world.dist) {
            float sd = max(b, sd_sphere(p, 1.0));
            world = op_u(world, mat_ref(C_BLACK, vec3(0.3, 0.6, 0.3)), sd);
            // world = op_u(world, mat(C_GREEN), sd);
        }

        // Bottom blue
        q = mod_tr(p, vec3(0.0, 0.75, 0.0));
        b = sd_box(q, s);
        if (b < world.dist) {
            float sd = max(b, sd_sphere(p, 1.0));
            world = op_u(world, mat(C_BLUE), sd);
        }
    }
    return world;
}

RayState map_scene(vec3 p) {
    RayState d = RayState(push.cfg.y, M_BACKGROUND);
    
    d = render_floor_plane(d, mod_tr(p, vec3(0.0, 2.0, 0.0)));

    d = render_rgb_cut_sphere(d, p);
    d = render_rgb_cut_sphere(d, mod_tr(p, vec3(2.5, 0.0, 0.0)));
    d = render_rgb_cut_sphere(d, mod_tr(p, vec3(-2.5, 0.0, 0.0)));

    // Under box
    d = op_u(
        d, 
        mat(C_WHITE),
        sd_box(
            mod_tr(p, vec3(0.0, 1.3, 0.0)), 
            vec3(5.0, 0.2, 5.0)
        )
    );

    return d;
}

////////////////////////////////////////////////////
// Scene dependent math
////////////////////////////////////////////////////

vec3 calc_normal(vec3 pos) {
    vec2 e = vec2(1.0,-1.0)*0.5773*0.0005;
    return normalize( e.xyy*map_scene( pos + e.xyy ).dist + 
					  e.yyx*map_scene( pos + e.yyx ).dist + 
					  e.yxy*map_scene( pos + e.yxy ).dist + 
					  e.xxx*map_scene( pos + e.xxx ).dist );
}

float calc_ao(vec3 pos, vec3 nor) {
	float occ = 0.0;
    float sca = 1.0;
    for(int i=0; i < 5; i++)
    {
        float h = 0.01 + 0.12*float(i)/4.0;
        float d = map_scene(pos + h*nor).dist;
        occ += (h-d)*sca;
        sca *= 0.95;
        if( occ>0.35 ) break;
    }
    return clamp(1.0 - 3.0*occ, 0.0, 1.0) * (0.5+0.5*nor.y);
}

float calc_shadow(vec3 point, vec3 light, float k) {
    vec3 diff = light - point;
    float mint = R_SHADOW_OFFSET;
    float maxt = length(diff);
    vec3 dir = normalize(diff);

    float res = 1.0;
    int step = 0;
    for (float t = mint; t < maxt && step < R_MAX_SHADOW_STEP; step++) {
        float h = map_scene(point + dir*t).dist;
        if (h < R_HIT_MARGIN) {
            return 0.0;
        }
        res = min(res, k*h/t);
        t += h;
    }
    return res;
}

////////////////////////////////////////////////////
// Light math
////////////////////////////////////////////////////

vec3 l_point_light(vec3 p, vec3 norm, vec3 lp, vec3 diff_color, float i, vec3 falloff) {
    vec3 pos_diff = lp - p;
    float dist = length(pos_diff);
    vec3 L = normalize(pos_diff);
    float arg = clamp(i / dist, 0.0, 1.0);

    float lin = 1.0;
    // Dot light (angle of attack)
    lin *= clamp(dot(norm, L), 0.0, 1.0);
    // Intensity based tinting
    lin *= falloff.x + arg*falloff.y + arg*arg*falloff.z;
    // Light shadows
    lin *= calc_shadow(p, lp, 10.0);

    return diff_color * lin;
}

vec3 l_point_light(vec3 p, vec3 norm, vec3 lp, vec3 diff_color, float i) {
    return l_point_light(p, norm, lp, diff_color, i, vec3(0.0, 1.0, 0.0));
}


////////////////////////////////////////////////////
// Rendering
////////////////////////////////////////////////////

RaycastResult raycast_forward(vec3 origin, vec3 dir, float max_dist, int max_step_count) {
    float d = 0.0;
    int step_count = 0;
    while (step_count < max_step_count && d < max_dist) {
        step_count += 1;

        vec3 p = origin + dir * d;
        RayState mapping = map_scene(p);
        float dist = mapping.dist;

        if (abs(dist) < R_HIT_MARGIN) {
            return RaycastResult(
                true,
                step_count,
                d,
                mapping.material
            );
        } else {
            d += dist * R_STEP_RADIUS;
        }
    }

    return RaycastResult(false, step_count, max_dist, M_BACKGROUND);
}

vec3 calc_light(vec3 p, vec3 norm, Material mat) {
    vec3 lin = vec3(1.0);
    lin *= calc_ao(p, norm);
    lin *= l_point_light(p, norm, vec3(1.0, 3.0, 1.0), vec3(1.0), 15.0, vec3(0.0, 0.0, 1.0));

    return mat.diffuse * max(lin, 0.2);
}

vec3 raycast(vec3 origin, vec3 dir) {
    float max_dist = push.cfg.y;
    int max_step_count = R_MAX_STEP_COUNT;

    vec3 ray_filter = C_WHITE;
    vec3 col = C_BLACK;

    // Loop reflections
    for (;;) {
        RaycastResult result = raycast_forward(origin, dir, max_dist, max_step_count);

        if (result.hit) {
            Material mat = result.material;
            float d = result.distance_traveled;
            int step_count = result.steps;
            vec3 p = origin + dir*d;

            vec3 norm = calc_normal(p);

            col += ray_filter * calc_light(p, norm, mat);
            vec3 r = mat.reflective;
            if (length(r) > 0.0) {
                max_dist -= d;
                max_step_count -= step_count;
                origin = p + norm * R_REFLECT_OFFSET;
                dir = reflect(dir, norm);
                ray_filter *= r;
                continue;
            }
        }
        
        return col;
    }
}

void main() {
    vec3 col = raycast(push.eye_pos.xyz, v_ray_dir);
    out_color = vec4(col, 1.0);
}
