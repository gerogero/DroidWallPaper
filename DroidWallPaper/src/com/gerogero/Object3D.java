package com.gerogero;
import javax.microedition.khronos.opengles.GL10;


public class Object3D {

	public Figure figure;
	public float positionX=0;
	public float positionY=0;
	public float positionZ=0;
	public float rotateY=0;
	

	public void draw(){
		GL10 gl=BaseGL.gl;
		gl.glPushMatrix();
		gl.glTranslatef(positionX, positionY, positionZ);
		gl.glRotatef(rotateY,0, 1, 0);
		figure.draw();
		gl.glPopMatrix();
	}
}
