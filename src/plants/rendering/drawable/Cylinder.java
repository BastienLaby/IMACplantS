package plants.rendering.drawable;
 
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import plants.exceptions.IllegalWeightSumException;
import plants.matrix.Transform;
import plants.utils.Utils;

public class Cylinder implements Drawable {
 
	private ArrayList<VertexShape> allVertices;
	private int nbTotalVertices;
	private final static int DRAWING_MODE = GL3.GL_TRIANGLES; // le mode de dessin
	private int vao;
	
	public Cylinder(GL3 gl, float height, int discLat, int discHeight, Matrix4f MPcp, Matrix4f MPcb, boolean uniqueChild) {
		
		try {
			this.createData(height, discLat, discHeight, uniqueChild, MPcp, MPcb);
		} catch (IllegalWeightSumException e) {
			e.printStackTrace();
		}
        this.createGLRessources(gl);
	}
 
	private void createData(float height, int discLat, int discHeight, boolean uniqueChild, Matrix4f MPcp, Matrix4f MPcb) throws IllegalWeightSumException {
		
		// Calculate factors for discretisation
		float rcpLat = 1.f / (discLat-1), rcpH = 1.f / (discHeight-1);
        float dPhi = 2 * (float)Math.PI * rcpLat, dH = height * rcpH;
        
        // Create a vertex list which is the list of dictinct vertices
        ArrayList<VertexShape> distinctVertices = new ArrayList<VertexShape>();
        
        // Build all the distinct vertices
        for(int j = 0; j < discHeight; ++j) {
        	for(int i = 0; i < discLat; ++i) {
        		
                VertexShape v = new VertexShape(
                		new Vector4f((float)Math.sin(i*dPhi), j * dH, (float)Math.cos(i * dPhi), 1.0f),
                		new Vector4f(0f, 0f, 0f, 0f),
                		new Vector4f(0f, 0f, 0f, 0f),
                		new Vector3f((float)Math.sin(i * dPhi), 0, (float)Math.cos(i * dPhi)),
                		new Vector2f(i * rcpLat, j * rcpH)
                );             
                distinctVertices.add(v);
            }
        }
        
        // Folding (repliement)
        this.fold(distinctVertices, height, discHeight, discLat, uniqueChild);
        
        // Calculate parent and brother coordinates
        this.computeBrotherAndParent(distinctVertices, MPcp, MPcb);
        
        // Weight Setup
        this.setupWeight(distinctVertices, height, discHeight, discLat, MPcp, MPcb);
        
        // Regroup vertices into triangles
        this.regroupDataIntoTriangles(discHeight, discLat, distinctVertices);
        
        // Inform total vertices
        nbTotalVertices = allVertices.size();
	}
	
	private void computeBrotherAndParent(ArrayList<VertexShape> distinctVertices, Matrix4f MPcp, Matrix4f MPcb) {
		
		for(VertexShape v : distinctVertices) {
        	
            v.setPositionRP(new Vector4f(Transform.multMat4Vec4(MPcp, v.getPosition())));
            v.setPositionRB(new Vector4f(Transform.multMat4Vec4(MPcb, v.getPosition())));

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
		gl.glEnableVertexAttribArray(Utils.TREESHADER_CURRENT_POSITION_LOCATION); // Current position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_PARENT_POSITION_LOCATION); // Father position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_BROTHER_POSITION_LOCATION); // Brother position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_NORMAL_LOCATION); // Normal
		gl.glEnableVertexAttribArray(Utils.TREESHADER_TEXCOORDS_LOCATION); // Texcoord
		
		// Specify vbo data
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_CURRENT_POSITION_LOCATION, // attribute index (here position)
								  VertexShape.NB_COMPONENTS_POSITION, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT, // One vertex total size
								  0L);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_PARENT_POSITION_LOCATION,
								  VertexShape.NB_COMPONENTS_PARENT_POSITION,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_PARENT_POSITION * Utils.SIZEOF_FLOAT);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_BROTHER_POSITION_LOCATION,
								  VertexShape.NB_COMPONENTS_BROTHER_POSITION,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_BROTHER_POSITION * Utils.SIZEOF_FLOAT);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_NORMAL_LOCATION,
								  VertexShape.NB_COMPONENTS_NORMAL,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_NORMAL * Utils.SIZEOF_FLOAT);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_TEXCOORDS_LOCATION,
								  VertexShape.NB_COMPONENTS_TEXCOORDS,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_TEXCOORDS * Utils.SIZEOF_FLOAT);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		// Unbind VAO
		gl.glBindVertexArray(0);
	}
	
	private float[] getData() {
		
		float[] data = new float[this.nbTotalVertices * VertexShape.NB_TOTAL_COMPONENTS];
		int i = 0;
		for(VertexShape v : allVertices) {
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
	
	private void fold(ArrayList<VertexShape> distinctVertices, float height, int discHeight, int discLat, boolean uniqueChild) {
		
		float dy = height/discHeight;
        
        for(int i = 0; i < discHeight/4; ++i) {
        	
        	float weight = (1-(float)4*i/discHeight);
        	float angle = (float)Math.PI/4;
        	
        	float ponderateAngle = weight*angle;
        	if(uniqueChild) {
    			ponderateAngle = - ponderateAngle;
    		}
        	
    		for(int j = 0; j < discLat/2; ++j) {
        		float tmpx = distinctVertices.get(j + discLat*i).getPosition().x;
        		float tmpy = distinctVertices.get(j + discLat*i).getPosition().y;
        		
        		tmpy = tmpy - i*dy;
        		
        		float newx = (tmpx * (float)Math.cos(ponderateAngle) - tmpy * (float)Math.sin(ponderateAngle));
        		float newy = (tmpx * (float)Math.sin(ponderateAngle) + tmpy * (float)Math.cos(ponderateAngle));
        		float newz = distinctVertices.get(j  + discLat*i).getPosition().z;
        		newy = newy + i*dy;
        		float normalx = distinctVertices.get(j + discLat*i).getNormal().x;
        		float normaly = distinctVertices.get(j + discLat*i).getNormal().y;
        		float normalz = distinctVertices.get(j + discLat*i).getNormal().z;
        		float tx = distinctVertices.get(j + discLat*i).getTexCoord().x;
        		float ty = distinctVertices.get(j + discLat*i).getTexCoord().y;
        		
        		distinctVertices.set(j  + discLat*i, new VertexShape(newx, newy, newz, normalx, normaly, normalz, tx, ty));
    		}
    		
    		ponderateAngle =  (float) (weight*(angle - (float)Math.PI/2));
    		
    		for(int j = discLat/2; j < discLat; ++j) {

    			float tmpx = distinctVertices.get(j + discLat*i).getPosition().x;
        		float tmpy = distinctVertices.get(j + discLat*i).getPosition().y;
        		
        		tmpy = tmpy - i*dy;
        		
        		float newx = (tmpx * (float)Math.cos(ponderateAngle) - tmpy * (float)Math.sin(ponderateAngle));
        		float newy = (tmpx * (float)Math.sin(ponderateAngle) + tmpy * (float)Math.cos(ponderateAngle));
        		float newz = distinctVertices.get(j  + discLat*i).getPosition().z;
        		newy = newy + i*dy;
        		float normalx = distinctVertices.get(j + discLat*i).getNormal().x;
        		float normaly = distinctVertices.get(j + discLat*i).getNormal().y;
        		float normalz = distinctVertices.get(j + discLat*i).getNormal().z;
        		float tx = distinctVertices.get(j + discLat*i).getTexCoord().x;
        		float ty = distinctVertices.get(j + discLat*i).getTexCoord().y;
        		
        		distinctVertices.set(j  + discLat*i, new VertexShape(newx, newy, newz, normalx, normaly, normalz, tx, ty));        		
        		
        	}
        }
	}
	
	private void setupWeight(ArrayList<VertexShape> distinctVertices, float height, int discHeight, int discLat, Matrix4f MPcp, Matrix4f MPcb) throws IllegalWeightSumException {
		
        float H = (float)discHeight/2;
        
        float wc;  // local weight
    	float wp; // Parent weight
    	float wb;// Brother weight
    	
    	for(int i = 0; i < discHeight; ++i) {
    		
    		// First semi-circle
        	for(int j = 0; j < discLat/2; ++j) {
        		
        		if(i < H) {
        			// Quadratique Ponderation
            		wc = (float)(Math.sqrt(i)/Math.sqrt(H));
        			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
        			wb = 0.0f;
        		} else {
        			wp = 0.0f;
        			wb = 0.0f;
        			wc = 1.0f;
        		}
        		distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
        	}
        	
        	// Second semi-circle
        	for(int j = discLat/2; j < discLat; ++j) {
        		
        		if(i < H) {
        			// Quadratique Ponderation
        			wc = (float)(Math.sqrt(i)/Math.sqrt(H));
        			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
        			wb = 0.0f; // sens�e �tre pond�r�e
        		} else {
        			wp = 0.0f;
        			wb = 0.0f;
        			wc = 1.0f;
        		}
        		distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
        	}
    	}
	}
	
	private void regroupDataIntoTriangles(int discHeight, int discLat, ArrayList<VertexShape> distinctVertices) {
		this.allVertices = new ArrayList<VertexShape>();
        for(int j = 0; j < discHeight-1; ++j) {
        	int offset = j * discLat;
        	for(int i = 0; i < discLat; ++i) {
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + i)));
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + (i + 1)%discLat)));
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + discLat + (i + 1)%discLat)));
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + i)));
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + discLat + (i + 1)%discLat)));
        		this.allVertices.add(new VertexShape(distinctVertices.get(offset + i + discLat)));
            }
        }
	}
	
	public void draw(GL3 gl) {
		gl.glBindVertexArray(this.vao);
        gl.glDrawArrays(Cylinder.DRAWING_MODE, 0, nbTotalVertices);
        gl.glBindVertexArray(0);
	}


}