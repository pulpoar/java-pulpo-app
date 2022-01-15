package com.pulpoar.pulpoardemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    ActivityResultLauncher<Intent> activityResultLauncher;
    String currentPhotoPath;
    String emre = " ++++++++++++++++++++++++++++++++ ";
    String erdem = " ------------------------------- ";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // https://developer.android.com/training/system-ui/status#41
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Hide action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        WebView mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl("https://devphotomakeup.pulpoar.com");

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setSupportZoom(false);

        activityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult activityResult) {
                                int newResultCode = activityResult.getResultCode();
                                Intent data = activityResult.getData();
                                Log.i("oh yea", "newResultCode: " + newResultCode + " Activity.RESULT_OK: " + Activity.RESULT_OK);
                                Log.i("data", ": " + data);
                                Log.i("dataSTR", ": " + activityResult.getData());
                                Uri[] results = null;

                                // Check that the response is a good one
                                if (newResultCode == Activity.RESULT_OK) {
                                    if (data == null) {
                                        Log.i("emre", "" + emre + " data: "  + data);
                                        // If there is not data, then we may have taken a photo
                                        if (mCameraPhotoPath != null) {
                                            Log.i("emre", "" + emre + " mCameraPhotoPath: "  + mCameraPhotoPath);
                                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                                        }
                                    } else {
                                        String dataString = data.getDataString();
                                        Log.i("98", "" + emre + " datastring: "  + dataString);
                                        if (dataString != null) {
                                            results = new Uri[]{Uri.parse(dataString)};
                                        }
                                    }

                                }
                                mFilePathCallback.onReceiveValue(results);
                                mFilePathCallback = null;
                            }
                        }
                );

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            // javascript commands should be executed after page loaded
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript: origin = 'http://127.0.0.1:8000';\n" +
                        "initProducts('038f1611-201e-4614-ab25-c3b9fff38905', origin);\n" +
                        "set_active_products(JSON.stringify({'Lipstick':'Pulpoar Lipstick 01', 'makeupMode':0, 'makeupType':['Lipstick']}))");

            }
        });

        mWebView.setWebChromeClient(new ChromeClient());

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:applyProduct('Lipstick','Pulpoar Lipstick 02')");
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",      /* suffix */
                storageDir          /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("message", " reqCode: " + requestCode + " resultCode: " + resultCode + " Activity.RESULT_OK: " + Activity.RESULT_OK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    Log.i("emre", "" + emre + " data: "  + data);
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        Log.i("emre", "" + emre + " mCameraPhotoPath: "  + data);
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    Log.i("emre", "" + emre + " datastring: "  + dataString);
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;

                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        return;
    }
*/

    public class ChromeClient extends WebChromeClient {

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    String destination = Environment.getExternalStorageDirectory().getPath() + "/image.jpg";
                    photoFile = createImageFile();
                    Log.i("photofile", "" + emre + "ok assign photofile " + Environment.getExternalStorageDirectory().getPath() + " new: " + currentPhotoPath + "destination: " + destination);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(destination)));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.i("199", "null değil " + erdem + photoFile.getAbsolutePath());
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    Uri newURI = FileProvider.getUriForFile(view.getContext(),  BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    Uri photoURI = Uri.fromFile(photoFile);

                    Log.i("199", " " + erdem + " newUrı: " + newURI + " photoURI " + photoURI);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newURI);
                } else {
                    takePictureIntent = null;
                    Log.i("205", "eeh null");
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            setResult(Activity.RESULT_OK, chooserIntent);
            //startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            activityResultLauncher.launch(chooserIntent);

            return true;

        }

    }

}

