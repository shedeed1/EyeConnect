package thesis.auc.eyeconnect;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import thesis.auc.eyeconnect.mjpeg.MjpegInputStream;
import thesis.auc.eyeconnect.mjpeg.MjpegView;


public class MjpegActivity extends AppCompatActivity {

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    playMovie();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private static final String TAG = MjpegActivity.class.getSimpleName();

    class SetupMjpegStreamTask extends AsyncTask<String, Void, MjpegInputStream> {
        final String TAG = SetupMjpegStreamTask.class.getSimpleName();

        @Override
        protected MjpegInputStream doInBackground(String... urls) {
            Log.d(TAG, "doInBackground url:" + urls[0]);
            return MjpegInputStream.read(urls[0]);
        }

        @Override protected void onPostExecute(MjpegInputStream result) {
            Log.d(TAG, "onPostExecute");
            MjpegView view = findViewById(R.id.mjpeg_preview);
            view.setSource(result);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpeg);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }
    }

    public void playMovie() {

        new SetupMjpegStreamTask().execute("http://192.168.137.236:8080/stream/video.mjpeg");
    }
}
