#version 320 es

#ifdef GL_ES
precision mediump float;
#endif

in vec2 v_TextureCoordinates;
//in vec2 v_AccelerometerCoordinates;
uniform sampler2D sTexture;
out vec4 FragColor;
void main() {

    FragColor = texture(sTexture, v_TextureCoordinates);
}