#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

uniform mat4 u_MVPMatrix;
in vec2 a_Position;
out vec2 normCord;


float PI = 3.14159;

void main(){

    vec4 nc = u_MVPMatrix * vec4(a_Position, 0.0, 1.0);
    float x = nc.x * 1.1;
    float y = nc.y * 1.2;

    normCord = vec2(x,y);
    gl_Position = nc;
}

