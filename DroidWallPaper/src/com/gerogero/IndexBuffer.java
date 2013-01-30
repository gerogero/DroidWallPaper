package com.gerogero;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


public class IndexBuffer extends GLObject{

	private int indexBufferId;
	private int indexBufferSize;
	
	
	public IndexBuffer(ArrayList<short[]> indexs){
		short[] indexArray=new short[3*indexs.size()];
		for(int i=0;i<indexs.size();i++){
			short[] index=indexs.get(i);
			System.arraycopy(index, 0, indexArray, 3*i, 3);
		}
		
		GL11 gl=BaseGL.gl;
		int[] bufferIds=new int[1];
		gl.glGenBuffers(1, bufferIds,0);
		indexBufferId=bufferIds[0];
		
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		ShortBuffer sb=ByteBuffer.allocateDirect(indexArray.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
		sb.put(indexArray);
		sb.position(0);
		
		indexBufferSize=indexArray.length;
		gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, sb.capacity()*2, sb,GL11.GL_STATIC_DRAW);
	}
	
	@Override
	public void bind() {
		GL11 gl=BaseGL.gl;
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		gl.glDrawElements(GL10.GL_TRIANGLES, indexBufferSize, GL10.GL_UNSIGNED_SHORT, 0);
	}

	@Override
	public void unbind() {
		GL11 gl=BaseGL.gl;
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}

	@Override
	public void dispose() {
		if(indexBufferId!=0){
			BaseGL.gl.glDeleteBuffers(1,new int[] {indexBufferId},0	);
			indexBufferId=0;
		}
	}
}
