package com.gerogero;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLU;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;


public class OpenGlLive extends WallpaperService{

	@Override
	public Engine onCreateEngine() {
		BaseGL.context=this;
		return new GLEngine();
	}
	
	public class GLEngine extends Engine{

		private GLEngineSurface glface=null;
		
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			glface.windowHeight=height;
			glface.windowWidth=width;
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			glface=new GLEngineSurface(getSurfaceHolder());
			glface.start();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			if(glface!=null){
				glface.onDestroy();
				glface=null;
			}
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if(visible){
				glface.onResume();
			}else{
				glface.onPause();
			}
		}
	}

	public class GLEngineSurface extends Thread{

		private int windowWidth=-1;
		private int windowHeight=-1;
		
		private boolean destroy=false;
		private boolean pause=false;
		private SurfaceHolder holder;
		private EGL10 egl;
		private EGLContext eglContext=null;
		private EGLDisplay eglDisplay=null;
		private EGLSurface eglSurface=null;
		private EGLConfig eglConfig=null;
		protected GL10 gl10=null;
		
		GLEngineRender render;
		
		public GLEngineSurface(SurfaceHolder holder){
			this.holder=holder;
		}
		
		private void initialize(){
			
			egl=(EGL10) EGLContext.getEGL();
			eglDisplay=egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
			
			int[] version={-1,-1};

			if(!egl.eglInitialize(eglDisplay, version)){
				return;
			}
			
			EGLConfig[] configs=new EGLConfig[1];
			int[] num=new int[1];
			
			int[] spec={
					EGL10.EGL_DEPTH_SIZE,1,
					EGL10.EGL_NONE
			};
			
			if(!egl.eglChooseConfig(eglDisplay, spec, configs, 1, num)){
				return;
			}
			eglConfig=configs[0];
			eglContext=egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
			
			egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
			eglSurface=egl.eglCreateWindowSurface(eglDisplay, eglConfig, holder, null);
			
			if(eglSurface==EGL10.EGL_NO_SURFACE){
				return;
			}
			
			gl10=(GL10) eglContext.getGL();
			BaseGL.gl=(GL11) gl10;
			
			if(!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)){
				return;
			}
			
		}
		
		private void dispose(){
			if(eglSurface!=null){
				egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				egl.eglDestroySurface(eglDisplay, eglSurface);
				eglSurface=null;
			}
			if(eglContext!=null){
				egl.eglDestroyContext(eglDisplay, eglContext);
				eglContext=null;
			}
			if(eglDisplay!=null){
				egl.eglTerminate(eglDisplay);
				eglDisplay=null;
			}
		}
		
		@Override
		public void run() {
			initialize();
			render=new GLEngineRender();
			render.onSurfaceCreate(gl10);
			render.onSurfaceChanged(gl10, windowWidth, windowHeight);
			while(!destroy){
				if(!pause){
					render.onDrawFrame(gl10);
					egl.eglSwapBuffers(eglDisplay, eglSurface);
				}else{
					try{
						Thread.sleep(3000);
					}catch(Exception e){
					}
				}
			}
			dispose();
		}
		
		public void onPause(){
			pause=true;
		}
		
		public void onResume(){
			pause=false;
		}
		
		public void onDestroy(){
			synchronized(this){
				destroy=true;
			}
			try{
				join();
			}catch(InterruptedException ex){
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public class GLEngineRender {
		private float aspect;
		private float angle;
		
		private Object3D model=new Object3D();
		
		public void onSurfaceCreate(GL10 gl10){
			gl10.glEnable(GL10.GL_DEPTH_TEST);
			gl10.glEnable(GL10.GL_LIGHTING);
			gl10.glEnable(GL10.GL_LIGHT0);
			gl10.glEnable(GL10.GL_CULL_FACE);
			gl10.glEnable(GL10.GL_BACK);
			
			gl10.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[]{0.2f,0.2f,0.2f,1.0f},0);
			gl10.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[]{0.7f,0.7f,0.7f,1.0f},0);
			gl10.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, new float[]{0.9f,0.9f,0.9f,1.0f},0);

			try{
				model.figure=ObjectLoader.load("droid.obj");
			}catch(Exception e){
			}
		}
		
		public void onSurfaceChanged(GL10 gl10,int windowWidth,int windowHeight){
			gl10.glViewport(0, 0, windowWidth, windowHeight);
			aspect=(float)windowWidth/windowHeight;
		}
		
		public void onDrawFrame(GL10 gl10){
			gl10.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
			gl10.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

			gl10.glMatrixMode(GL10.GL_PROJECTION);
			gl10.glLoadIdentity();
			GLU.gluPerspective(gl10, 45.0f, aspect, 0.01f, 100.0f);
		
			gl10.glMatrixMode(GL10.GL_MODELVIEW);
			gl10.glLoadIdentity();
			gl10.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[]{5.0f,5.0f,5.0f,1.0f},0);
			
			GLU.gluLookAt(gl10,
					0.0f, 0.8f, -10.0f,
					0.0f, 0.8f, 0.0f, 
					0.0f, 1.0f, 0.0f);
			
			angle=angle+1;
			if(angle>360) angle=0;
			
			model.rotateY=angle;
			model.draw();
		}
	}
}
