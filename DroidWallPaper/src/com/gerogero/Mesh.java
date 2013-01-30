package com.gerogero;

public class Mesh {

	public VertexBuffer vertexBuffer;
	public IndexBuffer indexBuffer;
	public Material material;
	
	public void draw(){
		material.bind();
		vertexBuffer.bind();
		indexBuffer.bind();
		indexBuffer.unbind();
		vertexBuffer.unbind();
		material.unbind();
	}
	
	
}
