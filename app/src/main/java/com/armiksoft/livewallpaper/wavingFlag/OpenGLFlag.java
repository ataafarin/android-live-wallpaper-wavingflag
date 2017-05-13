package com.armiksoft.livewallpaper.wavingFlag;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLFlag {

    private FloatBuffer fbCoords;

    private FloatBuffer fbTexture;

    private int texture;

    private float centerX, centerY, centerZ;

    private float width, height;

    private int points = 0;

    private float[] coords;

    private int skip = 0;

    public OpenGLFlag(int texture, float centerX, float centerY, float centerZ, float width, float height) {
        this.texture = texture;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
        ByteBuffer bb1 = ByteBuffer.allocateDirect(Constants.FLAG_RESOLUTION * 8 * Float.SIZE);
        bb1.order(ByteOrder.nativeOrder());
        fbCoords = bb1.asFloatBuffer();

        ByteBuffer bb2 = ByteBuffer.allocateDirect(Constants.FLAG_RESOLUTION * 8 * Float.SIZE);
        bb2.order(ByteOrder.nativeOrder());
        fbTexture = bb2.asFloatBuffer();

        coords = new float[Constants.FLAG_RESOLUTION * 12];
        int i = 0;

        for (int x = 0; x < Constants.FLAG_RESOLUTION; x++) {

            float z1 = centerZ + (float) (Math.sin(x * 2 * Math.PI / (Constants.FLAG_RESOLUTION - 1)) / Constants.FLAG_WAVE_LENGTH);
            float z2 = centerZ + (float) (Math.sin((x + 1) * 2 * Math.PI / (Constants.FLAG_RESOLUTION - 1)) / Constants.FLAG_WAVE_LENGTH);

            coords[i] = centerX - width / 2 + x * width / Constants.FLAG_RESOLUTION;
            i++;
            coords[i] = centerY - height / 2;
            i++;
            coords[i] = z1;
            i++;
            coords[i] = centerX - width / 2 + x * width / Constants.FLAG_RESOLUTION;
            i++;
            coords[i] = centerY - height / 2 + height;
            i++;
            coords[i] = z1;
            i++;
            coords[i] = centerX - width / 2 + (x + 1) * width / Constants.FLAG_RESOLUTION;
            i++;
            coords[i] = centerY - height / 2;
            i++;
            coords[i] = z2;
            i++;
            coords[i] = centerX - width / 2 + (x + 1) * width / Constants.FLAG_RESOLUTION;
            i++;
            coords[i] = centerY - height / 2 + height;
            i++;
            coords[i] = z2;
            i++;

            float dx1 = (float) x / Constants.FLAG_RESOLUTION;
            float dx2 = (float) (x + 1) / Constants.FLAG_RESOLUTION;

            fbTexture.put(dx1);
            fbTexture.put(1.0f);

            fbTexture.put(dx1);
            fbTexture.put(0);

            fbTexture.put(dx2);
            fbTexture.put(1.0f);

            fbTexture.put(dx2);
            fbTexture.put(0);

            /*
            fbTexture.put(dx1);
            fbTexture.put(1.0f);

            fbTexture.put(dx1);
            fbTexture.put(0);

            fbTexture.put(dx2);
            fbTexture.put(1.0f);

            fbTexture.put(dx2);
            fbTexture.put(0);
            */

            points += 4;
        }

        fbCoords.put(coords);

        fbCoords.position(0);
        fbTexture.position(0);
    }

    public void draw(GL10 gl, boolean paused) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, texture);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fbCoords);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, fbTexture);
        GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, points);

        if (!paused && skip >= Constants.FLAG_SKIP_FRAMES) {
            float f1, f2, f3, f4;
            for (int i = 1; i < coords.length / 12; i++) {
                f1 = coords[i * 12 + 2];
                f2 = coords[i * 12 + 5];
                f3 = coords[i * 12 + 8];
                f4 = coords[i * 12 + 11];
                int j = i - 1;
                coords[j * 12 + 2] = f1;
                coords[j * 12 + 5] = f2;
                coords[j * 12 + 8] = f3;
                coords[j * 12 + 11] = f4;
            }
            coords[coords.length - 1] = coords[11];
            coords[coords.length - 4] = coords[8];
            coords[coords.length - 7] = coords[5];
            coords[coords.length - 10] = coords[2];

            fbCoords.clear();
            fbCoords.put(coords);
            fbCoords.position(0);

            skip = 0;
        } else {
            skip++;
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

}
