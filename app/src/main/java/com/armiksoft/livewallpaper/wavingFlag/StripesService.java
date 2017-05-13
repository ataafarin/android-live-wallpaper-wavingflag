/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.armiksoft.livewallpaper.wavingFlag;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Wallpaper entry point.
 */
public final class StripesService extends WallpaperService {



	@Override
	public final Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	/**
	 * Private wallpaper engine implementation.
	 */
	private final class WallpaperEngine extends Engine  {


		private StripesSurfaceView mGLSurfaceView;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {

			super.onCreate(surfaceHolder);
			mGLSurfaceView = new StripesSurfaceView(getApplicationContext());

		}

		@Override
		public final void onDestroy() {
			super.onDestroy();
			mGLSurfaceView.onDestroy();
			mGLSurfaceView = null;
		}

		@Override
		public final void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				mGLSurfaceView.onResume();
				mGLSurfaceView.requestRender();
			} else {
				mGLSurfaceView.onPause();
			}
		}

		private final class StripesSurfaceView extends GLSurfaceView implements
				GLSurfaceView.Renderer {


			private Context context;

			private int textures[];

			private OpenGLFlag flag;

			private boolean paused = false;

			public StripesSurfaceView(Context context) {
				super(context);
				this.context = context;
				setRenderer(this);
				setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

			}

			@Override
			public final SurfaceHolder getHolder() {
				return WallpaperEngine.this.getSurfaceHolder();
			}

			public final void onDestroy() {
				super.onDetachedFromWindow();
			}

			@Override
			public final void onDrawFrame(GL10 gl) {
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

				gl.glPushMatrix();

				// rotate
				gl.glRotatef(Constants.FLAG_ROTATION_X, 1.0f, 0.0f, 0.0f);
				gl.glRotatef(Constants.FLAG_ROTATION_Y, 0.0f, 1.0f, 0.0f);
				gl.glRotatef(Constants.FLAG_ROTATION_Z, 0.0f, 0.0f, 1.0f);

				// draw
				flag.draw(gl, paused);

				gl.glPopMatrix();

			}

			@Override
			public final void onSurfaceChanged(GL10 gl, int width,
					int height) {
				float ratio = (float) width / height;

				// flag
				flag = new OpenGLFlag(textures[0], 0, 0.1f, 0, ratio * 5, ratio*5);

				gl.glShadeModel(GL10.GL_SMOOTH);
				GLES20.glClearDepthf(1.0f);
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
				GLES20.glDepthFunc(GLES20.GL_LEQUAL);
				GLES20.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GLES20.GL_NICEST);
				GLES20.glEnable(GLES20.GL_BLEND);
				GLES20.glEnable(GL10.GL_POINT_SMOOTH);
				GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); // https://www.opengl.org/sdk/docs/man2/xhtml/glBlendFunc.xml

				GLES20.glViewport(0, 0, width, height);
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // https://www.opengl.org/sdk/docs/man2/xhtml/glFrustum.xml

				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				GLU.gluLookAt(gl, 0, 0, 3.5f, 0, 0, 0, 0, 1.0f, 0); // https://www.opengl.org/sdk/docs/man2/xhtml/gluLookAt.xml

			}

			private Bitmap getBitmapFromAssets(Context context, String fileName, int width, int height) {
				AssetManager asset = context.getAssets();
				InputStream is;
				try {
					is = asset.open(fileName);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bit=BitmapFactory.decodeStream(is, null, options);
				return bit;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public final void onSurfaceCreated(GL10 gl, EGLConfig config) {
				// bind texture
				textures = new int[1];
//
				GLES20.glEnable(GLES20.GL_TEXTURE_2D);
				GLES20.glGenTextures(textures.length, textures, 0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

//				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app);
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inScaled = false;
				Bitmap bitmap = getBitmapFromAssets(getApplicationContext(),"sepidroodFlag3.png",0,0);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				Log.d("stripe", "onSurfaceCreated bitmap : "+bitmap);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);


				bitmap.recycle();




//				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);


			}

		}

	}

}