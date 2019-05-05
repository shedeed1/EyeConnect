package thesis.auc.eyeconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.gson.Gson;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.loopj.android.http.HttpGet;
import com.tzutalin.dlib.VisionDetRet;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.cache.HttpCacheContext;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import thesis.auc.eyeconnect.Twitter.Tweet;
import thesis.auc.eyeconnect.Twitter.TweetConverter;
import thesis.auc.eyeconnect.mjpeg.MjpegInputStream;

public class MjpegDetectActivity extends AppCompatActivity {

    public static String executeRemoteCommand(
            String username,
            String password,
            String hostname,
            String tweet) throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, 22);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        Log.i("Shedeed6555",tweet);

        // SSH Channel
        ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        //channelssh.setCommand("python /home/pi/OLED/python-examples/Python_ST7735/examples/image.py ");
        channelssh.setCommand("cd /home/pi/OLED/python-examples && python tweet.py '"+tweet+"'");
        //channelssh.setCommand("cd /home/pi/OLED/python-examples/Python_ST7735/examples/ && python image.py");
        channelssh.connect();

        try{Thread.sleep(1000);}catch(Exception ee){}
        channelssh.disconnect();
        session.disconnect();

        return baos.toString();
    }

    private static void sendTweetToScreen(final String tweet)
    {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    Log.i("Shedeed4",executeRemoteCommand("pi", "123", "192.168.43.128", tweet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    intializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public void intializeOpenCVDependencies()
    {
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
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

        mAppContext = MjpegDetectActivity.this;

        playMovie();
    }

    final String TAG = MjpegDetectActivity.class.getSimpleName();

    Thread mStreamThread;
    MjpegStreamOperator mStreamOperator;

    SurfaceView mResultView;

    private FirebaseVisionFaceDetectorOptions options;
    private FirebaseVisionFaceDetector detector;

    Context mAppContext;

    class SetupMjpegStreamTask extends AsyncTask<String, Void, MjpegInputStream> {
        final String TAG = SetupMjpegStreamTask.class.getSimpleName();

        @Override
        protected MjpegInputStream doInBackground(String... urls) {
            Log.d(TAG, "doInBackground url:" + urls[0]);
            return MjpegInputStream.read(urls[0]);
        }

        @Override protected void onPostExecute(MjpegInputStream result) {
            Log.d(TAG, "onPostExecute");


            mStreamOperator = new MjpegStreamOperator(mAppContext, result, 480, 640, mResultView.getHolder(), mResultView.getHolder());
            mStreamThread = new Thread(mStreamOperator);
            mStreamThread.start();
        }
    }

    public class MjpegStreamOperator implements Runnable {

        private final String TAG = MjpegStreamOperator.class.getSimpleName();
        private final int UPDATE_INTERVAL_DEFAULT = 1000;

        private SurfaceHolder mResult;
        private Rect mResultRect;

        private MjpegInputStream mStream;

        private Bitmap mBitmapCurrent;

        private boolean mDone = false;

        public MjpegStreamOperator(Context context, MjpegInputStream stream, int streamWidth, int streamHeght, SurfaceHolder preview, SurfaceHolder result) {
            mResult = preview;
            mStream = stream;

            mResultRect = result.getSurfaceFrame();

            mBitmapCurrent = Bitmap.createBitmap(streamWidth, streamHeght, Bitmap.Config.ARGB_8888);
        }

        @Override
        public synchronized void run() {
            Log.d(TAG, "Thread start");
            while (!mDone) {
                Log.d(TAG, "Read from stream");

                int ret = 0;
                try {
                    ret = mStream.readMjpegFrame(mBitmapCurrent);
                } catch (IOException e) {
                    Log.e(TAG, "readMjpegFrame error", e);
                }

                if (ret == -1) {
                    Log.e(TAG, "readMjpegFrame error");
                    return;
                }

                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(mBitmapCurrent);

                detector.detectInImage(firebaseVisionImage)
                        .addOnSuccessListener(firebaseVisionFaces -> {
                            if (firebaseVisionFaces.size() == 1)
                            {
                                if (firebaseVisionFaces.get(0).getSmilingProbability() >= 0.98) {
                                    List<VisionDetRet> results = FaceRecManager.mFaceRec.recognize(mBitmapCurrent);
                                    for (VisionDetRet n : results) {
                                        Log.d("SHEDEEEDREC", n.getLabel());
                                        downloadTweets(n.getLabel().replace(".jpg",""));
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.i("Shedeed","Failed"));

                drawBitmapToCanvas(mBitmapCurrent, mResult);
            }
        }

        public void shutdown() {
            Log.d(TAG, "Shutdown");
            mDone = true;
        }

        private void drawBitmapToCanvas(Bitmap source, SurfaceHolder target) {
            Rect sourceRect = new Rect(0, 0, source.getWidth(), source.getHeight());
            Rect targetRect = target.getSurfaceFrame();
            Canvas canvas = target.lockCanvas();
            canvas.drawBitmap(source, sourceRect, targetRect, null);
            target.unlockCanvasAndPost(canvas);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpeg_detect);

        options = new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .enableTracking()
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        mAppContext = this;

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mResultView = (SurfaceView)findViewById(R.id.mjpegdetect_result);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onStop");
        super.onPause();

        if (mStreamOperator != null) {
            mStreamOperator.shutdown();
        }
        if (mStreamThread != null) {
            try {
                mStreamThread.join();
            } catch (InterruptedException e) {
                // 何もしない
            }
        }
    }

    public void playMovie() {

        if (mStreamOperator != null) {
            mStreamOperator.shutdown();
            try {
                mStreamThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "thread join failed", e);
            }
        }

        new SetupMjpegStreamTask().execute("http://192.168.43.128:8080/stream/video.mjpeg");
    }

    public void stopMovie(View view) {
        mStreamOperator.shutdown();
    }

    public void downloadTweets(String username) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new getTweets(this).execute(username);
        } else {
            Log.v("SHEDEED", "No network connection available.");
        }
    }

    public class getTweets extends AsyncTask<String, Void, String> {
        final String CONSUMER_KEY = "YCW9ZCWlwmER0Urm8soAEUUzA";
        final String CONSUMER_SECRET = "QmwjnQDb9o2SAalJAJhjyAxhzNNuaQ7WJgmTtwn99fiRCbMk5Q";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        private String TwitterStreamURL;
        private final Activity activity;

        public getTweets(final Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(final String... strings) {
            TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + strings[0] + "&count=5";
            String result = null;
            if (strings.length > 0) {
                result = this.getTwitterStream(strings[0]);
            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(final String result) {

            try {
                Tweet[] data = TweetConverter.fromJsonString(result);

                Log.i("SHEDEEEDREC",data[0].getText());
                sendTweetToScreen(data[0].getText());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // convert a JSON authentication object into an Authenticated object
        private Authenticated jsonToAuthenticated(final String rawAuthorization) {
            Authenticated auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, Authenticated.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return auth;
        }

        private String getResponseBody(final HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        private String getTwitterStream(final String screenName) {

            HttpCacheContext context = HttpCacheContext.create();
            String results = null;

            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = this.getResponseBody(httpPost);
                final Authenticated auth = this.jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response

                    results = this.getResponseBody(httpGet);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
        }
    }

    public class Authenticated {
        String token_type;
        String access_token;
    }

}