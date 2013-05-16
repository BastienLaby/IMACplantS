#version 330

in vec3 v_Position;
in vec3 v_Normal;
in vec2 v_Texcoords;

/* UNIFORM */
uniform sampler2D uTex;

/* OUT */
out vec4 glFragColor;

/* LIGHTS */
struct DirectionalLight {
                vec4 dir;
                vec3 intensity;
};

vec3 lambertLight(DirectionalLight light, vec3 normal) {
    float cosA = dot(normalize(light.dir), vec4(normalize(normal), 1.0f));
    float A = 0.4*acos(cosA);
    return A * light.intensity;
}

/* MAIN */
void main (void) {

	DirectionalLight light;
	light.dir = vec4(-0.5f, -1.0f, 0.5f, 0.0f);
	light.intensity = vec3(1, 1, 1);

	vec4 lb = vec4(lambertLight(light, v_Normal), 1.0f);
	
	vec4 tex = texture(uTex, v_Texcoords);
	
	glFragColor = tex * lb;
	
} ;
