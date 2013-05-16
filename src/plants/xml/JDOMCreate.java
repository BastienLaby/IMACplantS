package plants.xml;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import javax.vecmath.Vector3f;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
 
public class JDOMCreate {
	
	/**
	 * XML root
	 */
	private static Element root = new Element("arbre");
	
	/**
	 * Root based document
	 */
	private static Document document = new Document(root);
 
	
	/** TRUNCK GENERATION PARAMETERS **/
	
	private static final float INITIAL_LENGTH = 20.0f;
	private static final float INITIAL_RADIUS = 0.05f * INITIAL_LENGTH;
	private static final float INITIAL_LENGTH_RATIO_VARIATION = 0.3f;
	private static final float INITIAL_RADIUS_RATIO_VARIATION = 0.3f;
	
	private static final float TWO_SONS_PROBABILITY = 1.0f;
	
	private static final float HERITED_LENGTH_RATIO = 0.4f;
	private static final float HERITED_RADIUS_RATIO = 0.3f;
	
	private static final float HERITED_LENGTH_RATIO_VARIATION = 0.3f;
	private static final float HERITED_RADIUS_RATIO_VARIATION = 0.1f;

	private static final float CURRENT_MINIMAL_RADIUS_ACCEPTABLE = 0.1f;
	private static final float CURRENT_MINIMAL_LENGTH_ACCEPTABLE = 0.03f*INITIAL_LENGTH;
	
	/** LEAF GENERATION PARAMETERS **/

	private static final int NB_LEAF_MAX = 1;
	
	private static final float MINIMAL_RHO = 0.0f;
	private static final float MAXIMAL_RHO = 360.0f;
	private static final float MINIMAL_PHI = 0.0f;
	private static final float MAXIMAL_PHI = 180.0f;
	private static final float MINIMAL_THETA = 0.0f;
	private static final float MAXIMAL_THETA = 360.0f;
	
	private static final float MIN_LATERAL_ANGLE = 0.0f;
	private static final float MAX_LATERAL_ANGLE = 360.0f;
	
	private static final float MINIMAL_HEIGHT_PERCENTAGE_POSITION = 35.0f;
	private static final float MAXIMAL_HEIGHT_PERCENTAGE_POSITION = 100.0f;
	
	private static final float MINIMAL_SCALE = 0.8f;
	private static final float MAXIMAL_SCALE = 1.2f;
	
	private JDOMCreate() {
	}
	
	public static void createTreeAt(String filepath) {
		
		root = new Element("arbre");
		document = new Document(root);
		
		// Create implicit root
		Attribute length = new Attribute("length","0");
		root.setAttribute(length);
		createTree();
		
		File file = new File(filepath);
		file.delete();
		saveTree(filepath);
		
	}

	private static void createTree() {
	   
	   // Initiate random
	   Random rand = new Random();
	   rand.setSeed(System.currentTimeMillis());
	   
	   /** CREATING THE MAIN TRUNC **/
	   
	   float initialTruncLength = INITIAL_LENGTH + (2*rand.nextFloat()-1) * INITIAL_LENGTH_RATIO_VARIATION * INITIAL_LENGTH;
	   float initialTruncRadius = INITIAL_RADIUS + (2*rand.nextFloat()-1) * INITIAL_RADIUS_RATIO_VARIATION * INITIAL_RADIUS;
	   String initialAxe = new String("0 1 0");
	   
	   // Creating the trunc element
	   Element trunck = new Element("trunck");
	   trunck.setAttribute(new Attribute("length", ""+initialTruncLength));
	   trunck.setAttribute(new Attribute("radius", ""+initialTruncRadius));
	   trunck.setAttribute(new Attribute("axe", initialAxe));
	   root.addContent(trunck);
	   
	   /** CREATING THE TRUNC SONS **/
	   int iter = 0;
	   createLink(trunck, iter);
   }

	private static void createLink(Element parentTrunck, int iteration) {
	   
		float parentLength = Float.parseFloat(parentTrunck.getAttributeValue("length"));
		float parentRadius = Float.parseFloat(parentTrunck.getAttributeValue("radius"));
		
		float heritedRadius;
		float heritedLength;
		
		if( parentRadius >= CURRENT_MINIMAL_RADIUS_ACCEPTABLE ) {
		   
			heritedRadius = parentRadius * HERITED_RADIUS_RATIO;
					   
		} else {
			
			heritedRadius = parentRadius;
			
		}
		if( parentLength <= CURRENT_MINIMAL_LENGTH_ACCEPTABLE) {
			heritedLength = parentLength;
		} else {
			heritedLength = parentLength * HERITED_LENGTH_RATIO;
		}
		
		// Initiate random
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
	   
			if( rand.nextFloat() < TWO_SONS_PROBABILITY ) {
			 
		   
			/** HANDLE SON ONE **/
		   
			float newLength1 =  heritedLength + (2*rand.nextFloat()-1) * HERITED_LENGTH_RATIO_VARIATION * heritedLength;
			float newLength2 =  heritedLength + (2*rand.nextFloat()-1) * HERITED_LENGTH_RATIO_VARIATION * heritedLength;
			float newRadius1 = heritedRadius + (2*rand.nextFloat()-1) * HERITED_RADIUS_RATIO_VARIATION * heritedRadius;
			float newRadius2 = heritedRadius + (2*rand.nextFloat()-1) * HERITED_RADIUS_RATIO_VARIATION * heritedRadius;
			Vector3f newAxe1 = new Vector3f(
					0.2f + rand.nextFloat()*0.3f,
					0.5f,
					0.5f + rand.nextFloat()*0.5f);
			Vector3f newAxe2 = new Vector3f(
					-0.2f + rand.nextFloat()*0.3f,
					0.5f,
					-0.5f + rand.nextFloat()*0.5f);
		   
			String newAxe1String = new String(newAxe1.x + " " + newAxe1.y + " " + newAxe1.z);
			String newAxe2String = new String(newAxe2.x + " " + newAxe2.y + " " + newAxe2.z);
		   
			Element son1 = new Element("trunck");
			son1.setAttribute("length", newLength1+"");
			son1.setAttribute("radius", newRadius1+"");
			son1.setAttribute("axe", newAxe1String);
			   
			Element son2 = new Element("trunck");
			son2.setAttribute("length", newLength2+"");
			son2.setAttribute("radius", newRadius2+"");
			son2.setAttribute("axe", newAxe2String);
			   
			parentTrunck.addContent(son1);
			parentTrunck.addContent(son2);
			
			addLeaf(son1);
			iteration++;
			if(iteration < 6) {
				createLink(son1, iteration);
			}
			
			addLeaf(son2);
			iteration++;
			if(iteration < 6) {
				createLink(son2, iteration);
			}
		   
		}
	}
   
	public static void addLeaf(Element trunck) {
	   
		float truncLength = Float.parseFloat(trunck.getAttributeValue("length"));
		float truncRadius = Float.parseFloat(trunck.getAttributeValue("radius"));
		
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		
		int nbLeaf =(int)(((INITIAL_RADIUS - truncRadius) / INITIAL_RADIUS) * NB_LEAF_MAX);
		
		float rho;
		float theta;
		float phi;
		float lateralAngle;
		float heightPercentagePosition;
		float scale;
		int id;
 
		for(int i = 0; i < nbLeaf; ++i) {
		   
			rho = MINIMAL_RHO + rand.nextFloat() * (MAXIMAL_RHO - MINIMAL_RHO);
			theta = MINIMAL_THETA + rand.nextFloat() * (MAXIMAL_THETA - MINIMAL_THETA);
			phi = MINIMAL_PHI + rand.nextFloat() * (MAXIMAL_PHI - MINIMAL_PHI);
			   
			lateralAngle = MIN_LATERAL_ANGLE + rand.nextFloat() * (MAX_LATERAL_ANGLE - MIN_LATERAL_ANGLE);
			if(lateralAngle >= 270.0f || lateralAngle <= 90.0f) {
			phi = -phi;
			}
			  
			heightPercentagePosition = rand.nextFloat()*(MAXIMAL_HEIGHT_PERCENTAGE_POSITION - MINIMAL_HEIGHT_PERCENTAGE_POSITION) + MINIMAL_HEIGHT_PERCENTAGE_POSITION;
			scale = rand.nextFloat()*(MAXIMAL_SCALE - MINIMAL_SCALE) + MINIMAL_SCALE;
			
			id = rand.nextInt(3);
			
			Element leaf = new Element("leaf");
			
			leaf.setAttribute(new Attribute("id", ""+id));
			leaf.setAttribute(new Attribute("rho", ""+rho));
			leaf.setAttribute(new Attribute("theta", ""+theta));
			leaf.setAttribute(new Attribute("phi",""+phi));
			leaf.setAttribute(new Attribute("angleDiscLat", ""+lateralAngle));
			leaf.setAttribute(new Attribute("height", ""+heightPercentagePosition));
			leaf.setAttribute(new Attribute("scale", ""+scale));
			leaf.setAttribute(new Attribute("lengthCurrent", ""+truncLength));
			leaf.setAttribute(new Attribute("radiusCurrent", ""+truncRadius));
			   
			trunck.addContent(leaf);
		}
	}
   
	  private static void displayTree() {
			  try {
				  XMLOutputter outputterXML = new XMLOutputter(Format.getPrettyFormat());
				  outputterXML.output(document, System.out);
			  } catch (java.io.IOException e){
				  e.printStackTrace();
			  }
	  }
	  
	  
	  private static void saveTree(String fichier) {
	     try {
	        XMLOutputter outputterXML = new XMLOutputter(Format.getPrettyFormat());
	        outputterXML.output(document, new FileOutputStream(fichier));
	     } catch (java.io.IOException e){
	    	 e.printStackTrace();
	     }
	  }
      
      
   }