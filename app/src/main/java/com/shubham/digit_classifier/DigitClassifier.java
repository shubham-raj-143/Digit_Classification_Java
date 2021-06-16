package com.shubham.digit_classifier;

import android.graphics.Bitmap;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Interpreter.Options;


public final class DigitClassifier {
    private Interpreter interpreter;
    public boolean isInitialized = false;
    private ExecutorService executorService;
    private int inputImageHeight;
    private int inputImageWidth;
    private int modelInputSize;
    private Context context;
    private static final int OUTPUT_CLASS = 10;
    private static final int PIXEL_VALUE = 1;
    private static final int FLOAT_TYPE = 4;
//    @NotNull
//    public static final DigitClassifier.Companion Companion = new DigitClassifier.Companion((DefaultConstructorMarker)null);
//
//    public final boolean isInitialized() {
//        return this.isInitialized;
//    }


    public final Task<Void> initialize() {
        TaskCompletionSource<Void> task = new TaskCompletionSource<Void>();
        executorService.execute((Runnable)(new Runnable() {
            public final void run() {
                try {
                    initializeInterpreter();
                    task.setResult(null);
                } catch (Exception e) {
                    task.setException(e);
                }

            }
        }));

        return task.getTask();
    }

    private final void initializeInterpreter() throws IOException {
        AssetManager assetManager = context.getAssets();
        ByteBuffer model = loadModelFile(assetManager, "minst.tflite");
        Options options = new Options();
        options.setUseNNAPI(true);
        Interpreter interpreter = new Interpreter(model, options);
        int[] inputShape = interpreter.getInputTensor(0).shape();

        inputImageHeight = inputShape[2];
        inputImageWidth = inputShape[1];
        modelInputSize = FLOAT_TYPE * inputImageWidth *inputImageHeight * PIXEL_VALUE;
        this.interpreter = interpreter;
        isInitialized = true;
    }

    @NotNull
    public Task<String> classifyAsyn(@NotNull final Bitmap bitmap) {
        TaskCompletionSource<String> task = new TaskCompletionSource<String>();
        executorService.execute((Runnable)(new Runnable() {
            public final void run() {
                String result = classify(bitmap);
                task.setResult(result);
            }
        }));

        return task.getTask();
    }

    private ByteBuffer convertBitmaptoByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelInputSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[inputImageHeight * inputImageWidth];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for(int pixelvalue: pixels) {
            int r = pixelvalue >> 16 & 255;
            int b = pixelvalue & 255;
            int g = pixelvalue >> 8 & 255;
            float normalizedpixel = (float)(r + g + b) / 3.0F / 255.0F;
            byteBuffer.putFloat(normalizedpixel);
        }

        return byteBuffer;
    }

    private String classify(Bitmap bitmap) {

//        if (isInitialized) {
//
//            System.out.println("TF lite is not initialized");
//
//        }
//            Bitmap resizeImage = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true);
//
//            ByteBuffer byteBuffer = convertBitmaptoByteBuffer(resizeImage);
//
//
//            float[][] f = new float[1][];
//            float[][] output = (float[][])f;
//
//
//            interpreter.run(byteBuffer, output);
//
//
//            float[] result = output[0];


//            int maxindex = result.;
//            String var29 = "prediction result: %d\nConfidence:%2f";
//            Object[] var30 = new Object[]{maxindex, result[maxindex]};
//            $i$f$maxByOrNull = false;
//            String var36 = String.format(var29, Arrays.copyOf(var30, var30.length));
//            Intrinsics.checkNotNullExpressionValue(var36, "java.lang.String.format(this, *args)");
//
            String resultString = "var36";
            return resultString;

    }

    public final void close() {
        this.executorService.execute((Runnable)(new Runnable() {
            public final void run() {

                interpreter.close();

            }
        }));
    }

    private final ByteBuffer loadModelFile(AssetManager assetManager, String filename) throws IOException {
       AssetFileDescriptor fileDescriptor = assetManager.openFd(filename);
       FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
       FileChannel fileChannel = inputStream.getChannel();
       Long startoffset = fileDescriptor.getStartOffset();
       Long declaredLength = fileDescriptor.getDeclaredLength();
       return fileChannel.map(MapMode.READ_ONLY, startoffset, declaredLength);

    }


}
