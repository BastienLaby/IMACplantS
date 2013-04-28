package plants.camera;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class FPSCamera {

	private Vector3f position;
	private float phi;
	private float theta;
	private Vector3f frontVector;
	private Vector3f leftVector;
	private Vector3f upVector;
	
	/* Create a FPS Camera */
	public FPSCamera() {
		
		position = new Vector3f(0.0f, 1.0f, -5.0f);
		phi = 0.0f;
		theta = 0.0f;
		
		frontVector = new Vector3f();
		upVector = new Vector3f(0.0f, 1.0f, 0.0f);
		leftVector = new Vector3f();
		
		computeDirectionVectors();
	}
	
	/* Return the position of the center of the camera */
	public Vector3f position() {
		return position;
	}
	
	/* Compute direction vectors to actualize the camera view */
	private void computeDirectionVectors() {
		
		float cosTheta = (float)Math.cos(theta);
		
		frontVector.x = cosTheta*(float)Math.sin(phi);
		frontVector.y = (float)Math.sin(theta);
		frontVector.z = cosTheta*(float)Math.cos(phi);
	
		leftVector.x = (float)Math.sin(phi + Math.PI/2);
		leftVector.y = 0.0f;
		leftVector.z = (float)Math.cos(phi + Math.PI/2);
		
		upVector.cross(frontVector, leftVector);
		
	}
	
	/* Move the position of the camera, following the left vector */
	public void moveLeft(float t) {
		
		position.x += t*leftVector.x;
		position.y += t*leftVector.y;
		position.z += t*leftVector.z;
	}
	
	/* Move the position of the camera, following the front vector */
	public void moveFront(float t) {
		position.x += t*frontVector.x;
		position.y += t*frontVector.y;
		position.z += t*frontVector.z;
	}
	
	/* Rotate the camera by rotating his parameter phi */
	public void rotateLeft(float degrees) {
		phi += degrees/360 * 2 * Math.PI;
		computeDirectionVectors();
	}
	
	/* Rotate the camera by rotating his parameter theta */
	public void rotateUp(float degrees) {
		theta += degrees/360 * 2 * Math.PI;
		computeDirectionVectors();
	}
	
	/* Return the view matrix corresponding to the camera view, using a LookAt matrix */
	public Matrix4f getViewMatrix() {
		Vector3f view = new Vector3f();
		view.add(position, frontVector);
		return plants.matrix.Transform.LookAt(position, view, upVector);
	}
	
	public void resetRotate() {
		this.phi = 0;
		this.theta = 0;
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
}
