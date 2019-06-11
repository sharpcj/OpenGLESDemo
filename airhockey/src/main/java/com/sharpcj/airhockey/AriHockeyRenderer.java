package com.sharpcj.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.sharpcj.airhockey.util.LogUtils;
import com.sharpcj.airhockey.util.ShaderHelper;
import com.sharpcj.airhockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AriHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;

    /**
     * 一个浮点型占 4 个字节
     */
    private static final int BYTES_PER_FLOAT = 4;

    /**
     * 在本地内存创建一个缓冲区，用来将 Android 虚拟机中的浮点型数组（用来表示坐标）复制到本地内存
     */
    private final FloatBuffer mVertexData;

    private final Context mContext;

    private int mProgram;

    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private int uColorLocation;
    private int aPositionLocation;

    public AriHockeyRenderer(Context context) {
        this.mContext = context;

        float[] tableVerticesWithTriangles = {
                0f, 0f,
                9f, 14f,
                0f, 14f,  // Triangle 1

                0f, 0f,
                9f, 0f,
                9f, 14f,   // Triangle 2

                0f, 7f,
                9f, 7f,    // Line 1

                4.5f, 2f,    // Point 1

                4.5f, 12f    // Point 2
        };

        // 将 Android 虚拟机中的浮点型数组（用来表示坐标）复制到本地内存
        mVertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT) // 申请内存大小
                .order(ByteOrder.nativeOrder()) // 本地内存排序
                .asFloatBuffer();

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f ,0.0f ,0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LogUtils.ON) {
            ShaderHelper.validateProgram(mProgram);
        }
        glUseProgram(mProgram);  // 告诉 OpenGL 在绘制任何东西到屏幕上的时候要使用这里定义的程序

        uColorLocation = glGetUniformLocation(mProgram, U_COLOR);

        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        mVertexData.position(0); // 移动指针到 0，表示从开头开始读取

        // 告诉 OpenGL， 可以在缓冲区中找到 a_Position 对应的数据
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, mVertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
