package com.gerogero;
import java.util.ArrayList;
import java.util.HashMap;


public class Figure {

	public HashMap<String,Material> materials;
	public ArrayList<Mesh> meshs;
	
	public void draw(){
		for(Mesh mesh:meshs) mesh.draw();
	}
	
}
