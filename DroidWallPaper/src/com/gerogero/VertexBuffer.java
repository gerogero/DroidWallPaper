package com.gerogero;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


public class VertexBuffer extends GLObject{

	private int vertexBufferId;
	

	public VertexBuffer(ArrayList<float[]> vertexs){
		float[] vertexArray=new float[8*vertexs.size()];
		
		for(int i=0;i<vertexs.size();i++){
			float[] vertex=vertexs.get(i);
			System.arraycopy(vertex, 0, vertexArray, 8*i, 8);
		}
		GL11 gl=BaseGL.gl;
		int count=vertexArray.length/8;
		int[] bufferIds=new int[1];
		BaseGL.gl.glGenBuffers(1, bufferIds,0);
		vertexBufferId=bufferIds[0];
		FloatBuffer fb=ByteBuffer.allocateDirect(8*4*count).order(ByteOrder.nativeOrder()).asFloatBuffer();
		fb.put(vertexArray);
		fb.position(0);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexBufferId);
		gl.glBufferData(GL11.GL_ARRAY_BUFFER, fb.capacity()*4, fb, GL11.GL_STATIC_DRAW);
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
	}
	
	
	@Override
	public void bind() {
		GL11 gl=BaseGL.gl;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexBufferId);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 8*4, 0);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 8*4, 3*4);
		gl.glNormalPointer(GL10.GL_FLOAT, 8*4, 5*4);
	}

	@Override
	public void unbind() {
		GL11 gl=BaseGL.gl;
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		
	}

	@Override
	public void dispose() {
		if(vertexBufferId!=0){
			BaseGL.gl.glDeleteBuffers(1, new int[]{vertexBufferId},0);
			vertexBufferId=0;
		}
		
	}

}
