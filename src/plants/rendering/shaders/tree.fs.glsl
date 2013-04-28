#version 330

/* IN */
in vec3 v_Position;
in vec3 v_Normal;
in vec2 v_Texcoords;

/* OUT */
out vec4 glFragColor;

/* UNIFORM */
uniform sampler2D uTex;

/* MAIN */
void main (void) {
	
	glFragColor = texture(uTex, v_Texcoords);
	
} ;
