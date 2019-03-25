package thesis.auc.eyeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private Button mjpegView;
    private Button mjpegDetection;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mjpegView = findViewById(R.id.mjpeg_view);
        mjpegDetection = findViewById(R.id.mjpeg_detection);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        mjpegView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMjpeg(v);
            }
        });

        mjpegDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMjpegDetect(v);
            }
        });

    }



    public void showMjpeg(View view) {
        Intent intent = new Intent( this, MjpegActivity.class );
        startActivity(intent);
    }


    public void showMjpegDetect(View view) {
        Intent intent = new Intent(this, MjpegDetectActivity.class);
        startActivity(intent);
    }
}
