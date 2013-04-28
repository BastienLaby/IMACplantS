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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.createGLRessources(gl);
	}
 
	private void createData(float height, int discLat, int discHeight, boolean uniqueChild, Matrix4f MPcp, Matrix4f MPcb) throws IllegalWeightSumException {
		
		// Calculate factors for discretisation
		float rcpLat = 1.f / discLat, rcpH = 1.f / discHeight;
        float dPhi = 2 * (float)Math.PI * rcpLat, dH = height * rcpH;
        
        // Create a vertex list which is the list of dictinct vertices
        ArrayList<VertexShape> distinctVertices = new ArrayList<VertexShape>();
        
        // Build all the distinct vertices
        for(int j = 0; j <= discHeight; ++j) {
        	for(int i = 0; i < discLat; ++i) {
                VertexShape v = new VertexShape(
                		new Vector4f((float)Math.sin(i*dPhi)*0.5f, j * dH, (float)Math.cos(i * dPhi)*0.5f, 1.0f),
                		new Vector4f(),
                		new Vector4f(),
                		new Vector3f((float)Math.sin(i * dPhi), 0, (float)Math.cos(i * dPhi)),
                		new Vector2f(i * rcpLat, j * rcpH)
                );             
                distinctVertices.add(v);
            }
        }
        
        // Folding (repliement)
        this.fold(distinctVertices, height, discHeight, discLat, uniqueChild);
        
        // Weight Setup
        this.setupWeight(distinctVertices, height, discHeight, discLat, MPcp, MPcb);

        // Regroup vertices into triangles
        this.regroupDataIntoTriangles(discHeight, discLat, distinctVertices);
        
        // Inform total vertices
        nbTotalVertices = allVertices.size();
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
		gl.glEnableVertexAttribArray(Utils.TREESHADER_POSITIONC_LOCATION); // Current position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_POSITIONP_LOCATION); // Father position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_POSITIONB_LOCATION); // Brother position
		gl.glEnableVertexAttribArray(Utils.TREESHADER_NORMAL_LOCATION); // Normal
		gl.glEnableVertexAttribArray(Utils.TREESHADER_TEXCOORDS_LOCATION); // Texcoord
		
		// Specify vbo data
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_POSITIONC_LOCATION, // attribute index (here position)
								  VertexShape.NB_COMPONENTS_POSITION, // number of component
								  GL3.GL_FLOAT, // datatype
								  false, // normalize ?
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT, // One vertex total size
								  0L);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_POSITIONP_LOCATION,
								  VertexShape.NB_COMPONENTS_POSITIONRP,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_POSITIONP * Utils.SIZEOF_FLOAT);
		
		gl.glVertexAttribPointer( Utils.TREESHADER_POSITIONB_LOCATION,
								  VertexShape.NB_COMPONENTS_POSITIONRB,
								  GL3.GL_FLOAT,
								  false,
								  VertexShape.NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT,
								  VertexShape.OFFSET_POSITIONB * Utils.SIZEOF_FLOAT);
		
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
        
		// For the first quarter points
        for(int i = 0; i < discHeight/4; ++i) {  
        	
        	// Calculate weight and ponderate angle
        	float weight = (1-(float)4*i/discHeight);
        	float angle = (float)Math.PI/4;        	
        	float ponderateAngle = weight*angle;
        	if(uniqueChild) {
    			ponderateAngle = - ponderateAngle;
    		}
        	
        	// For the first semi-circle points (ponderated by parent only)
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
    		
    		// Inverse the ponderate angle
    		ponderateAngle =  (float) (weight*(angle - (float)Math.PI/2));
    		
    		// For the second semi-circle points (ponderated by parent and brother)
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
		
		float start = -discHeight/6;
        float a = start;
        float H = (float)discHeight/2; // The height limit onto the cylinder to ponderate
        float pas = (-2*start)/H;
        
        float wc; // local weight
    	float wp; // Parent weight
    	float wb; // Brother weight
        
    	float B; // Lateral ponderation
    	float X; // Height variation
    	
    	
    	for(int i = 0; i < discHeight; ++i) {
    		
            	X = (float)Math.sqrt(i);
            	
            	/****** First semi-circle ******/
            	for(int j = 0; j < discLat/2; ++j) {
            		
            		B = ((float)16.0f/((discLat)*(discLat)))*(j - ((float)(discLat)/4))*(j - ((float)(discLat)/4));
            		//initialWp = (16/((discLat)*(discLat)))*(j - ((3*(discLat))/4)*(j - ((3*(discLat))/4)));
            		//initialWc = initialWb = (1 - initialWp)/2;
            		
            		if(i < H) {
            			
            			// Polynomial ponderation
            			/*if(!uniqueChild){
    	        			//wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			//wb = wp = (1 - wc)/2;
            				// Parent
            				wp = B -(B/((float)Math.sqrt(H)))*X;
            				// Curent
            				wc = ((1-B)/2) + ((B+1)/(2*(float)Math.sqrt(H)))*X;
            				// Brother
            				wb = ((1-B)/2) + ((B-1)/(2*(float)Math.sqrt(H)))*X;
            			} else {
            				wc = (float)((float)Math.sqrt(i))/((float)Math.sqrt(H));
                			wp = (1.0f - (float)((float)Math.sqrt(i))/((float)Math.sqrt(H)));
                			wb = 0.0f;
            			}*/
            			
            			// Triple reverse ultime ponderation
            			/*if(!uniqueChild){
    	        			wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			wb = wp = (1 - wc)/2;
    	        			
            			} else {
            				wc = (float)(4*i*i)/(discHeight*discHeight);
                			wp = (1.0f - (float)(4*i*i)/(discHeight*discHeight)); // ->  linear
                			wb = 0.0f;
            			}*/
            			
                		// Quadratic Ponderation
                		wc = (float)(Math.sqrt(i)/Math.sqrt(H));
            			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
            			wb = 0.0f;
                		
            			// Arctan ponderation
            			//wc = (float) (1.176f*((Math.atan(a/4)/Math.PI) + 0.49f));
            			//wp = 1 - wc;
            			//wp = (float) (0.5f - 1.176f*((Math.atan(a/4)/Math.PI) + 0.24f));
            			//wb = 0.0f;
            			
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            			
            		} else {
            			
            			wp = 0.0f;
            			wb = 0.0f;
            			wc = 1.0f;
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            			
            		}
            	}
            	
            	/****** Second semi-circle ******/
            	for(int j = discLat/2; j < discLat; ++j) {
            		
            		//initialWp = (16/((discLat)*(discLat)))*(j - ((3*(discLat))/4)*(j - ((3*(discLat))/4)));
            		//initialWc = initialWb = (1 - initialWp)/2;
            		
            		B = ((float)16.0f/((discLat)*(discLat)))*(j - ((float)(3*(discLat))/4))*(j - ((float)(3*(discLat))/4));
            		
            		if(i < H) {
            		
            			// Triple reverse ultime ponderation
            			/*if(!uniqueChild){
    	        			//wc = (1-(float)(4*i*i)/(discHeight*discHeight))*(initialWc) + (float)(4*i*i)/(float)(discHeight*discHeight);
    	        			//wb = wp = (1 - wc)/2;
    	        			
            				// Parent
            				wp = B -(B/((float)Math.sqrt(H)))*X;
            				// Current
            				wc = ((1-B)/2) + ((B+1)/(2*(float)Math.sqrt(H)))*X;
            				// Brother
            				wb = ((1-B)/2) + ((B-1)/(2*(float)Math.sqrt(H)))*X;
            				
            			} else {
            				wc = (float)((float)Math.sqrt(i))/((float)Math.sqrt(H));
                			wp = (1.0f - (float)((float)Math.sqrt(i))/((float)Math.sqrt(H)));
                			wb = 0.0f;
            			}*/
            			
            			// Quadratique Ponderation
                		wc = (float)(Math.sqrt(i)/Math.sqrt(H));
            			wp = 1.0f - (float)(Math.sqrt(i)/Math.sqrt(H));
            			wb = 0.0f;
            			
            			//Arctan Ponderation
            			//wc = (float) (1.176f*((Math.atan(a/4)/Math.PI) + 0.49f));
            			//wp = 1 - wc;
            			//wp = (float) (0.5f - 1.176f*((Math.atan(a/4)/Math.PI) + 0.24f));
            			//wb = 0.0f;
            			
            			//Linear Ponderation
            			//wp = (0.5f - (float)i/discHeight);//--> linear
            			//wc = (float)2*i/discHeight;
            			//wb = (0.5f - (float)i/discHeight);
            			
            			//Double quadratique ponderation
            			//wc = (i*i)/(float)Math.pow(discHeight/2, 2);
            			//wp = wb = (1 - wc)/2;
            			
            			//Polynomial ponderation
            			//wp = B + w*X -(2*t + (1/H*H) + (w/H))*X*X;
            			//wc =((1-B)/2) - (H*t + ((1-B)/2) + w)*X + ((1/H*H) + t + (w/H))*X*X;
            			//wb = ((1-B)/2) - (H*t + ((1-B)/2*H))*X + t*X*X;
            			//wb = ((1-B)/2) - (H*t + ((1-B)/2))*X + t*X*X;
            			
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            			
            		} else {
            			
            			wp = 0.0f;
            			wb = 0.0f;
            			wc = 1.0f;
            			distinctVertices.get(j  + discLat*i).fixWeight(wc, wp, wb);
            			
            		}
            	}
            	
            	a = a + pas;
            	
            }
        
        for(VertexShape v : distinctVertices) {
      
            Vector4f coordsRP = Transform.multMat4Vec4(MPcp, new Vector4f(v.getPosition().x, v.getPosition().y, v.getPosition().z, 1));
            v.getPositionRP().x = coordsRP.x/coordsRP.w;
            v.getPositionRP().y = coordsRP.y/coordsRP.w;
            v.getPositionRP().z = coordsRP.z/coordsRP.w;
            
            Vector4f coordsRB = Transform.multMat4Vec4(MPcb, new Vector4f(v.getPosition().x, v.getPosition().y, v.getPosition().z, 1));
            v.getPositionRB().x = coordsRB.x/coordsRB.w;
            v.getPositionRB().y = coordsRB.y/coordsRB.w;
            v.getPositionRB().z = coordsRB.z/coordsRB.w;
            
        }
		
	}
	
	private void regroupDataIntoTriangles(int discHeight, int discLat, ArrayList<VertexShape> distinctVertices) {
		this.allVertices = new ArrayList<VertexShape>();
        for(int j = 0; j < discHeight; ++j) {
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