package plants.dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import plants.matrix.Transform;
import plants.rendering.drawable.Cylinder;

public class TrunckTreeNode extends PlantsTreeNode {
	
	private boolean renderable = true;
	
	private float length, alpha/*, radius*/;
	private Vector3f axe;

	private Matrix4f MPcp, MPcb;
	private Matrix4f SC = new Matrix4f();
	private Matrix4f SP = new Matrix4f();
	private Matrix4f SB = new Matrix4f();
	private Cylinder cylinder;
	
	private boolean uniqueChild;
	
	public TrunckTreeNode(float length, Vector3f axe, float rad, float radP, float radB, Matrix4f MPcp, Matrix4f MPcb, boolean uniqueChild) {
		
		this.MV = new Matrix4f();
		this.MV.setIdentity();
		
		this.length = length;
		this.axe = axe;
		//this.radius = rad;
		this.uniqueChild = uniqueChild;
		
		this.SC = new Matrix4f(
			rad,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		rad,	0,
			0,		0,		0,		1
		);
		//System.out.println("SC : " + SC);
		
		this.SP = new Matrix4f(
			radP,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		radP,	0,
			0,		0,		0,		1
		);
		//System.out.println("SP : " + SP);
		
		this.SB = new Matrix4f(
			radB,	0,		0,		0,
			0,		1,		0,		0,
			0,		0,		radB,	0,
			0,		0,		0,		1
		);
		//System.out.println("SB : " + SB);
		
		final float normeAxe = axe.length();
		this.alpha = (float) Math.acos(axe.x/normeAxe);
		if(axe.y < 0) {
			this.alpha = -this.alpha;
		}
		
		this.MPcp = new Matrix4f(MPcp);
		this.MPcb = new Matrix4f(MPcb);
		
	}
	
	public void createDrawableObjects(GL3 gl) {
		this.cylinder = new Cylinder(gl, length, 8, 8, MPcp, MPcb, this.uniqueChild);
	}
	
	public TrunckTreeNode() {
		this.length = 0;
		this.axe = new Vector3f(0, 1, 0);
		this.alpha = 1.0f;
		this.renderable = false;
		Matrix4f identity = new Matrix4f();
		identity.setIdentity();
	}

	public Matrix4f getRotationMatrix() {
		
		Vector3f v = new Vector3f(this.axe);
		v.normalize();
		
		Vector3f X = new Vector3f(1.0f, 0.0f, 0.0f);
		Vector3f w = new Vector3f();
		
		if(Math.abs(X.dot(v)) > 0.8) {
			X.x = 0.0f;
			X.y = 1.0f;
			X.z = 0.0f;
			w.cross(v, X);
			w.normalize();
		} else {
			w.cross(X, v);
			w.normalize();
		}
		
		Vector3f u = new Vector3f();
		u.cross(v, w);
		u.normalize();
		
		Matrix4f Mrotate = new Matrix4f(
				u.x, v.x, w.x, 0.0f,
				u.y, v.y, w.y, 0.0f,
				u.z, v.z, w.z, 0.0f,
				0.0f, 0.0f, 0.0f, 1f
		);
		
		Mrotate.transpose();
		
		return Mrotate;
	}
	
	public void setMV(Matrix4f mv) {
		this.MV = mv;
	}
	
	public Matrix4f getMV() {
		return this.MV;
	}

	public String toString() {
		return ("TRUNCK : L="+length);
	}
	
	public void render(GL3 gl, int MVcLocation, int MVpLocation, Matrix4f MVp, int MVbLocation, Matrix4f MVb, int SCLocation, int SPLocation, int SBLocation) {
		if(this.renderable) {
	        gl.glUniformMatrix4fv(MVcLocation, 1, false, Transform.toFloatArray(this.MV), 0);
	        gl.glUniformMatrix4fv(MVpLocation, 1, false, Transform.toFloatArray(MVp), 0);
	        gl.glUniformMatrix4fv(MVbLocation, 1, false, Transform.toFloatArray(MVb), 0);
	        gl.glUniformMatrix4fv(SCLocation, 1, false, Transform.toFloatArray(this.SC), 0);
	        gl.glUniformMatrix4fv(SPLocation, 1, false, Transform.toFloatArray(this.SP), 0);
	        gl.glUniformMatrix4fv(SBLocation, 1, false, Transform.toFloatArray(this.SB), 0);
	        this.cylinder.draw(gl);
		}
	}

	public float getLength() {
		return this.length;
	}

	public String getType() {
		return "trunck";
	}
}