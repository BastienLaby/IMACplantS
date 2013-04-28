#version 330

/* IN */
in vec3 v_Position;
in vec3 v_Normal;
in vec2 v_Texcoords;

/* OUT */
out vec4 fFragColor;

/* UNIFORM */
uniform sampler2D uTex;

/* STRUCT */
struct DirectionalLight {
                vec4 dir;
                vec3 intensity;
};

/* PROTO */
vec3 lambertLight(DirectionalLight light, vec3 normal);

/* MAIN */
void main (void) {

	DirectionalLight light;
	light.dir = vec4(-0.5f, -1.0f, 1.0f, 0.0f);
	light.intensity = vec3(1, 0.9, 0.8);
	
	vec4 texel = texture(uTex, v_Texcoords) * vec4(lambertLight(light, v_Normal), 1.0f);
    if(texel.a <0.5){
        discard;
    }
    fFragColor = texel;
    
} ;

vec3 lambertLight(DirectionalLight light, vec3 normal) {
    float cosA = dot(normalize(light.dir), vec4(normalize(normal), 1.0f));
    float A = 0.5 * acos(cosA);
    return A * light.intensity;
}