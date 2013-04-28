package plants.dataTree;

import javax.media.opengl.GL3;
import javax.vecmath.Matrix4f;

public abstract class PlantsTreeNode {
	
	protected Matrix4f MV;
	
	public PlantsTreeNode() {
		
	}

	//public abstract void render(GL3 gl, int MVcLocation, int MVpLocation, Matrix4f MVp, int MVbLocation, Matrix4f MVb, int SCLocation, int SPLocation, int SBLocation);
	public abstract Matrix4f getRotationMatrix();
	public abstract String getType();
	public abstract void createDrawableObjects(GL3 gl);
	
	public String toString() {
		return ("ABSTRACT TREE ELEMENT");
	}
	
	public void setMV(Matrix4f mv) {
		this.MV = mv;
	}
	
	public Matrix4f getMV() {
		return this.MV;
	}
	
}
