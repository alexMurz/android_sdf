@file:Suppress("unused", "SpellCheckingInspection")

package com.example.spheremarch.math

//////////////////
// X-

val Vec2.xxxx: Vec4; inline get() = vec4(x, x, x, x)
val Vec2.xxxy: Vec4; inline get() = vec4(x, x, x, y)

val Vec2.xxyx: Vec4; inline get() = vec4(x, x, y, x)
val Vec2.xxyy: Vec4; inline get() = vec4(x, x, y, y)

val Vec2.xyxx: Vec4; inline get() = vec4(x, y, x, x)
val Vec2.xyxy: Vec4; inline get() = vec4(x, y, x, y)

val Vec2.xyyx: Vec4; inline get() = vec4(x, y, y, x)
val Vec2.xyyy: Vec4; inline get() = vec4(x, y, y, y)

val Vec2.yxxx: Vec4; inline get() = vec4(y, x, x, x)
val Vec2.yxxy: Vec4; inline get() = vec4(y, x, x, y)

val Vec2.yxyx: Vec4; inline get() = vec4(y, x, y, x)
val Vec2.yxyy: Vec4; inline get() = vec4(y, x, y, y)

val Vec2.yyxx: Vec4; inline get() = vec4(y, y, x, x)
val Vec2.yyxy: Vec4; inline get() = vec4(y, y, x, y)

val Vec2.yyyx: Vec4; inline get() = vec4(y, y, y, x)
val Vec2.yyyy: Vec4; inline get() = vec4(y, y, y, y)

////

val Vec2.xxx: Vec3; inline get() = vec3(x, x, x)
val Vec2.xxy: Vec3; inline get() = vec3(x, x, y)

val Vec2.xyx: Vec3; inline get() = vec3(x, y, x)
val Vec2.xyy: Vec3; inline get() = vec3(x, y, y)

val Vec2.yxx: Vec3; inline get() = vec3(y, x, x)
val Vec2.yxy: Vec3; inline get() = vec3(y, x, y)

val Vec2.yyx: Vec3; inline get() = vec3(y, y, x)
val Vec2.yyy: Vec3; inline get() = vec3(y, y, y)

////

val Vec2.xx: Vec2; inline get() = vec2(x, x)
val Vec2.xy: Vec2; inline get() = vec2(x, y)

val Vec2.yx: Vec2; inline get() = vec2(y, x)
val Vec2.yy: Vec2; inline get() = vec2(y, y)

