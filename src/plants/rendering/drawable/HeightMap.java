package plants.rendering.drawable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL3;

import plants.utils.Utils;

public class HeightMap implements Drawable {

	private final int[][] heightMap;
	private final int width;
	private final int height;
	
	private ArrayList<VertexShape> vertices;
	private final int nbVertices;
	private static final int DRAWING_MODE = GL3.GL_TRIANGLES;
	private int vao;
	
	public HeightMap(GL3 gl, String filename) throws IOException  {

		// Create buffered iamge from image file
		BufferedImage img = null;
		img = ImageIO.read(new File(filename));
		
		// Initialize width and height and allocate HM int array
		this.width = img.getWidth()/10;
		this.height = img.getHeight()/10;
		this.heightMap = new int[width][height];
		
		System.out.println("HM : w = " + this.width + ", h = " + this.height);
		
		this.fill(img);
		this.createData();
		this.nbVertices = this.vertices.size();
		this.createGLRessources(gl);
		
	}
	
	private void fill(BufferedImage img) {
		
		int minimumHeight = 255;
		
		// Fill each element of the heightmap with the corresponding image pixel
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int rgb = img.getRGB(i, j);
				this.heightMap[i][j] =  (rgb & 0xFF);
				if(this.heightMap[i][j] < minimumHeight) {
					minimumHeight = this.heightMap[i][j];
				}
			}
		}
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				this.heightMap[i][j] -= minimumHeight;
			}
		}
	}
	
	private void createData() {
		
		// Fill all vertices
		float mapWidth = 1, mapHeight = 1; // map between -0.5 & 0.5
		ArrayList<VertexShape> distinctVertices = new ArrayList<VertexShape>();
		for(int i = 0; i < this.width; i++) {
			for(int j = 0; j < this.height; j++) {
				//System.out.println((float)j/(float)this.width + ", " + (float)i/(float)this.height);
				distinctVertices.add(new VertexShape(-mapWidth/2 + ((float)j/(float)this.width) * mapWidth, // X coordinate
													this.heightMap[i][j]*0.3f, // Y coordinate (corresponding to the heightMap)
													-mapHeight/2 + ((float)i/(float)this.height) * mapHeight, // Z coordinate
													 0f,
													 1f,
													 0f,
													 i%2, // Texcoord X
													 j%2)); // Texcoord Y
			}
		}
		
		// Regroup vertices into triangles
		// Each face is like : ( [i][j], [i+1][j], [i][j+1])  ([i][j+1], [i+1][j], [i+1][j+1]
		this.vertices = new ArrayList<VertexShape>();
		for(int i = 0; i < this.width - 1; i++) {
			for(int j = 0; j < this.height - 1; j++) {
				
				// First face
				this.vertices.add(distinctVertices.get(this.width*i + j));
				this.vertices.add(distinctVertices.get(this.width*(i+1) + j));
				this.vertices.add(distinctVertices.get(this.width*i + j+1));
				
				// Second face
				this.vertices.add(distinctVertices.get(this.width*i + j+1));
				this.vertices.add(distinctVertices.get(this.width*(i+1) + j));
				this.vertices.add(distinctVertices.get(this.width*(i+1) + j + 1));
				
			}
		}
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
		gl.glEnableVertexAttribArray(Utils.GROUNDSHADER_POSITION_LOCATION); // position
		gl.glEnableVertexAttribArray(Utils.GROUNDSHADER_NORMAL_LOCATION); // normal
		gl.glEnableVertexAttribArray(Utils.GROUNDSHADER_TEXCOORDS_LOCATION); // texcoord
		
		// Specify vbo data
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		gl.glVertexAttribPointer( Utils.GROUNDSHADER_POSITION_LOCATION, // attribute index (here position)
								  VertexShape.NB_COMPONENTS_POSITION, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT, // One vertex total size
								  0L);
		
		gl.glVertexAttribPointer( Utils.GROUNDSHADER_NORMAL_LOCATION, // attribute index (here position)
								  VertexShape.NB_COMPONENTS_NORMAL, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT, // One vertex total size
								  VertexShape.OFFSET_NORMAL * Utils.SIZEOF_FLOAT);
		
		gl.glVertexAttribPointer( Utils.GROUNDSHADER_TEXCOORDS_LOCATION, // attribute index (here position)
								  VertexShape.NB_COMPONENTS_TEXCOORDS, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT, // One vertex total size
								  VertexShape.OFFSET_TEXCOORDS * Utils.SIZEOF_FLOAT);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Unbind VAO
		gl.glBindVertexArray(0);
	}
	
	private float[] getData() {
		float[] data = new float[this.nbVertices * VertexShape.NB_TOTAL_COMPONENTS];
		int i = 0;
		System.out.println(this.vertices.size());
		for(VertexShape v : this.vertices) {
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
	
	public void draw(GL3 gl) {
		
		gl.glBindVertexArray(this.vao);
        gl.glDrawArrays(HeightMap.DRAWING_MODE, 0, this.nbVertices);
        gl.glBindVertexArray(0);
        
	}
	
}
