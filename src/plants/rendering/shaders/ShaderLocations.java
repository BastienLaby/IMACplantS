package plants.rendering.shaders;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

public class ShaderLocations {

	public final int PROGRAM_ID;
	public final int LOC_MV;
	public final int LOC_P;
	public final int LOC_TEX;
	
	public ShaderLocations(GLAutoDrawable drawable, String vsFilename, String fsFilename) {
		JOGLShaderProgram program = new JOGLShaderProgram(drawable, vsFilename, fsFilename);
		GL3 gl = drawable.getGL().getGL3();
		this.PROGRAM_ID = program.getProgram();
		this.LOC_MV = gl.glGetUniformLocation(PROGRAM_ID, "uniform_MV");
		this.LOC_P = gl.glGetUniformLocation(PROGRAM_ID, "uniform_P");
		this.LOC_TEX = gl.glGetUniformLocation(PROGRAM_ID, "uTex");
	}
	
}
