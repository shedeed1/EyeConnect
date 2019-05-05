package thesis.auc.eyeconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.HttpGet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.cache.HttpCacheContext;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import thesis.auc.eyeconnect.Twitter.Twitter;
import thesis.auc.eyeconnect.Twitter.User;
import thesis.auc.eyeconnect.Twitter.UserConverter;

public class LoadFriendsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SHEDEED";
    private ProgressBar progressBar;
    private TextView loadingText;
    private CardView cardView;
    private Button proceedBtn;

    private TextView username;
    private TextView noOfFriends;
    private TextView noOfRecFriends;

    private ArrayList<User> listOfFriends;
    private ArrayList<String> imageURLs;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_friends);

        listOfFriends = new ArrayList<>();
        imageURLs = new ArrayList<>();

        proceedBtn = findViewById(R.id.proceedBtn);
        cardView = findViewById(R.id.cardView);
        loadingText = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar);

        username = findViewById(R.id.usernameValue);
        noOfFriends = findViewById(R.id.noFriendsValue);
        noOfRecFriends = findViewById(R.id.recFriendsValue);

        sharedPreferences = getSharedPreferences("SHARED_PREFERENCES",0);

        downloadUsers("-1");

        proceedBtn.setOnClickListener(view -> {
           Intent intent = new Intent(this,MjpegDetectActivity.class);
           startActivity(intent);
        });
    }

    private void displayData()
    {
        proceedBtn.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        loadingText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        username.setText(sharedPreferences.getString("USERNAME",""));
        noOfFriends.setText(String.valueOf(listOfFriends.size()));
    }

    public static void imageDownload(String url, String name){
        Log.i("MOHAMED",url);
        Picasso.get()
                .load(url)
                .into(getTarget(name));
    }

    private static Target getTarget(final String name){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Constants.getDLibImageDirectoryPath() + "/" + name);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public void downloadUsers(String cursor) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new getFriends(this).execute("eyeconnect9",cursor);
        } else {
            Log.v(LOG_TAG, "No network connection available.");
        }
    }

    public class getFriends extends AsyncTask<String, Void, String> {
        final String CONSUMER_KEY = "YCW9ZCWlwmER0Urm8soAEUUzA";
        final String CONSUMER_SECRET = "QmwjnQDb9o2SAalJAJhjyAxhzNNuaQ7WJgmTtwn99fiRCbMk5Q";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        private String TwitterStreamURL;
        private final Activity activity;

        public getFriends(final Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(final String... strings) {
            TwitterStreamURL = "https://api.twitter.com/1.1/friends/list.json?cursor=" + strings[1] + "&count=5&skip_status=true&include_user_entities=true&screen_name=";
            Log.i("YOOH",TwitterStreamURL);
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
                Twitter data = UserConverter.fromJsonString(result);
                User[] users = data.getUsers();
                listOfFriends.addAll(Arrays.asList(users));
                if (data.getNextCursor() != 0)
                {
                    downloadUsers(data.getNextCursorStr());
                }
                else
                {
                    for (int i =0;i<listOfFriends.size();i++)
                    {
                        imageURLs.add(listOfFriends.get(i).getProfileImageURL().replace("_normal",""));
                    }

                    for (int i =0;i<imageURLs.size();i++)
                    {
                        imageDownload(imageURLs.get(i),listOfFriends.get(i).getScreenName() + ".jpg");
                        if (i==imageURLs.size()-1)
                        {
                            new initRecAsync().execute();
                        }
                    }
                }
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

    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

    private class initRecAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.d("SHEDEED", "initRecAsync onPreExecute called");
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // create dlib_rec_example directory in sd card and copy model files
            File folder = new File(Constants.getDLibDirectoryPath());
            boolean success = false;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                File image_folder = new File(Constants.getDLibImageDirectoryPath());
                image_folder.mkdirs();
                if (!new File(Constants.getFaceShapeModelPath()).exists()) {
                    FileUtils.copyFileFromRawToOthers(LoadFriendsActivity.this, R.raw.shape_predictor_5_face_landmarks, Constants.getFaceShapeModelPath());
                }
                if (!new File(Constants.getFaceDescriptorModelPath()).exists()) {
                    FileUtils.copyFileFromRawToOthers(LoadFriendsActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
                }
            } else {
                //TODO: This statement should probably be removed, but I'm too afraid to do it at this point.
            }
            FaceRecManager.mFaceRec = new FaceRec(Constants.getDLibDirectoryPath());
            FaceRecManager.mFaceRec.train();
            return null;
        }

        protected void onPostExecute(Void result) {
            displayData();
        }
    }
}
