#version 330

layout(location = 0) in vec3 attribute_Position;
layout(location = 1) in vec3 attribute_Normal;
layout(location = 2) in vec2 attribute_Texcoords;

out vec3 v_Position;
out vec3 v_Normal;
out vec2 v_Texcoords;

uniform mat4 uniform_MVc = mat4(1.f);
uniform mat4 uniform_MV = mat4(1.f);
uniform mat4 uniform_P = mat4(1.f);

uniform sampler2D uTex;

void main(void) {  
	
	mat4 MVP = uniform_P * uniform_MV;
	gl_Position = MVP * uniform_MVc * vec4(attribute_Position, 1.0f);
	v_Position = attribute_Position;
	v_Normal = attribute_Normal;
	v_Texcoords = attribute_Texcoords;
	
};