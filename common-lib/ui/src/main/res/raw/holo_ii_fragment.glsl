#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

layout(binding = 0) uniform sampler2D ourTextureBase;
layout(binding = 0) uniform sampler2D ourTexture0;

in vec2 v_TextureCoord0;
in float m_distance;
in vec3 incident;

out vec4 FragColor;

vec3 hsv2rgb_smooth( in vec3 c ){
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb); // cubic smoothing
    return c.z * mix( vec3(1.0), rgb, c.y);
}

const float offset = 1.0 / 300.0;
const float PI2 = 6.28318;

void main()
{
    vec4 textureBase = texture(ourTextureBase, v_TextureCoord0);

    vec2 offsets[9] = vec2[](
    vec2(-offset,  offset), // top-left
    vec2( 0.0f,    offset), // top-center
    vec2( offset,  offset), // top-right
    vec2(-offset,  0.0f),   // center-left
    vec2( 0.0f,    0.0f),   // center-center
    vec2( offset,  0.0f),   // center-right
    vec2(-offset, -offset), // bottom-left
    vec2( 0.0f,   -offset), // bottom-center
    vec2( offset, -offset)  // bottom-right
    );

    float x = incident.x;
    float y = incident.y;
    float kernel[] = float[](
                0.0, -1.0, -20.0,
                -1.0, 63.0, -20.0,
                0.0, -1.0, -20.0
            );

    if( abs(x) > abs(y)){
        if( x< 0.0){
            kernel = float[](
            -20.0, -1.0, 0.0,
            -20.0, 63.0, -1.0,
            -20.0, -1.0, 0.0
            );
        }else {
            kernel = float[](
            0.0, -1.0, -20.0,
            -1.0, 63.0, -20.0,
            0.0, -1.0, -20.0
            );
        }
    }else{
        if( y> 0.0){
            kernel = float[](
            -20.0, -20.0, -20.0,
            -1.0, 63.0, -1.0,
             0.0, -1.0, 0.0
            );
        }else {
            kernel = float[](
            0.0, -1.0, 0.0,
            -1.0, 63.0, -1.0,
            -20.0, -20.0, -20.0
            );
        }
    }

    vec3 sampleTex[9];
    for(int i = 0; i < 9; i++)
        sampleTex[i] = vec3(texture(ourTexture0, v_TextureCoord0.st + offsets[i]));

    vec3 col = vec3(0.0);
    for(int i = 0; i < 9; i++)
        col += sampleTex[i] * kernel[i];

    vec3 col1 = hsv2rgb_smooth( col );
    vec3 col2 = vec3(1.0 - col1);
    FragColor = mix(textureBase, vec4(col2, 1.0), m_distance/5.0);
}

