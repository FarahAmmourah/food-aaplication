package com.farah.foodapp.sujoud;

import android.content.Context;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SentimentInterpreter {

    private static Interpreter interpreter = null;

    private static MappedByteBuffer loadModel(Context context) throws IOException {
        FileInputStream fis = new FileInputStream(context.getAssets().openFd("sentiment_model.tflite").getFileDescriptor());
        FileChannel channel = fis.getChannel();
        long startOffset = context.getAssets().openFd("sentiment_model.tflite").getStartOffset();
        long declaredLength = context.getAssets().openFd("sentiment_model.tflite").getDeclaredLength();
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private static void init(Context context) {
        if (interpreter == null) {
            try {
                interpreter = new Interpreter(loadModel(context));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static float[] predict(float[] inputVector, Context context) {

        init(context);

        float[][] output = new float[1][3];

        interpreter.run(inputVector, output);

        return output[0];
    }
}
