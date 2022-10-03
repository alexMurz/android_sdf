@file:Suppress("unused", "SpellCheckingInspection")

package com.example.spheremarch.math

//////////////////
// X-

// *X--
val Vec3.xxxx: Vec4; inline get() = vec4(x, x, x, x)
val Vec3.xxxy: Vec4; inline get() = vec4(x, x, x, y)
val Vec3.xxxz: Vec4; inline get() = vec4(x, x, x, z)

val Vec3.xxyx: Vec4; inline get() = vec4(x, x, y, x)
val Vec3.xxyy: Vec4; inline get() = vec4(x, x, y, y)
val Vec3.xxyz: Vec4; inline get() = vec4(x, x, y, z)

val Vec3.xxzx: Vec4; inline get() = vec4(x, x, z, x)
val Vec3.xxzy: Vec4; inline get() = vec4(x, x, z, y)
val Vec3.xxzz: Vec4; inline get() = vec4(x, x, z, z)

// *Y--
val Vec3.xyxx: Vec4; inline get() = vec4(x, y, x, x)
val Vec3.xyxy: Vec4; inline get() = vec4(x, y, x, y)
val Vec3.xyxz: Vec4; inline get() = vec4(x, y, x, z)

val Vec3.xyyx: Vec4; inline get() = vec4(x, y, y, x)
val Vec3.xyyy: Vec4; inline get() = vec4(x, y, y, y)
val Vec3.xyyz: Vec4; inline get() = vec4(x, y, y, z)

val Vec3.xyzx: Vec4; inline get() = vec4(x, y, z, x)
val Vec3.xyzy: Vec4; inline get() = vec4(x, y, z, y)
val Vec3.xyzz: Vec4; inline get() = vec4(x, y, z, z)

// *Z--
val Vec3.xzxx: Vec4; inline get() = vec4(x, z, x, x)
val Vec3.xzxy: Vec4; inline get() = vec4(x, z, x, y)
val Vec3.xzxz: Vec4; inline get() = vec4(x, z, x, z)

val Vec3.xzyx: Vec4; inline get() = vec4(x, z, y, x)
val Vec3.xzyy: Vec4; inline get() = vec4(x, z, y, y)
val Vec3.xzyz: Vec4; inline get() = vec4(x, z, y, z)

val Vec3.xzzx: Vec4; inline get() = vec4(x, z, z, x)
val Vec3.xzzy: Vec4; inline get() = vec4(x, z, z, y)
val Vec3.xzzz: Vec4; inline get() = vec4(x, z, z, z)

///////3//////////
// Y-

// *X--
val Vec3.yxxx: Vec4; inline get() = vec4(y, x, x, x)
val Vec3.yxxy: Vec4; inline get() = vec4(y, x, x, y)
val Vec3.yxxz: Vec4; inline get() = vec4(y, x, x, z)

val Vec3.yxyx: Vec4; inline get() = vec4(y, x, y, x)
val Vec3.yxyy: Vec4; inline get() = vec4(y, x, y, y)
val Vec3.yxyz: Vec4; inline get() = vec4(y, x, y, z)

val Vec3.yxzx: Vec4; inline get() = vec4(y, x, z, x)
val Vec3.yxzy: Vec4; inline get() = vec4(y, x, z, y)
val Vec3.yxzz: Vec4; inline get() = vec4(y, x, z, z)

// *Y--
val Vec3.yyxx: Vec4; inline get() = vec4(y, y, x, x)
val Vec3.yyxy: Vec4; inline get() = vec4(y, y, x, y)
val Vec3.yyxz: Vec4; inline get() = vec4(y, y, x, z)

val Vec3.yyyx: Vec4; inline get() = vec4(y, y, y, x)
val Vec3.yyyy: Vec4; inline get() = vec4(y, y, y, y)
val Vec3.yyyz: Vec4; inline get() = vec4(y, y, y, z)

val Vec3.yyzx: Vec4; inline get() = vec4(y, y, z, x)
val Vec3.yyzy: Vec4; inline get() = vec4(y, y, z, y)
val Vec3.yyzz: Vec4; inline get() = vec4(y, y, z, z)

// *Z--
val Vec3.yzxx: Vec4; inline get() = vec4(y, z, x, x)
val Vec3.yzxy: Vec4; inline get() = vec4(y, z, x, y)
val Vec3.yzxz: Vec4; inline get() = vec4(y, z, x, z)

val Vec3.yzyx: Vec4; inline get() = vec4(y, z, y, x)
val Vec3.yzyy: Vec4; inline get() = vec4(y, z, y, y)
val Vec3.yzyz: Vec4; inline get() = vec4(y, z, y, z)

val Vec3.yzzx: Vec4; inline get() = vec4(y, z, z, x)
val Vec3.yzzy: Vec4; inline get() = vec4(y, z, z, y)
val Vec3.yzzz: Vec4; inline get() = vec4(y, z, z, z)


///////3//////////
// Z-

// *X--
val Vec3.zxxx: Vec4; inline get() = vec4(z, x, x, x)
val Vec3.zxxy: Vec4; inline get() = vec4(z, x, x, y)
val Vec3.zxxz: Vec4; inline get() = vec4(z, x, x, z)

val Vec3.zxyx: Vec4; inline get() = vec4(z, x, y, x)
val Vec3.zxyy: Vec4; inline get() = vec4(z, x, y, y)
val Vec3.zxyz: Vec4; inline get() = vec4(z, x, y, z)

val Vec3.zxzx: Vec4; inline get() = vec4(z, x, z, x)
val Vec3.zxzy: Vec4; inline get() = vec4(z, x, z, y)
val Vec3.zxzz: Vec4; inline get() = vec4(z, x, z, z)

// *Y--
val Vec3.zyxx: Vec4; inline get() = vec4(z, y, x, x)
val Vec3.zyxy: Vec4; inline get() = vec4(z, y, x, y)
val Vec3.zyxz: Vec4; inline get() = vec4(z, y, x, z)

val Vec3.zyyx: Vec4; inline get() = vec4(z, y, y, x)
val Vec3.zyyy: Vec4; inline get() = vec4(z, y, y, y)
val Vec3.zyyz: Vec4; inline get() = vec4(z, y, y, z)

val Vec3.zyzx: Vec4; inline get() = vec4(z, y, z, x)
val Vec3.zyzy: Vec4; inline get() = vec4(z, y, z, y)
val Vec3.zyzz: Vec4; inline get() = vec4(z, y, z, z)

// *Z--
val Vec3.zzxx: Vec4; inline get() = vec4(z, z, x, x)
val Vec3.zzxy: Vec4; inline get() = vec4(z, z, x, y)
val Vec3.zzxz: Vec4; inline get() = vec4(z, z, x, z)

val Vec3.zzyx: Vec4; inline get() = vec4(z, z, y, x)
val Vec3.zzyy: Vec4; inline get() = vec4(z, z, y, y)
val Vec3.zzyz: Vec4; inline get() = vec4(z, z, y, z)

val Vec3.zzzx: Vec4; inline get() = vec4(z, z, z, x)
val Vec3.zzzy: Vec4; inline get() = vec4(z, z, z, y)
val Vec3.zzzz: Vec4; inline get() = vec4(z, z, z, z)


///////3//////////
// Vec3

// X--
val Vec3.xxx: Vec3; inline get() = vec3(x, x, x)
val Vec3.xxy: Vec3; inline get() = vec3(x, x, y)
val Vec3.xxz: Vec3; inline get() = vec3(x, x, z)

val Vec3.xyx: Vec3; inline get() = vec3(x, y, x)
val Vec3.xyy: Vec3; inline get() = vec3(x, y, y)
val Vec3.xyz: Vec3; inline get() = vec3(x, y, z)

val Vec3.xzx: Vec3; inline get() = vec3(x, z, x)
val Vec3.xzy: Vec3; inline get() = vec3(x, z, y)
val Vec3.xzz: Vec3; inline get() = vec3(x, z, z)

// Y--
val Vec3.yxx: Vec3; inline get() = vec3(y, x, x)
val Vec3.yxy: Vec3; inline get() = vec3(y, x, y)
val Vec3.yxz: Vec3; inline get() = vec3(y, x, z)

val Vec3.yyx: Vec3; inline get() = vec3(y, y, x)
val Vec3.yyy: Vec3; inline get() = vec3(y, y, y)
val Vec3.yyz: Vec3; inline get() = vec3(y, y, z)

val Vec3.yzx: Vec3; inline get() = vec3(y, z, x)
val Vec3.yzy: Vec3; inline get() = vec3(y, z, y)
val Vec3.yzz: Vec3; inline get() = vec3(y, z, z)

// Z--
val Vec3.zxx: Vec3; inline get() = vec3(z, x, x)
val Vec3.zxy: Vec3; inline get() = vec3(z, x, y)
val Vec3.zxz: Vec3; inline get() = vec3(z, x, z)

val Vec3.zyx: Vec3; inline get() = vec3(z, y, x)
val Vec3.zyy: Vec3; inline get() = vec3(z, y, y)
val Vec3.zyz: Vec3; inline get() = vec3(z, y, z)

val Vec3.zzx: Vec3; inline get() = vec3(z, z, x)
val Vec3.zzy: Vec3; inline get() = vec3(z, z, y)
val Vec3.zzz: Vec3; inline get() = vec3(z, z, z)

// Vec2
// *Z--
val Vec3.xx: Vec2; inline get() = vec2(x, x)
val Vec3.xy: Vec2; inline get() = vec2(x, y)
val Vec3.xz: Vec2; inline get() = vec2(x, z)

val Vec3.yx: Vec2; inline get() = vec2(y, x)
val Vec3.yy: Vec2; inline get() = vec2(y, y)
val Vec3.yz: Vec2; inline get() = vec2(y, z)

val Vec3.zx: Vec2; inline get() = vec2(z, x)
val Vec3.zy: Vec2; inline get() = vec2(z, y)
val Vec3.zz: Vec2; inline get() = vec2(z, z)

