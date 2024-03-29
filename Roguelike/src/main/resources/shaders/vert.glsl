#version 330 core

uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec4 fragColor;
out vec2 texCoord;

void main() {
    gl_Position = projection * view * vec4(aPos, 1.0);
    texCoord = aTexCoord;
}
