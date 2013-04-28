package plants.rendering.shaders;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

public class TreeShaderLocations extends ShaderLocations {

	public final int LOC_MV_CURRENT;
	public final int LOC_MV_PARENT;
	public final int LOC_MV_BROTHER;
	public final int LOC_S_CURRENT;
	public final int LOC_S_PARENT;
	public final int LOC_S_BROTHER;
	
	public TreeShaderLocations(GLAutoDrawable drawable, String vsFilename, String fsFilename) {
		super(drawable, vsFilename, fsFilename);
		GL3 gl = drawable.getGL().getGL3();
		this.LOC_MV_CURRENT = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_MVc");
		this.LOC_MV_PARENT = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_MVp");
		this.LOC_MV_BROTHER = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_MVb");
		this.LOC_S_CURRENT = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_Sc");
		this.LOC_S_PARENT = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_Sp");
		this.LOC_S_BROTHER = gl.glGetUniformLocation(super.PROGRAM_ID, "uniform_Sb");
	}

}
