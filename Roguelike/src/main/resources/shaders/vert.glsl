#version 330 core

uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 pos;

out vec4 fragColor;
out vec2 texCoord;

void main() {
    gl_Position = projection * view * vec4(pos, 1.0);
}
