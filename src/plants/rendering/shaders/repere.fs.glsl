#version 330

/* IN */
in vec3 v_Position;
in vec3 v_Normal;

/* OUT */
out vec4 glFragColor;

/* UNIFORM */
uniform sampler2D uTex;

/* MAIN */
void main (void) {
	
	glFragColor = vec4(v_Normal, 0f);
	
} ;
