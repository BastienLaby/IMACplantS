package plants.rendering.drawable;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import plants.exceptions.IllegalWeightSumException;
import plants.utils.Utils;


public class VertexShape {

	public final static int NB_COMPONENTS_POSITION = 4;
	public final static int NB_COMPONENTS_POSITIONRP = 4;
	public final static int NB_COMPONENTS_POSITIONRB = 4;
	public final static int NB_COMPONENTS_NORMAL = 3;
	public final static int NB_COMPONENTS_TEXCOORDS = 2;
	public final static int NB_TOTAL_COMPONENTS = NB_COMPONENTS_POSITION
												  + NB_COMPONENTS_POSITIONRP
												  + NB_COMPONENTS_POSITIONRB
												  + NB_COMPONENTS_NORMAL
												  + NB_COMPONENTS_TEXCOORDS;
	public final static int OFFSET_POSITION = 0;
	public final static int OFFSET_POSITIONP = NB_COMPONENTS_POSITION;
	public final static int OFFSET_POSITIONB = OFFSET_POSITIONP + NB_COMPONENTS_POSITIONRP;
	public final static int OFFSET_NORMAL = OFFSET_POSITIONB + NB_COMPONENTS_POSITIONRB;
	public final static int OFFSET_TEXCOORDS = OFFSET_NORMAL + NB_COMPONENTS_NORMAL;
	public final static int SIZEOF_VERTEX = NB_TOTAL_COMPONENTS * Utils.SIZEOF_FLOAT;

	private Vector4f position;
	private Vector4f positionRP;
	private Vector4f positionRB;
	private Vector3f normal;
	private Vector2f texCoord;

	public VertexShape(Vector4f position, Vector4f positionRP, Vector4f positionRB, Vector3f normal, Vector2f texCoord) {
		this.position = position;
		this.positionRP = positionRP;
		this.positionRB = positionRB;
		this.normal = normal;
		this.texCoord = texCoord;		
	}

	public VertexShape() {
		this(	new Vector4f(0f, 0f, 0f, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector3f(0f, 0f, 0f),
				new Vector2f(0f, 0f));
	}

	public VertexShape(VertexShape v) {
		this(v.position, v.positionRP, v.positionRB, v.normal, v.texCoord);
	}

	public VertexShape(float x, float y, float z) {
		this(	new Vector4f(x, y, z, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector3f(0f, 0f, 0f),
				new Vector2f(0f, 0f));
	}

	public VertexShape(float x, float y, float z, float n1, float n2, float n3) {
		this(	new Vector4f(x, y, z, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector3f(n1, n2, n3),
				new Vector2f(0f, 0f));
	}

	public VertexShape(float x, float y, float z, float n1, float n2, float n3, float tc1, float tc2) {
		this(	new Vector4f(x, y, z, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector4f(0f, 0f, 0f, 1f),
				new Vector3f(n1, n2, n3),
				new Vector2f(tc1, tc2));
	}

	public void fixWeight(float wc, float wp, float wb) throws IllegalWeightSumException {
		this.position.w = wc;
		this.positionRP.w = wp;
		this.positionRB.w = wb;
		
		// Check sum
		float sum = this.position.w + this.positionRP.w + this.positionRB.w;
		if(Math.abs(1 - sum) > 0.1) {
			System.err.println("/!\\ Illegal weights sum : " + sum);
			throw new IllegalWeightSumException("Illegal weights sum : " + sum);
		}
	}
	
	public Vector4f getPosition() {
		return position;
	}

	public void setPosition(Vector4f position) {
		this.position = position;
	}

	public Vector4f getPositionRP() {
		return positionRP;
	}

	public void setPositionRP(Vector4f positionRP) {
		this.positionRP = positionRP;
	}

	public Vector4f getPositionRB() {
		return positionRB;
	}

	public void setPositionRB(Vector4f positionRB) {
		this.positionRB = positionRB;
	}

	public void setCurrentWeight(float weight) {
		this.position.z = weight;
	}
	
	public void setParentWeight(float weight) {
		this.positionRP.z = weight;
	}
	
	public void setBrotherWeight(float weight) {
		this.positionRB.z = weight;
	}
	
	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

	public void setTexCoord(Vector2f texCoord) {
		this.texCoord = texCoord;
	}

}
