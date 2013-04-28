#version 330

in vec3 v_Position;
in vec3 v_Normal;
in vec2 v_Texcoords;

/* UNIFORM */
uniform sampler2D uTex;

/* OUT */
out vec4 glFragColor;

/* MAIN */
void main (void) {

	glFragColor = vec4(vec2(v_Texcoords), 0.0f, 1.0f);
	glFragColor = texture(uTex, vec2(v_Texcoords));
	
} ;
