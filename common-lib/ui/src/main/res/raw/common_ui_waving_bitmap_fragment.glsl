#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_Sampler;
uniform float u_Time;
in vec2 normCord;

out vec4 FragColor;

float PI = 3.14159;
float PI2 = 6.28318;
mat3 MATR = mat3( 0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 1.0);

void main() {

    float x = normCord.x ;
    float y = normCord.y;


//    y  += 0.1*sin( 1.0* PI *( mod(x + u_Time, PI2) ) ) ;
    y  += 0.1*sin( 1.0* PI *( x + u_Time ) ) ;
    if (x < -1.0 || x > 1.0 || y < -1.0 || y > 1.0 ){
        discard;
//        FragColor = vec4(1.0,1.0,0.0,1.0);
    }else {
        vec2 v_UV = (MATR * vec3(x, y, 1.0)).xy;
        FragColor = texture(u_Sampler, v_UV);
    }
}


