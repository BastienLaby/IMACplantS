package plants.rendering.drawable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import plants.utils.Utils;

public class Square implements Drawable {

	private VertexShape[] vertices;
	private final static int NB_VERTICES = 4;
	private final static int DRAWING_MODE = GL3.GL_TRIANGLE_FAN;
	private int vao;
	
	public Square(GL3 gl) {
		this.vertices = new VertexShape[NB_VERTICES];
		this.createData();
		this.createGLRessources(gl);
	}
	
	public void draw(GL3 gl) {
		gl.glBindVertexArray(this.vao);
		gl.glDrawArrays(Square.DRAWING_MODE, 0, Square.NB_VERTICES);
		gl.glBindVertexArray(0);
	}
	
	private void createData() {
		this.vertices[0] = new VertexShape(-0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		this.vertices[1] = new VertexShape(0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f);
		this.vertices[2] = new VertexShape(0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
		this.vertices[3] = new VertexShape(-0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
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
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, data.length*Float.SIZE, bufferdata, GL3.GL_STATIC_DRAW);
		
		// Unbind VBO
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Generate VAO
		IntBuffer bufVAO = IntBuffer.allocate(1);
		gl.glGenVertexArrays(1, bufVAO);
		this.vao = bufVAO.get(0);		
		
		// Bind VAO
		gl.glBindVertexArray(this.vao);
		
		// Active vertex attributes
		gl.glEnableVertexAttribArray(Utils.LEAFSHADER_POSITION_LOCATION); // position
		gl.glEnableVertexAttribArray(Utils.LEAFSHADER_NORMAL_LOCATION); // normal
		
		// Specify vbo data
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		gl.glVertexAttribPointer( Utils.LEAFSHADER_POSITION_LOCATION, // attribute index
								  VertexShape.NB_COMPONENTS_POSITION, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.SIZEOF_VERTEX, // One vertex total size
								  VertexShape.OFFSET_POSITION * Utils.SIZEOF_FLOAT); // offset(byte) to access first attribute element
		
		gl.glVertexAttribPointer( Utils.LEAFSHADER_NORMAL_LOCATION,
								  VertexShape.NB_COMPONENTS_NORMAL,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.SIZEOF_VERTEX,
								  VertexShape.OFFSET_NORMAL * Utils.SIZEOF_FLOAT);
		gl.glVertexAttribPointer( Utils.LEAFSHADER_TEXCOORDS_LOCATION,
								  VertexShape.NB_COMPONENTS_TEXCOORDS,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.SIZEOF_VERTEX,
								  VertexShape.OFFSET_TEXCOORDS * Utils.SIZEOF_FLOAT);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Unbind VAO
		gl.glBindVertexArray(0);
	}
	
	private float[] getData() {
		float[] data = new float[Square.NB_VERTICES * VertexShape.NB_TOTAL_COMPONENTS];
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
		
}
