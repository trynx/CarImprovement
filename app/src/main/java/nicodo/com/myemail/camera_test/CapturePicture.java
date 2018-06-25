package nicodo.com.myemail.camera_test;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nicodo.com.myemail.R;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CapturePicture extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_CODE = 114;//request code for Camera and External Storage permission
    private static final int CAMERA_REQUEST_CODE = 133;//request code for capture image

    private Uri fileUri = null;//Uri to capture image
    private String getImageUrl = "";
    private ImageView imageView;

    // EXTRA
    File photoFile;
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";

    private Context viewContext;
    private Activity viewActivity;

    public CapturePicture(Context viewContext, Activity viewActivity) {
        this.viewContext = viewContext;
        this.viewActivity= viewActivity;
    }

    public CapturePicture() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_camera);
        imageView = (ImageView) findViewById(R.id.image_view);
    }


    /*  Check both permissions  */
    private boolean checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        for (String permission : getAllPermissions()) {
            int result = checkPermission(viewContext, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }
        //If both permissions are granted
        if (permissions.size() == 0)
            allPermissionGranted();
        else
            //if any one of them are not granted then request permission
            requestPermission(permissions.toArray(new String[permissions.size()]));
        return true;
    }

    /*   on both permission granted  */
    private void allPermissionGranted() {
        //Initiate capture image method
        if (isDeviceSupportCamera())
            captureImage();
    }

    /*  Request permissions  */
    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(viewActivity, permissions, PERMISSION_REQUEST_CODE);
    }

    /*  Permissions string array  */
    private String[] getAllPermissions() {
        return new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
    }

    /*  Method to check permissions  */
    public static int checkPermission(final Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    /*  on Capture image button click check permissions  */
    public void captureImage(View view) {
        checkPermission();
    }

    public void shootImage() {
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    int counter = 0;//counter to traverse all permissions
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //show alert dialog if any of the permission denied
                            showMessageOKCancel(getString(R.string.permission_message),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //If user click on OK button check permission again.
                                                checkPermission();
                                            }
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(viewContext, R.string.capture_deny_message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            return;
                        } else {
                            counter++;
                            //If counter is equal to permissions length mean all permission granted.
                            if (counter == permissions.length)
                                allPermissionGranted();
                        }
                    }

                }


                break;
        }
    }


    /*  Alert dialog on permission denied    */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(viewContext)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    // Checking camera supportability
    private boolean isDeviceSupportCamera() {
        if (viewContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
            return true;
        else {
            Toast.makeText(viewContext, getResources().getString(R.string.camera_not_supported), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /*  Capture Image Method  */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
        photoFile = getPhotoFileUri(photoFileName);

        fileUri = FileProvider.getUriForFile(viewContext,viewContext.getPackageName() + ".provider", photoFile);
       /* fileUri = CameraUtils.getOutputMediaFileUri(this);//get fileUri from CameraUtils
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent*/
        viewActivity.startActivityForResult(intent, CAMERA_REQUEST_CODE);//start activity for result with CAMERA_REQUEST_CODE
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                try {
                    //When image is captured successfully
                    if (resultCode == RESULT_OK) {

                        //Check if device SDK is greater than 22 then we get the actual image path via below method
                        if (Build.VERSION.SDK_INT > 22)
//                            getImageUrl = ImagePath_MarshMallow.getPath(CapturePicture.this, fileUri);
                            getImageUrl = getFilePathFromURI(viewContext, fileUri);
                        else
                            //else we will get path directly
                            getImageUrl = fileUri.getPath();


                        //After image capture show captured image over image view
//                        showCapturedImage();
                    } else
                        Toast.makeText(viewContext, R.string.cancel_message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }


    /*  Show Captured over ImageView  */
    private void showCapturedImage() {
        if (!getImageUrl.equals("") && getImageUrl != null){

//            imageView.setImageBitmap(CameraUtils.convertImagePathToBitmap(getImageUrl, false));

        }
        else{
            Toast.makeText(viewContext, R.string.capture_image_failed, Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    // EXTRA
    public File getPhotoFileUri(String fileName){

        File mediaStorageDir = new File(viewContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File rootDataDir = context.getFilesDir();
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            try {
                IOUtils.copy(inputStream, outputStream);

            } catch (IOException e){
                Log.e("IOError",e.getMessage());
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getGetImageUrl() {
        return getImageUrl;
    }
}