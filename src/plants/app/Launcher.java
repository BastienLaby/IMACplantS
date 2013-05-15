package plants.app;

import plants.ui.JOGLWindow;
import plants.xml.JDOMCreate;

public class Launcher {

	public static void main(String[] args) {
		
		JDOMCreate.createTreeAt("randomTree.xml");
		@SuppressWarnings("unused")
		JOGLWindow window = new JOGLWindow("PlantS");
		
	}
	
}
