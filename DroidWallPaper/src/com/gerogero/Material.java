package com.gerogero;
import javax.microedition.khronos.opengles.GL10;


public class Material extends GLObject{

	public float[] ambient=new float[4];//����
	public float[] diffuse=new float[4];//�g�U��
	public float[] specular=new float[4];//���ʌ�
	public float[] shininess=new float[1];//���ʔ��ˊp
	
	@Override
	public void bind() {
		GL10 gl=BaseGL.gl;
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient,0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse,0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,specular,0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess,0);

	}

	@Override
	public void unbind() {
		
	}

	@Override
	public void dispose() {
		
	}

}
