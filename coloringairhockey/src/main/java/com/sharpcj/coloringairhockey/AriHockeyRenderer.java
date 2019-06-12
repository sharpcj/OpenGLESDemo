package com.sharpcj.coloringairhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.sharpcj.coloringairhockey.util.LogUtils;
import com.sharpcj.coloringairhockey.util.ShaderHelper;
import com.sharpcj.coloringairhockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AriHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;

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

//    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";

//    private int uColorLocation;
    private int aColorLocation;
    private int aPositionLocation;

    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;



    public AriHockeyRenderer(Context context) {
        this.mContext = context;

        float[] tableVerticesWithTriangles = {

                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

                -0.5f, 0.0f, 1.0f, 0f, 0f,
                0.5f, 0.0f, 1.0f, 0f, 0f,

                0.0f, -0.25f, 0f, 0f, 1f,

                0.0f, 0.25f, 1f, 0f, 0f
        };

        // 将 Android 虚拟机中的浮点型数组（用来表示坐标）复制到本地内存
        mVertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT) // 申请内存大小
                .order(ByteOrder.nativeOrder()) // 本地内存排序
                .asFloatBuffer();

        mVertexData.put(tableVerticesWithTriangles);
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

//        uColorLocation = glGetUniformLocation(mProgram, U_COLOR);
        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);

        mVertexData.position(0); // 移动指针到 0，表示从开头开始读取
        // 告诉 OpenGL， 可以在缓冲区中找到 a_Position 对应的数据
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aPositionLocation);

        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL_COLOR_BUFFER_BIT);
//        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
