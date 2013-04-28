#version 330

layout(location = 0) in vec4 attribute_PositionC;
layout(location = 1) in vec4 attribute_PositionP;
layout(location = 2) in vec4 attribute_PositionB;
layout(location = 3) in vec3 attribute_Normal;
layout(location = 4) in vec2 attribute_Texcoords;

out vec3 v_Position;
out vec3 v_Normal;
out vec2 v_Texcoords;

uniform mat4 uniform_MV = mat4(1.f);
uniform mat4 uniform_MVc = mat4(1.f);
uniform mat4 uniform_MVp = mat4(1.f);
uniform mat4 uniform_MVb = mat4(1.f);
uniform mat4 uniform_P = mat4(1.f);
uniform mat4 uniform_Sc = mat4(1.f);
uniform mat4 uniform_Sp = mat4(1.f);
uniform mat4 uniform_Sb = mat4(1.f);

uniform sampler2D uTex;

void main(void) {  
	
	vec3 positionC = vec3(uniform_MVc * uniform_Sc * vec4(attribute_PositionC.xyz, 1.f));
	vec3 positionP = vec3(uniform_MVp * uniform_Sp * vec4(attribute_PositionP.xyz, 1.f));
	vec3 positionB = vec3(uniform_MVb * uniform_Sb * vec4(attribute_PositionB.xyz, 1.f));
	
	gl_Position.x = attribute_PositionC.w * positionC.x + attribute_PositionP.w * positionP.x + attribute_PositionB.w * positionB.x;
	gl_Position.y = attribute_PositionC.w * positionC.y + attribute_PositionP.w * positionP.y + attribute_PositionB.w * positionB.y;
	gl_Position.z = attribute_PositionC.w * positionC.z + attribute_PositionP.w * positionP.z + attribute_PositionB.w * positionB.z;
	gl_Position.w = 1.f;
	
	gl_Position = uniform_P * uniform_MV * gl_Position;
	gl_Position = uniform_P * uniform_MV * vec4(positionC.xyz, 1.0f);
	
	v_Position = vec3(gl_Position);
	v_Normal = attribute_Normal;
	v_Texcoords = attribute_Texcoords;
	
	//gl_Position = uniform_P * uniform_MV * uniform_MVc * vec4(attribute_PositionC);
	
};