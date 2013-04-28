package plants.rendering.shaders;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;


public class JOGLShaderProgram {
     
    private final int idVertexShader;
    private final int idFragmentShader;
    private final int idProgram;
    
	public JOGLShaderProgram(GLAutoDrawable drawable, String vsFilename, String fsFilename) {
		
		/*********************************************/
		/*** FILL AND COMPILE SHADERS INTO PROGRAM ***/
		/*********************************************/
		
		// Get GL context
		GL3 gl = drawable.getGL().getGL3();
		
		// Create shaders
		this.idVertexShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
		this.idFragmentShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
		
		/* Fill Vertex Shader */
		File vsFile = new File(vsFilename);
		this.fillShader(gl, vsFile, idVertexShader);

        /* Fill Fragment Shader */
		File fsFile = new File(fsFilename);
        this.fillShader(gl, fsFile, idFragmentShader);
        
        // Create program
        idProgram = gl.glCreateProgram();
        
        // Fill program
        this.fillProgram(gl);
        
	}
	
	private void fillShader(GL3 gl, File file, int idShader) {
		
		// Get shader sources from file
		String[] vertexShaderSource = GetShaderSource(file);
		int nbStringInVertexShaderSource = vertexShaderSource.length;
		int[] lengtOfEachStringInVertexShaderSource = new int[vertexShaderSource.length];
		for(int i = 0; i < vertexShaderSource.length; ++i) {
			lengtOfEachStringInVertexShaderSource[i] = vertexShaderSource[i].length();
		}
		
		// Fill shader with sources and compile it
        gl.glShaderSource(idShader, nbStringInVertexShaderSource, vertexShaderSource, lengtOfEachStringInVertexShaderSource, 0);
        gl.glCompileShader(idShader);
        
        // Check compile
        int[] compiled = new int[1];
        gl.glGetShaderiv(idShader, GL3.GL_COMPILE_STATUS, compiled, 0);
        if(compiled[0] != 0) {
        	System.out.println("Shader " + file.getName() + " compiled.");
        } else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(this.idVertexShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(this.idVertexShader, logLength[0], (int[])null, 0, log, 0);
            System.err.println("Error compiling the shader: " + new String(log));
            System.exit(1);
        }
	}
	
	private void fillProgram(GL3 gl) {
        
        // Attach the two shaders to it
        gl.glAttachShader(idProgram, idVertexShader);
        gl.glAttachShader(idProgram, idFragmentShader);

        // Associate attribute ids with the attribute names inside the vertex shader
        /*gl.glBindAttribLocation(idProgram, 0, "attribute_Position");
        gl.glBindAttribLocation(idProgram, 1, "attribute_Color");*/

        // Link Program
        gl.glLinkProgram(idProgram);
	}
	
	static String[] GetShaderSource(File shaderFile) {
		
		// Create list of strings
		ArrayList<String> shaderSource = new ArrayList<String>();
		
		// Create a scanner to read into the file
		Scanner scanner_shader = null;
		try {
			scanner_shader = new Scanner(shaderFile);
		} catch (FileNotFoundException e) {
			System.err.println("Shader " + shaderFile.toString() + " file was not found");
			System.exit(0);
		}
		
		// Setup scanner
		scanner_shader.useDelimiter("\n");	
		
		// Get each line of the file and add it into the list of strings
		while(scanner_shader.hasNext()){
			String temp = scanner_shader.next() + "\n";
			shaderSource.add(temp);
		}
		
		// Transform the list of strings into an array of strings
		String[] shaderSourceArray = new String[shaderSource.size()];
		shaderSourceArray = shaderSource.toArray(shaderSourceArray);
		int[] lengths= new int[shaderSourceArray.length];
		for(int i = 0; i < shaderSourceArray.length; i++){
			lengths[i] = shaderSourceArray[i].length();
		}
		  
		scanner_shader.close();
		return shaderSourceArray;
	}
	
	public int getProgram() {
		return idProgram;
	}



}
