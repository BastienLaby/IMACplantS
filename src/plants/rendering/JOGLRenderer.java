package plants.rendering;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import plants.camera.FPSCamera;
import plants.dataTree.LeafTreeNode;
import plants.dataTree.PlantsTreeNode;
import plants.dataTree.TrunckTreeNode;
import plants.matrix.MatrixStack;
import plants.matrix.Transform;
import plants.rendering.drawable.Cube;
import plants.rendering.drawable.HeightMap;
import plants.rendering.shaders.TreeShaderLocations;
import plants.xml.JDOMCreate;
import plants.xml.JDOMHierarchy;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


public class JOGLRenderer implements GLEventListener {
	
	/* OpenGL Ressources */
	
	// utils
	private final GLU glu = new GLU();
	private String glError = new String();
	
	// GL context
	private GL3 gl;	
	
	// Shaders
	private TreeShaderLocations skyboxLoc, groundLoc, treeLoc, leafLoc;
	
	// Matrix relatives
	private MatrixStack stack;
	
	// UI
	private FPSCamera camera;
	
	// Textures
	private Texture texSkybox, texGround, texTree, texLeaf1, texLeaf2, texLeaf3;
	
	// Drawable
	private Cube skybox;
	private HeightMap ground;
	ArrayList<DefaultMutableTreeNode> trees = new ArrayList<>();
	
	@Override
	public void display (GLAutoDrawable drawable) {

		// Get openGL Context
		this.gl = drawable.getGL().getGL3();

		// Clear GL context
		this.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		this.gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

		// Check errors
		this.glError = this.glu.gluErrorString(this.gl.glGetError());
		if(this.glError != "no error") {
			System.err.println("GL ERROR : " + this.glError);
			System.err.println("The program will close...");
			System.exit(0);
		}

		/* DRAW */
		
		// Select the current program shader
		
		this.stack.push();
		
			this.stack.mult(this.camera.getViewMatrix());
			
			// Drawing Skybox
			this.stack.push();
				this.gl.glUseProgram(this.skyboxLoc.PROGRAM_ID);
				this.gl.glActiveTexture(GL3.GL_TEXTURE1);
				this.texSkybox.enable(this.gl);
				this.texSkybox.bind(this.gl);
				this.gl.glUniform1i(this.skyboxLoc.LOC_TEX, 1);
				this.stack.translate(this.camera.getPosition());
				this.stack.scale(new Vector3f(2.0f, 2.0f, 2.0f));
				this.gl.glUniformMatrix4fv(this.skyboxLoc.LOC_MV, 1, false, Transform.toFloatArray(this.stack.top()), 0);
				this.skybox.draw(this.gl);
			this.stack.pop();
			
			// Drawing HeightMap
			this.stack.push();
				this.gl.glUseProgram(this.groundLoc.PROGRAM_ID);
				this.gl.glActiveTexture(GL3.GL_TEXTURE1);
				this.texGround.enable(this.gl);
				this.texGround.bind(this.gl);
				this.gl.glUniform1i(this.groundLoc.LOC_TEX, 1);
				this.stack.scale(new Vector3f(500.0f, 1.0f, 500.0f));
				this.stack.translate(new Vector3f(0.0f, -17.5f, 0.0f));
				this.gl.glUniformMatrix4fv(this.groundLoc.LOC_MV, 1, false, Transform.toFloatArray(this.stack.top()), 0);
				this.ground.draw(this.gl);
			this.stack.pop();
			
		this.stack.pop();
		
		// Drawing tree
		for(int i = 0; i < 2; i++) {
			this.stack.push();
			this.stack.rotate(new Vector3f(0f, 1f, 0f), i*135);
				this.render(this.trees.get(0));
			this.stack.pop();
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		// Create the FPS Camera
		this.camera = new FPSCamera();
		
		// Get openGL Context
		this.gl = drawable.getGL().getGL3();

		for(int i = 0; i < 1; i++) {
			this.createTree();
		}
		
		// Init openGL
		this.gl.glEnable(GL3.GL_DEPTH_TEST);
		this.gl.glDepthFunc(GL3.GL_LEQUAL);
		this.gl.glEnable(GL3.GL_TEXTURE_2D);
		
		// Load shaders and init OpenGL Program
		this.skyboxLoc = new TreeShaderLocations(drawable, "src/plants/rendering/shaders/skybox.vs.glsl", "src/plants/rendering/shaders/skybox.fs.glsl");
		this.groundLoc = new TreeShaderLocations(drawable, "src/plants/rendering/shaders/ground.vs.glsl", "src/plants/rendering/shaders/ground.fs.glsl");
		this.treeLoc = new TreeShaderLocations(drawable, "src/plants/rendering/shaders/tree.vs.glsl", "src/plants/rendering/shaders/tree.fs.glsl");
		this.leafLoc = new TreeShaderLocations(drawable, "src/plants/rendering/shaders/leaf.vs.glsl", "src/plants/rendering/shaders/leaf.fs.glsl");
		
		// Send Projection Matrix to shadersMatrix4f
		final Matrix4f P = new Matrix4f(Transform.Perspective(70.0f, 900.0f/900.0f, 0.1f, 1000.0f));
		
		this.gl.glUseProgram(this.skyboxLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.skyboxLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.groundLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.groundLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.treeLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.treeLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.leafLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.leafLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		// Create the this.stack Matrix
		this.stack = new MatrixStack();
		
		// Load and init textures
		try {
			this.texSkybox = TextureIO.newTexture(new File("src/plants/rendering/img/skybox.jpg"), true);
			this.texGround = TextureIO.newTexture(new File("src/plants/rendering/img/automne2.jpg"), true);
			this.texTree = TextureIO.newTexture(new File("src/plants/rendering/img/tree5.jpg"), false);
			this.texLeaf1 = TextureIO.newTexture(new File("src/plants/rendering/img/leaf3.png"), false);
			this.texLeaf2 = TextureIO.newTexture(new File("src/plants/rendering/img/leaf2.png"), false);
			this.texLeaf3 = TextureIO.newTexture(new File("src/plants/rendering/img/leaf3.png"), false);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.texSkybox.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texSkybox.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		this.texGround.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texGround.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		this.texTree.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texTree.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		this.texLeaf1.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texLeaf1.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		this.texLeaf2.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texLeaf2.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		this.texLeaf3.setTexParameterf(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
		this.texLeaf3.setTexParameterf(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		
		// Create drawable objects
		this.skybox = new Cube(this.gl);
		
		try {
			this.ground = new HeightMap(this.gl, "src/plants/rendering/heightmaps/hmap2.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Init openGL : Success");

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		
		// Recalculate the projection matrix
		final Matrix4f P = new Matrix4f(Transform.Perspective(70.0f, width/(float)height, 0.1f, 1000.0f));
		
		this.gl.glUseProgram(this.skyboxLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.skyboxLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.groundLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.groundLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.treeLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.treeLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);
		
		this.gl.glUseProgram(this.leafLoc.PROGRAM_ID);
		this.gl.glUniformMatrix4fv(this.leafLoc.LOC_P, 1, false, Transform.toFloatArray(P), 0);

	}
	
	public FPSCamera getCamera() {
		return camera;
	}

	public void setCamera(FPSCamera camera) {
		this.camera = camera;
	}

	public void createTree() {
		
		int i = this.trees.size() + 1;
		String filename = new String("src/plants/xml/randomTree"+i+".xml");
		JDOMCreate.createTreeAt(filename);
		JDOMHierarchy xmlHierarchy = new JDOMHierarchy(new File(filename));
		//JDOMHierarchy xmlHierarchy = new JDOMHierarchy(new File("src/plants/xml/randomxml.xml"));
		
		PlantsTreeNode root = new TrunckTreeNode();
		DefaultMutableTreeNode treeNodeRoot = new DefaultMutableTreeNode(root, true);
		JDOMHierarchy.fillTree(this.gl, xmlHierarchy.getRoot(), treeNodeRoot);
		
		MatrixStack stack = new MatrixStack();
		JOGLRenderer.computeMVmatrix(treeNodeRoot, stack);
		this.trees.add(treeNodeRoot);
		
	}
	
	/**
	 * For each component of the tree, calculate the modelView Matrix according to the parents parameters.
	 * @param root : the element which is curently being computed
	 * @param stack : a Matrix Stach wherein the top matrix represent the parent modelview matrix
	 */
	public static void computeMVmatrix(DefaultMutableTreeNode root, MatrixStack stack) {
		
		if(((PlantsTreeNode)root.getUserObject()).getType() == "leaf") {
			
			float ty = -((LeafTreeNode)root.getUserObject()).getLengthTrunck() + ((LeafTreeNode)root.getUserObject()).getLengthTrunck()*(((LeafTreeNode)root.getUserObject()).getHeight())/100.0f;
			float tx = ((LeafTreeNode)root.getUserObject()).getRadiusTrunck();
			float orientation = ((LeafTreeNode)root.getUserObject()).getOrientation();
			
			stack.translate(new Vector3f(0f, ty, 0f)); // translate h
			stack.rotate(new Vector3f(0f, 1f, 0f), orientation);
			stack.translate(new Vector3f(tx, 0f, 0f));
			stack.rotate(((PlantsTreeNode) root.getUserObject()).getRotationMatrix());
			stack.scale(new Vector3f(((LeafTreeNode)root.getUserObject()).getScale(), ((LeafTreeNode)root.getUserObject()).getScale(), 1f));
			((PlantsTreeNode) root.getUserObject()).setMV(stack.top());
		}
		
		if(((PlantsTreeNode)root.getUserObject()).getType() == "trunck") {
			stack.rotate(((PlantsTreeNode) root.getUserObject()).getRotationMatrix());
			((PlantsTreeNode) root.getUserObject()).setMV(stack.top());
			for (int i = 0; i < root.getChildCount(); i++) {
				stack.push();
					float length = ((TrunckTreeNode) root.getUserObject()).getLength();
					stack.translate(new Vector3f(0.0f, length, 0.0f));
					computeMVmatrix((DefaultMutableTreeNode) root.getChildAt(i), stack);
				stack.pop();
			}
		}
	}
	
	/**
	 * For each element of the tree, draw it using glDrawArray
	 * @param tree : the root of the tree currently treated
	 */
	private void render(DefaultMutableTreeNode tree) {

		
		Matrix4f MV = new Matrix4f();
		
		switch (((PlantsTreeNode)tree.getUserObject()).getType()) {
		
			case "trunck" :
				
				// Calculate Parent ModelView Matrix
				Matrix4f MVp = new Matrix4f();
				if (tree.getParent() == null) {
					MVp.setIdentity();
				} else {
					DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) tree.getParent());
					MVp = new Matrix4f(((TrunckTreeNode) parent.getUserObject()).getMV());
				}
		
				// Calculate Brother ModelView Matrix
				Matrix4f MVb = new Matrix4f();
				if (tree.getSiblingCount() == 2) {
					if (tree.getPreviousSibling() == null) {
						MVb = new Matrix4f(((TrunckTreeNode) tree.getNextSibling()
								.getUserObject()).getMV());
					} else {
						MVb = new Matrix4f(((TrunckTreeNode) tree.getPreviousSibling()
								.getUserObject()).getMV());
					}
				} else {
					MVb = new Matrix4f(((TrunckTreeNode) tree.getUserObject()).getMV());
				}
				
				// Rend trunck
				
				gl.glUseProgram(this.treeLoc.PROGRAM_ID);
				gl.glActiveTexture(GL3.GL_TEXTURE1);
				this.texTree.enable(this.gl);
				this.texTree.bind(this.gl);
				this.gl.glUniform1i(this.treeLoc.LOC_TEX, 1);
				
				MV = new Matrix4f(this.camera.getViewMatrix());
				MV.mul(this.stack.top());
				this.gl.glUniformMatrix4fv(this.treeLoc.LOC_MV, 1, false, Transform.toFloatArray(MV), 0);
				
				((TrunckTreeNode) tree.getUserObject()).render(gl,
															   this.treeLoc.LOC_MV_CURRENT,
															   this.treeLoc.LOC_MV_PARENT,
															   MVp,
															   this.treeLoc.LOC_MV_BROTHER,
															   MVb,
															   this.treeLoc.LOC_S_CURRENT,
															   this.treeLoc.LOC_S_PARENT,
															   this.treeLoc.LOC_S_BROTHER);
				
				// draw children
				for (int i = 0; i < tree.getChildCount(); ++i) {
					this.render((DefaultMutableTreeNode) tree.getChildAt(i));
				}
				
				break;
			
			case "leaf" :

				this.gl.glUseProgram(this.leafLoc.PROGRAM_ID);
				this.gl.glActiveTexture(GL3.GL_TEXTURE1);
				
				if(((LeafTreeNode)tree.getUserObject()).getId() == 0) {
					this.texLeaf1.enable(gl);
					this.texLeaf1.bind(gl);
				}
				else if(((LeafTreeNode)tree.getUserObject()).getId() == 1) {
					this.texLeaf2.enable(gl);
					this.texLeaf2.bind(gl);
				}
				else if(((LeafTreeNode)tree.getUserObject()).getId() == 2) {
					this.texLeaf3.enable(gl);
					this.texLeaf3.bind(gl);
				}
				
				this.gl.glUniform1i(this.leafLoc.LOC_TEX, 1);
				
				MV = new Matrix4f(this.camera.getViewMatrix());
				MV.mul(this.stack.top());
				gl.glUniformMatrix4fv(this.leafLoc.LOC_MV, 1, false, Transform.toFloatArray(MV), 0);
				
				((LeafTreeNode)tree.getUserObject()).render(gl, this.leafLoc.LOC_MV_CURRENT);
					
				break;
		
		}
		
	}
	
}
