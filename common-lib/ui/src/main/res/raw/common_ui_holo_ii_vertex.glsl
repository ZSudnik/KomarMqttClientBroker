#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

uniform mat4 uMVPMatrix;
uniform mat4 uTexMatrix;
uniform vec3 u_AccelerometerCoordinates;
in vec4 a_VertexPosition;
in vec4 a_TextureCoordinates;

out vec3 incident;
out float m_distance;
out vec2 v_TextureCoord0;


void main() {

    incident = normalize(u_AccelerometerCoordinates);

    float x = incident.x;
    float y = incident.y;

    v_TextureCoord0 = (uTexMatrix * a_TextureCoordinates).xy;

    m_distance = pow(((x*x)+(y*y))  , 0.5 ) ;

    gl_Position = uMVPMatrix * a_VertexPosition;
}
