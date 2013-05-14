package plants.xml;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL3;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import plants.dataTree.LeafTreeNode;
import plants.dataTree.TrunckTreeNode;

public class JDOMHierarchy {

	private final Element root;
	private org.jdom2.Document document;
	
	public JDOMHierarchy(File XMLFile){
		
		final SAXBuilder sxb = new SAXBuilder();
		
		try{
			this.document = sxb.build(XMLFile);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		this.root = this.document.getRootElement();
	}
	
	public void printXML(){
		try{
			final XMLOutputter exit = new XMLOutputter(Format.getPrettyFormat());
			exit.output(this.document, System.out);
		} catch(java.io.IOException e){
			e.printStackTrace();
		}
	}
	
	public Element getRoot(){
		return this.root;
	}
	
	public static void fillTree(GL3 gl, Element JDOMelement, DefaultMutableTreeNode tree) {
		
		if (!JDOMelement.getChildren().isEmpty()) {

			List<Element> childrenList = JDOMelement.getChildren();
			Iterator<Element> itChildren = childrenList.iterator();

			// Get children axes
			ArrayList<Vector3f> axesList = new ArrayList<Vector3f>();
			float[] radiusList = new float[3];
			int q = 0;
			for (Element e : childrenList) {
				if (e.getName() == "trunck") {
					String axeXYZ = e.getAttributeValue("axe");
					String vect[] = axeXYZ.split(" ");
					float x = Float.parseFloat(vect[0]);
					float y = Float.parseFloat(vect[1]);
					float z = Float.parseFloat(vect[2]);
					axesList.add(new Vector3f(x, y, z));
					radiusList[q] = Float.parseFloat(e.getAttributeValue("radius"));
					++q;
				}
			}
			
			itChildren = childrenList.iterator();
			int numChild = 0;
			int nbChildren = axesList.size();

			while (itChildren.hasNext()) {

				Element JDOMchild = (Element) itChildren.next();
				DefaultMutableTreeNode treeChild = null;

				switch (JDOMchild.getName()) {

				case "trunck":
					
					float rad = Float.parseFloat(JDOMchild.getAttributeValue("radius"));
					float length = Float.parseFloat(JDOMchild.getAttributeValue("length"));
					
					float radp = rad;
					if(JDOMchild.getParentElement().getAttributeValue("radius") != null) {
						radp = Float.parseFloat(JDOMchild.getParentElement().getAttributeValue("radius"));
					}

					float radb = 1;
					if(nbChildren > 1) {
						if(numChild == 0) {
							radb = radiusList[1];
						} else {
							radb = radiusList[0];
						}	
					}

					String axeXYZ = JDOMchild.getAttributeValue("axe");
					String vect[] = axeXYZ.split(" ");
					float x = Float.parseFloat(vect[0]);
					float y = Float.parseFloat(vect[1]);
					float z = Float.parseFloat(vect[2]);

					float plength;
					if(JDOMchild.getParentElement() == null) {
						plength = 0;
					} else {
						plength = Float.parseFloat(JDOMchild.getParentElement().getAttributeValue("length"));
					}
					Vector3f v = new Vector3f(x, y, z);
					v.normalize();

					Matrix4f PASSAGEbrotherParent = new Matrix4f();
					PASSAGEbrotherParent.setIdentity();

					// Matrice de passage FILS -> PERE

					Vector3f X = new Vector3f(1.0f, 0.0f, 0.0f);
					Vector3f w = new Vector3f();
					Vector3f u = new Vector3f();

					if(Math.abs(X.dot(v)) > 0.8) {
						X.x = 0.0f;
						X.y = 1.0f;
						X.z = 0.0f;
						w.cross(v, X);
						w.normalize();
						u.cross(v, w);
						u.normalize();
					} else {
						w.cross(X, v);
						w.normalize();
						u.cross(v, w);
						u.normalize();
					}

					Matrix4f PASSAGEchildParent = new Matrix4f();

					if(plength == 0) {
						PASSAGEchildParent = new Matrix4f(
								1.0f,0.0f,0.0f,0.0f,
								0.0f,1.0f,0.0f,0.0f,
								0.0f,0.0f,1.0f,0.0f,
								0.0f,0.0f,0.0f,1.0f);
					}
					else if(v.x < 0) {
						PASSAGEchildParent = new Matrix4f(
								(float)Math.cos(Math.PI/4),(float)Math.sin(Math.PI/4), 0 , 0,
								-(float)Math.sin(Math.PI/4),(float)Math.cos(Math.PI/4), 0, 0,
								0					, 0	,					1.0f, 0,
								0					, 0	,					0, 1.0f);
					}
					else {
						PASSAGEchildParent = new Matrix4f(
							(float)Math.cos(Math.PI/4),-(float)Math.sin(Math.PI/4), 0 , 0,
							(float)Math.sin(Math.PI/4),(float)Math.cos(Math.PI/4), 0, 0,
							0					, 0	,					1.0f, 0,
							0					, 0	,					0, 1.0f);
					}

					if(nbChildren == 1) {
						PASSAGEchildParent.m13 = 0.87f*plength;
					} else {
						PASSAGEchildParent.m13 = 0.95f*plength;
					}

					Matrix4f PASSAGEchildBrother = new Matrix4f();

					if(nbChildren > 1) {

						// Matrice de passage FRERE -> PERE

						Vector3f brotherAxe = new Vector3f();

						if(numChild == 0) {
							brotherAxe = axesList.get(1);
						} else {
							brotherAxe = axesList.get(0);
						}

						if(Math.abs(brotherAxe.dot(X)) > 0.5) {
							u.cross(brotherAxe, new Vector3f(1, 0, 0));
						} else {
							u.cross(brotherAxe, X);
						}
						u.normalize();
						w = new Vector3f();
						w.cross(u, brotherAxe);
						w.normalize();

						if(v.x < 0) {
							PASSAGEchildBrother = new Matrix4f(
									(float)Math.cos(Math.PI/2),(float)Math.sin(Math.PI/2), 0 , 0,
									-(float)Math.sin(Math.PI/2),(float)Math.cos(Math.PI/2), 0, 0,
									0					, 0	,					1.0f, 0,
									0					, 0	,					0, 1.0f);
						}
						else {
							PASSAGEchildBrother = new Matrix4f(
								(float)Math.cos(Math.PI/2),-(float)Math.sin(Math.PI/2), 0 , 0,
								(float)Math.sin(Math.PI/2),(float)Math.cos(Math.PI/2), 0, 0,
								0					, 0	,					1.0f, 0,
								0					, 0	,					0, 1.0f);
						}

					}


					else {
						PASSAGEchildBrother.setIdentity();
					}

					boolean uniqueChild = (nbChildren==1)?true:false;

					treeChild = new DefaultMutableTreeNode(new TrunckTreeNode(gl, length, v, rad, radp, radb, PASSAGEchildParent, PASSAGEchildBrother, uniqueChild));

					break;

				case "leaf":
					float rho = Float.parseFloat(JDOMchild.getAttributeValue("rho"));
					float theta = Float.parseFloat(JDOMchild.getAttributeValue("theta"));
					float phi = Float.parseFloat(JDOMchild.getAttributeValue("phi"));
					float scale = Float.parseFloat(JDOMchild.getAttributeValue("scale"));
					float height = Float.parseFloat(JDOMchild.getAttributeValue("height"));
					float currentRadius = Float.parseFloat(JDOMchild.getAttributeValue("radiusCurrent"));
					float currentLength = Float.parseFloat(JDOMchild.getAttributeValue("lengthCurrent"));
					float orientation = Float.parseFloat(JDOMchild.getAttributeValue("angleDiscLat"));
					treeChild = new DefaultMutableTreeNode(new LeafTreeNode(gl, rho, theta, phi, scale, height, currentRadius, currentLength, orientation));
					break;

				default:
					System.err.println("Logic error in xml file");
					break;
				}

				fillTree(gl, JDOMchild, treeChild);
				tree.add(treeChild);
				numChild++;
			}
		}
	}
	
	public static void displayTree(DefaultMutableTreeNode root) {
		System.out.println(root.getUserObject());
		for(int i =0; i < root.getChildCount(); i++) {
			System.out.print("     ");
			JDOMHierarchy.displayTree((DefaultMutableTreeNode)root.getChildAt(i));
		}
	}
}
