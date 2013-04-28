package plants.matrix;

import java.util.Stack;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class MatrixStack {

	private Stack<Matrix4f> stack;
	
	/* Initialize the stack with Identity */
	public MatrixStack() {
		this.stack = new Stack<Matrix4f>();
		Matrix4f id = new Matrix4f();
		id.setIdentity();
		this.stack.push(id);
	}
	
	/* Push a matrix on the top of the stack which is the same matrix as the current top */
	public void push() {
		Matrix4f m = new Matrix4f(top());
		this.stack.push(m);
	}
	
	/* Remove the top of the stack */
	public void pop() {
		this.stack.pop();
	}
	
	/* Return the top of the stack */
	public Matrix4f top() {
		return this.stack.peek();
	}
	
	/* Modify the current top matrix, replacing it with the input matrix */
	public void set(Matrix4f mat) {
		this.stack.pop();
		this.stack.push(mat);
	}
	
	/* Do a right-multiplication between the current top matrix and the input matrix */
	public void mult(Matrix4f mat) {
		top().mul(mat);
	}
	
	/* Do a right-multiplication between the current top matrix and a scale matrix which corresponds to the input scale vector */
	public void scale(Vector3f s) {
		Matrix4f scale = new Matrix4f(s.x, 0, 0, 0, 0, s.y, 0, 0, 0, 0, s.z, 0, 0, 0, 0, 1);
		mult(scale);
	}
	
	/* Do a right-multiplication between the current top matrix and a rotation matrix which corresponds to the input rotation parameters */
	public void rotate(Vector3f dir, float angle) {
		Matrix4f rotate = Transform.Rotation(dir, angle);
		mult(rotate);
	}
	
	/* Do a right-multiplication between the current top matrix and the input rotation matrix  */
	public void rotate(Matrix4f rotate) {
		mult(rotate);
	}
	
	/* Do a right-multiplication between the current top matrix and a translation matrix which corresponds to the input translation vector */
	public void translate(Vector3f t) {
		Matrix4f translate = Transform.Translation(t);
		mult(translate);
	}
	
}
