package plants.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import plants.rendering.JOGLRenderer;

import com.jogamp.opengl.util.FPSAnimator;

public class JOGLWindow {

	private final JFrame frame;
	private final GLCanvas canvas;
	private final GLCapabilities capabilities;
	private final GLProfile profile;	
	private final FPSAnimator animator;
	private final JOGLRenderer renderer;
	
	private final int WINDOWS_WIDTH = 900;
	private final int WINDOWS_HEIGHT = 900;
	
	public JOGLWindow(String windowName) {
		
		// Init Frame
		this.frame = new JFrame(windowName);
		this.frame.setSize(WINDOWS_WIDTH, WINDOWS_HEIGHT);
		
		// Init GL Profile with default profile
		this.profile = GLProfile.getDefault();
		
		// Init GL Profile capabilities (set of capabilities that a window's rendering context must support)
		this.capabilities = new GLCapabilities(this.profile);
		
		// Init canvas and add gl profile to this canvas
		this.canvas = new GLCanvas(this.capabilities);
		
		// Define Listeners		
		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		
		canvas.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				
			}
			public void keyReleased(KeyEvent e) {}	
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyChar()) {
				case 'z' : case 'Z' : 
					renderer.getCamera().moveFront(1.0f);
				break;
				case 's' : case 'S' : 
					renderer.getCamera().moveFront(-1.0f);
				break;
				case 'q' : case 'Q' : 
					renderer.getCamera().moveLeft(1.0f);
				break;
				case 'd' : case 'D' : 
					renderer.getCamera().moveLeft(-1.0f);
				break;
			}
			}
			
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(renderer.getCamera() != null) {
					int offsetX = WINDOWS_WIDTH/2 - e.getX();
					int offsetY = WINDOWS_HEIGHT/2 - e.getY();
					renderer.getCamera().resetRotate();
					renderer.getCamera().rotateLeft(offsetX);
					renderer.getCamera().rotateUp(offsetY);
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Init Render class
		this.renderer = new JOGLRenderer();
		
		// Add render class to the current canvas
		this.canvas.addGLEventListener(this.renderer);
		
		// Set canvas background color
		this.canvas.setBackground(Color.BLACK);
		
		// Add canvas to the current frame
		this.frame.add(this.canvas);
 
		// Init Animator
		this.animator = new FPSAnimator(this.canvas, 60);
		
		// Add drawable canvas to the list managed by this animator
		this.animator.add(this.canvas);
		
		// Start animator
		this.animator.start();
		
		// Set the frame visible
		this.frame.setVisible(true);
	}
	
}
