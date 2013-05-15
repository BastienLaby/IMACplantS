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
 
	private static final float INITIAL_LENGTH = 20.0f;
	private static final float INITIAL_RADIUS = 0.1f * INITIAL_LENGTH;
	private static final float INITIAL_LENGTH_RATIO_VARIATION = 0.3f; // + or - 10%
	private static final float INITIAL_RADIUS_RATIO_VARIATION = 0.3f; // + or - 10%
	
	private static final float TWO_SONS_PROBABILITY = 1.0f;
	
	private static final float HERITED_LENGTH_RATIO = 0.8f;
	private static final float HERITED_RADIUS_RATIO = 0.5f;
	
	private static final float HERITED_LENGTH_RATIO_VARIATION = 0.3f; // + or - 10%
	private static final float HERITED_RADIUS_RATIO_VARIATION = 0.2f; // + or - 5%
	
	
	// Recursivity Stop condition
	private static final float CURRENT_MINIMAL_RADIUS_ACCEPTABLE = 0.1f;
	
	private JDOMCreate() {
	}
	
	public static void createTreeAt(String filename) {
		
		root = new Element("arbre");
		document = new Document(root);
		
		// Create implicit root
		Attribute length = new Attribute("length","0");
		root.setAttribute(length);
		createTree();
		
		File file = new File("src/plants/xml/" + filename);
		file.delete();
		saveTree("src/plants/xml/" + filename);
		
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
	   
	   createLink(trunck);
   }

	private static void createLink(Element parentTrunck) {
	   
		float parentLength = Float.parseFloat(parentTrunck.getAttributeValue("length"));
		float parentRadius = Float.parseFloat(parentTrunck.getAttributeValue("radius"));
		   
		if( parentRadius >= CURRENT_MINIMAL_RADIUS_ACCEPTABLE ) {
		   
			// Initiate random
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());
		   
				if( rand.nextFloat() < TWO_SONS_PROBABILITY ) {
			   
				float heritedLength = parentLength * HERITED_LENGTH_RATIO;
				float heritedRadius = parentRadius * HERITED_RADIUS_RATIO;
			   
				/** HANDLE SON ONE **/
			   
				float newLength1 =  heritedLength + (2*rand.nextFloat()-1) * HERITED_LENGTH_RATIO_VARIATION * heritedLength;
				float newLength2 =  heritedLength + (2*rand.nextFloat()-1) * HERITED_LENGTH_RATIO_VARIATION * heritedLength;
				float newRadius1 = heritedRadius + (2*rand.nextFloat()-1) * HERITED_RADIUS_RATIO_VARIATION * heritedRadius;
				float newRadius2 = heritedRadius + (2*rand.nextFloat()-1) * HERITED_RADIUS_RATIO_VARIATION * heritedRadius;
				Vector3f newAxe1 = new Vector3f(
						rand.nextFloat()*(0.8f - 0.2f) + 0.2f,
						0.5f,
						rand.nextFloat()*(0.8f - 0.1f) + 0.1f);
				Vector3f newAxe2 = new Vector3f(
						-rand.nextFloat()*(0.8f - 0.2f) + 0.2f,
						0.5f,
						-rand.nextFloat()*(0.8f - 0.1f) + 0.1f);
			   
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
				   
				createLink(son1);
				createLink(son2);
			   
		   }		   
	   }
   }
   
   public static void addLeaf(Element trunck, float lengthBranch, float radiusBranch) {
	   Random rand = new Random();
	   rand.setSeed(System.currentTimeMillis());
	   
	   float minNbLeaf = 2.0f;
	   float maxNbLeaf = 5.0f;
	   float nbFeuille = rand.nextFloat()*(maxNbLeaf - minNbLeaf) + minNbLeaf;
	   float minAngle = 0.0f;
	   float maxAngle = 360.0f;
	   float minPhi = 0f;
	   float maxPhi = 180f;
	   float minPercent = 35.0f;
	   float maxPercent = 100.0f;
	   float minScale = 0.8f;
	   float maxScale = 1.2f;
	   float scale;
	   float pourcentageHauteur;
	   float angleSurDiscLat;
	   float rho;
	   float theta;
	   float phi;
	   int IDtype;
 
	   for(int i = 0; i < nbFeuille; ++i) {
		   rho = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   theta = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   pourcentageHauteur = rand.nextFloat()*(maxPercent - minPercent) + minPercent;
		   scale = rand.nextFloat()*(maxScale - minScale) + minScale;
		   IDtype = rand.nextInt(2);
		   angleSurDiscLat = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
		   if(angleSurDiscLat >= 270 || angleSurDiscLat <= 90) {
			   phi = -phi;
		   }
		   
		   
		   Element leaf = new Element("leaf");
		   trunck.addContent(leaf);
		   Attribute IDTYPE = new Attribute("type", ""+IDtype);
		   leaf.setAttribute(IDTYPE);
		   Attribute RHO = new Attribute("rho",""+rho+"");
		   leaf.setAttribute(RHO);
		   Attribute THETA = new Attribute("theta",""+theta+"");
		   leaf.setAttribute(THETA);
		   Attribute PHI = new Attribute("phi",""+phi+"");
		   leaf.setAttribute(PHI);
		   Attribute ANGLESURDISCLAT = new Attribute("angleDiscLat",""+angleSurDiscLat+"");
		   leaf.setAttribute(ANGLESURDISCLAT);
		   Attribute HAUTEUR = new Attribute("height",""+pourcentageHauteur+"");
		   leaf.setAttribute(HAUTEUR);
		   Attribute SCALE = new Attribute("scale",""+scale+"");
		   leaf.setAttribute(SCALE);
		   Attribute LENGTHCOURANT = new Attribute("lengthCurrent",""+lengthBranch+"");
		   leaf.setAttribute(LENGTHCOURANT);
		   Attribute RADIUSCOURANT = new Attribute("radiusCurrent",""+radiusBranch+"");
			   leaf.setAttribute(RADIUSCOURANT);
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