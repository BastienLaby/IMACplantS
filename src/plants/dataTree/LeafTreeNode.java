package plants.dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;

import plants.matrix.Transform;
import plants.rendering.drawable.Square;

public class LeafTreeNode extends PlantsTreeNode {

	private float 	rho,
					theta,
					phi,
					scale,
					height,
					radiusTrunck,
					lengthTrunck,
					orientation;
	
	private Square square;
	
	public LeafTreeNode(float rho, float theta, float phi, float scale, float height, float radiusTrunck, float lengthTrunck, float orientation) {
		this.rho = rho;
		this.theta = theta;
		this.phi = phi;
		this.scale = scale;
		this.height = height;
		this.radiusTrunck = radiusTrunck;
		this.lengthTrunck = lengthTrunck;
		this.orientation = orientation;
	}
	
	public void createDrawableObjects(GL3 gl) {
		this.square = new Square(gl);
	}
	
	public void render(GL3 gl, int MVcLocation) {
		gl.glUniformMatrix4fv(MVcLocation, 1, false, Transform.toFloatArray(this.MV), 0);
		this.square.draw(gl);
	}
	
	public String toString() {
		return ("LEAF");
	}
	
	public Matrix4f getRotationMatrix() {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f XRotate = new Matrix4f(
			1.0f, 0.0f, 					0.0f, 					0.0f,
			0.0f, (float)Math.cos(rho), 	(float)-Math.sin(rho), 	0.0f,
			0.0f, (float)Math.sin(rho), 	(float)Math.cos(rho), 	0.0f,
			0.0f, 0.0f, 					0.0f, 					1.0f
		);
		XRotate.transpose();
		
		Matrix4f YRotate = new Matrix4f(
			(float)Math.cos(theta),		0.0f,	(float)Math.sin(theta),		0.0f,
			0.0f, 						1.0f,	0.0f,						0.0f,
			-(float)Math.sin(theta),	0.0f,	(float)Math.cos(theta),		0.0f,
			0.0f,						0.0f,	0.0f,						1.0f	
		);
		YRotate.transpose();
		
		Matrix4f ZRotate = new Matrix4f(
			(float)Math.cos(phi),		-(float)Math.sin(phi),		0.0f,		0.0f,
			(float)Math.sin(phi), 	(float)Math.cos(phi),		0.0f,		0.0f,
			0.0f,						0.0f,						1.0f,		0.0f,
			0.0f,						0.0f,						0.0f,		1.0f	
		);
		ZRotate.transpose();
		
		matrix.mul(XRotate);
		matrix.mul(YRotate);
		matrix.mul(ZRotate);
			
		return matrix;
	}

	public String getType() {
		return "leaf";
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getRadiusTrunck() {
		return radiusTrunck;
	}

	public void setRadiusTrunck(float radiusTrunck) {
		this.radiusTrunck = radiusTrunck;
	}

	public float getLengthTrunck() {
		return lengthTrunck;
	}

	public void setLengthTrunck(float lengthTrunck) {
		this.lengthTrunck = lengthTrunck;
	}

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	
	
}
