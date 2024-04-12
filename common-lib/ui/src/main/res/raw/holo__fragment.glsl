#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

layout(binding = 0) uniform sampler2D ourTexture0;
layout(binding = 1) uniform sampler2D ourTexture1;

in vec2 v_TextureCoord0;
in vec2 v_TextureCoord1;
in float m_distance;

out vec4 FragColor;


void main() {

    vec4 texture0 = texture(ourTexture0, v_TextureCoord0);
    vec4 texture1 = texture(ourTexture1, v_TextureCoord1);

    FragColor = mix(texture0, texture1, m_distance); // 0.0 .. 1.0
}
