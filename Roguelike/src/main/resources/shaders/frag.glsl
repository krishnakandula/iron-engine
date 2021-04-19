#version 330 core

uniform vec3 color;
in vec2 texCoord;
out vec4 outColor;

uniform sampler2D tex;

void main() {
    outColor = vec4(1.0, 0.0, 0.0, 1.0);
}
