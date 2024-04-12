#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

uniform mat4 uMVPMatrix;
uniform mat4 uTexMatrix;
//uniform vec3 u_AccelerometerCoordinates;
in vec4 a_VertexPosition;
in vec4 a_TextureCoordinates;
out vec2 v_TextureCoordinates;
//out vec2 v_AccelerometerCoordinates;
void main() {
    gl_Position = uMVPMatrix * a_VertexPosition;
    v_TextureCoordinates = (uTexMatrix * a_TextureCoordinates).xy;
//    v_AccelerometerCoordinates = normalize(u_AccelerometerCoordinates).xy;
}
