#version 330

layout(location = 0) in vec4 attribute_Position;
layout(location = 1) in vec3 attribute_Normal;

out vec3 v_Position;
out vec3 v_Normal;

uniform mat4 uniform_MV = mat4(1.f);
uniform mat4 uniform_P = mat4(1.f);

uniform sampler2D uTex;

void main(void) {  
	
	v_Position = vec3(gl_Position);
	v_Normal = attribute_Normal;
	
	gl_Position = uniform_P * uniform_MV * vec4(attribute_Position);
	
};