package plants.rendering.drawable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import plants.utils.Utils;


public class Cube implements Drawable {
	
	private VertexShape[] vertices;
	private final static int NB_VERTICES = 36;
	private final static int DRAWING_MODE = GL3.GL_TRIANGLES;
	private int vao;
	
	/* Create cube whith a size of 1, center on the origin */
	public Cube(GL3 gl) {
		this.vertices = new VertexShape[NB_VERTICES];
		this.createData();
		this.createGLRessources(gl);
	}
	
	private void createData() {
		
		// bottom side
		this.vertices[0] = new VertexShape(-0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 1.0f);
		this.vertices[1] = new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 1.0f);
		this.vertices[2] = new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 0.666666f);
		
		this.vertices[3] = new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 0.666666f);
		this.vertices[4] = new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.25f, 0.666666f);
		this.vertices[5] = new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.5f, 1.0f);
		
		// top side
		this.vertices[6] = new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.333333f);
		this.vertices[7] = new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.0f);
		this.vertices[8] = new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.0f);
		
		this.vertices[9] = new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.0f);
		this.vertices[10] = new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.25f, 0.333333f);
		this.vertices[11] = new VertexShape(0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.333333f);
		
		// left side
		this.vertices[12] = new VertexShape(-0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.25f, 0.333333f);
		this.vertices[13] = new VertexShape(-0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.666666f);	    		
		this.vertices[14] = new VertexShape(-0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,  0.25f, 0.666666f);
		
		this.vertices[15] = new VertexShape(-0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.666666f);
		this.vertices[16] = new VertexShape(-0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.25f, 0.333333f);
		this.vertices[17] = new VertexShape(-0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.333333f);
		
		// right side
		this.vertices[18] = new VertexShape(0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.666666f);
		this.vertices[19] = new VertexShape(0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.666666f);
		this.vertices[20] = new VertexShape(0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.333333f);    		
		
		this.vertices[21] = new VertexShape(0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.333333f);
		this.vertices[22] = new VertexShape(0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f, 0.333333f);
		this.vertices[23] = new VertexShape(0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.75f, 0.666666f);
		
		// front side
		this.vertices[24] = new VertexShape(-0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.666666f);
		this.vertices[25] = new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.666666f);
		this.vertices[26] = new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.333333f);
		
		this.vertices[27] = new VertexShape(0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.333333f);
		this.vertices[28] = new VertexShape(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.333333f);
		this.vertices[29] = new VertexShape(0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 0.666666f);
		
		// rear side
		this.vertices[30] = new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.333333f);
		this.vertices[31] = new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.666666f);
		this.vertices[32] = new VertexShape(-0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.666666f);
		
		this.vertices[33] = new VertexShape(0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.666666f);
		this.vertices[34] = new VertexShape(-0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.333333f);
		this.vertices[35] = new VertexShape(0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.75f, 0.333333f);
	}
	
	private void createGLRessources(GL3 gl) {
		
		// Generate VBO
		IntBuffer bufVBO = IntBuffer.allocate(1);
		gl.glGenBuffers(1, bufVBO);
		final int vbo = bufVBO.get(0);
				
		// Bind VBO
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		// Create data
		float[] data = getData();
		FloatBuffer bufferdata = FloatBuffer.allocate(data.length);
		bufferdata.put(data);
		bufferdata.position(0);
		
		// Send data into VBO
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, data.length*Utils.SIZEOF_FLOAT, bufferdata, GL3.GL_STATIC_DRAW);
		
		// Unbind VBO
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Generate VAO
		IntBuffer bufVAO = IntBuffer.allocate(1);
		gl.glGenVertexArrays(1, bufVAO);
		vao = bufVAO.get(0);		
		
		// Bind VAO
		gl.glBindVertexArray(vao);
		
		// Active vertex attributes
		gl.glEnableVertexAttribArray(Utils.SKYBOXSHADER_POSITION_LOCATION); // position
		gl.glEnableVertexAttribArray(Utils.SKYBOXSHADER_NORMAL_LOCATION); // normals
		gl.glEnableVertexAttribArray(Utils.SKYBOXSHADER_TEXCOORDS_LOCATION); // texcoords
		
		// Specify vbo data
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		gl.glVertexAttribPointer( Utils.SKYBOXSHADER_POSITION_LOCATION, // attribute index
								  VertexShape.NB_COMPONENTS_POSITION, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.SIZEOF_VERTEX,
								  VertexShape.OFFSET_POSITION * Utils.SIZEOF_FLOAT);
		System.out.println("offset texcoord : " + VertexShape.SIZEOF_VERTEX );
		gl.glVertexAttribPointer( Utils.SKYBOXSHADER_NORMAL_LOCATION, // attribute index
								  VertexShape.NB_COMPONENTS_NORMAL, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.SIZEOF_VERTEX,
								  VertexShape.OFFSET_NORMAL * Utils.SIZEOF_FLOAT);
		gl.glVertexAttribPointer( Utils.SKYBOXSHADER_TEXCOORDS_LOCATION, // attribute index
								  VertexShape.NB_COMPONENTS_TEXCOORDS, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.SIZEOF_VERTEX,
								  VertexShape.OFFSET_TEXCOORDS * Utils.SIZEOF_FLOAT);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Unbind VAO
		gl.glBindVertexArray(0);
		
	}
	
	/* Return data as a float array containing all float data interlaced */
	private float[] getData() {
		float[] data = new float[Cube.NB_VERTICES * VertexShape.NB_TOTAL_COMPONENTS];
		int i = 0;
		for(VertexShape v : vertices) {
			data[i] = v.getPosition().x; ++i;
			data[i] = v.getPosition().y; ++i;
			data[i] = v.getPosition().z; ++i;
			data[i] = v.getPosition().w; ++i;
			data[i] = v.getPositionRP().x; ++i;
			data[i] = v.getPositionRP().y; ++i;
			data[i] = v.getPositionRP().z; ++i;
			data[i] = v.getPositionRP().w; ++i;
			data[i] = v.getPositionRB().x; ++i;
			data[i] = v.getPositionRB().y; ++i;
			data[i] = v.getPositionRB().z; ++i;
			data[i] = v.getPositionRB().w; ++i;
			data[i] = v.getNormal().x; ++i;
			data[i] = v.getNormal().y; ++i;
			data[i] = v.getNormal().z; ++i; 
			data[i] = v.getTexCoord().x; ++i;
			data[i] = v.getTexCoord().y; ++i;
		}
		return data;
	}
	
	@Override
	public void draw(GL3 gl) {
		gl.glBindVertexArray(vao);
		gl.glDrawArrays(Cube.DRAWING_MODE, 0, Cube.NB_VERTICES);
		gl.glBindVertexArray(0);
	}

}
