package com.gerogero;

public abstract class GLObject {

	protected void finalize() throws Throwable{
		super.finalize();
		dispose();
	}
	
	public abstract void bind();
	
	public abstract void unbind();
	
	public abstract void dispose();
	
	
}
