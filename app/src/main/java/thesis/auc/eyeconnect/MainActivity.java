package thesis.auc.eyeconnect;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.face.FaceRecognizer;
import org.opencv.core.MatOfInt;
import org.opencv.core.*;
import org.opencv.face.Face;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    private CameraBridgeViewBase openCvCameraView;
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    private Net net;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void initializeOpenCVDependencies() {

        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

        // And we are ready to go
        openCvCameraView.enableView();
    }

    // Upload file to storage and return a path.
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i("Failed to upload a file","nonos");
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        openCvCameraView = new JavaCameraView(this, -1);
        setContentView(openCvCameraView);
        openCvCameraView.setCvCameraViewListener(this);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }





    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        String proto = getPath("deploy.prototxt.txt",this);
        String weights = getPath("res10_300x300_ssd_iter_140000.caffemodel",this);
        net = Dnn.readNetFromCaffe(proto,weights);
        Log.i("Nonos","Network Loaded !!");
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {

        int height = inputFrame.height();
        int width = 400;
        Imgproc.resize(inputFrame,inputFrame,new Size(width,height));
        Mat blobImage = new Mat();
        Imgproc.resize(inputFrame,blobImage,new Size(300,300));
        Mat blob = Dnn.blobFromImage(inputFrame,1.0, new Size(300, 300),new Scalar(104.0,177.0,123.0));
        net.setInput(blob);
        Mat detections = net.forward();
        int cols = inputFrame.cols();
        int rows = inputFrame.rows();
        detections = detections.reshape(1,(int)detections.total() / 7);
        for(int i=0; i < detections.rows();++i){
            double confidence = detections.get(i,2)[0];
            int classId = (int)detections.get(i,1)[0];
            int left = (int) (detections.get(i,3)[0] * cols);
            int top = (int) (detections.get(i,4)[0] * rows);
            int right = (int) (detections.get(i,5)[0] * cols);
            int bottom = (int) (detections.get(i,6)[0] * rows);
            Imgproc.rectangle(inputFrame,new Point(left, top), new Point(right, bottom),
                    new Scalar(0, 255, 0));
            String label =  String.valueOf(confidence);
            int[] baseLine = new int[1];
            Size labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
            // Write class name and confidence.
            Imgproc.putText(inputFrame, label, new Point(left, top),
                    Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));

        }
        return inputFrame;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
//    }
}