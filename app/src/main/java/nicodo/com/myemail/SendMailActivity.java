package nicodo.com.myemail;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nicodo.com.myemail.camera_util.IOUtils;
import nicodo.com.myemail.googlesheet.GetDataTask;
import nicodo.com.myemail.googlesheet.PostData;
import nicodo.com.myemail.recyclerview.ImprovementRVAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SendMailActivity extends Activity implements AdapterView.OnItemSelectedListener {

    // RecyclerView Declaration
    private RecyclerView rv;
    private ImprovementRVAdapter adapter;
    private List<Improvement> improvementList;

    private Spinner spinner;
    private static final String I10 = "i10", OTHER = "אחר";
    private String selectedModel = "";

    private EditText[] unFocusText;
    private EditText carChassis;
    private EditText carNumber;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String emailBody = "";
    public final String APP_TAG = "MyCustomApp";

    public static final int PERMISSION_REQUEST_CODE = 114;//request code for Camera and External Storage permission
    private static final int CAMERA_REQUEST_CODE = 133;//request code for capture image

    private Uri fileUri = null;//Uri to capture image
    private List<String> getImageUrlList = new ArrayList<>();

    private ShimmerFrameLayout shimmerViewContainer;
    private ProgressBar progressSpinner;
    // EXTRA
    File photoFile;
    public String photoFileName = "photo";
    int i = 0; // Flag to increment the photoFileName


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail_const);

        progressSpinner = (ProgressBar) findViewById(R.id.spinner_progress);
        // Spinner Apply
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> list = new ArrayList<>();

        // Custom Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);

        GetDataTask getDataTask = new GetDataTask(getApplicationContext());
        getDataTask.setSpinnerList(list, adapter);
        getDataTask.execute("spinner");

        spinner.setAdapter(adapter);

        carChassis = (EditText) findViewById(R.id.car_chassis);
        carNumber = (EditText) findViewById(R.id.car_number);
        unFocusText = new EditText[]{carChassis, carNumber};

        // RecyclerView Initialization
        rv = (RecyclerView) findViewById(R.id.rv_improvements);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        improvementList = new ArrayList<>();

        initializeAdapter();
    }

    private void initializeAdapter() {
        // Lazy Loading on the adapter, only update the recyclerview adapter if it already exists
        if (adapter == null) {
            adapter = new ImprovementRVAdapter(improvementList, unFocusText);
            rv.setAdapter(adapter);
        } else {
            rv.setAdapter(adapter);
        }
    }

    public void takePicture(View view) {
        checkPermission();
    }

    private String calcItemPrice() {
        StringBuilder txt = new StringBuilder();
        int total = 0;
        for (Improvement impro : improvementList) {
            if (impro.isSelected()) {
                txt.append(impro.getName()).append(" : ").append(impro.getPrice()).append("\n");
                total += impro.getPrice();
            }
        }

        txt.append("סה\"כ : ").append(total);
        return txt.toString();
    }

    private String calcTotalPrice(){
        int total = 0;
        for (Improvement impro : improvementList) {
            if (impro.isSelected()) {
                total += impro.getPrice();
            }
        }
        return Integer.toString(total);
    }

    private String[] getSelectedImprovements(){
        List<String> list = new ArrayList<>();

        for(Improvement impro: improvementList){
            if(impro.isSelected()){
                list.add(impro.getName());
            }
        }

        return list.toArray(new String[0]);
    }

    private String[] getSelectedImprovementsAndPrice(){
        List<String> list = new ArrayList<>();

        for(Improvement impro: improvementList){
            if(impro.isSelected()){
                list.add(impro.getName());
                list.add(Integer.toString(impro.getPrice()));
            }
        }

        return list.toArray(new String[0]);
    }
    // OnItemSelected Methods of Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        String item = adapterView.getItemAtPosition(position).toString();
        selectedModel = item;

        GetDataTask getDataTask = new GetDataTask(getApplicationContext());
        getDataTask.setImprovementListAndAdapter(improvementList, adapter, progressSpinner);
        getDataTask.execute("list", item);

        initializeAdapter();

    }

    @Override // Not in use
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /*  Check both permissions  */
    private boolean checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        for (String permission : getAllPermissions()) {
            int result = checkPermission(this, permission);
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
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /*  Permissions string array  */
    private String[] getAllPermissions() {
        return new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
    }

    /*  Method to check permissions  */
    public int checkPermission(final Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    /*  on Capture image button click check permissions  */
    public void captureImage(View view) {
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
                                            Toast.makeText(SendMailActivity.this, R.string.capture_deny_message, Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    // Checking camera supportability
    private boolean isDeviceSupportCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
            return true;
        else {
            Toast.makeText(this, getResources().getString(R.string.camera_not_supported), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /*  Capture Image Method  */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
        photoFile = getPhotoFileUri(photoFileName + Integer.toString(i++) + ".jpg");

        fileUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent
        startActivityForResult(intent, CAMERA_REQUEST_CODE);//start activity for result with CAMERA_REQUEST_CODE
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
//                        if (Build.VERSION.SDK_INT > 22)
                            //                            getImageUrlList = ImagePath_MarshMallow.getPath(CapturePicture.this, fileUri);
                            getImageUrlList.add(getFilePathFromURI(this, fileUri));

//                        else
//                            //else we will get path directly
//                            getImageUrlList = fileUri.getPath();

                    } else
                        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
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
    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public String getFilePathFromURI(Context context, Uri contentUri) {
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

    public String getFileName(Uri uri) {
        if (uri == null) return null;

        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            try {
                IOUtils.copy(inputStream, outputStream);

            } catch (IOException e) {
                Log.e("IOError", e.getMessage());
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Button Action of "Send"
    public void sendMail(View view) {
        Log.i("SendMailActivity", "Send Button Clicked.");

        String currentTime = Util.getCurrentTime();
        // TODO - Get Data from Google Sheet(Admin - Mine)
        String carChassis = ((TextView) findViewById(R.id.car_chassis)).getText().toString().toUpperCase();
        String carNumber = ((TextView) findViewById(R.id.car_number)).getText().toString().toUpperCase();
        String fromEmail = getString(R.string.admin_email);
        String fromPassword = getString(R.string.admin_password);
        List<String> toEmailList = new ArrayList<>();
        toEmailList.add(getString(R.string.recipient_email));
        String emailSubject = currentTime + " - " + selectedModel + " - " + carChassis + " - " + carNumber;
        emailBody = calcItemPrice();

        // Need a picture to send the Mail
        if(getImageUrlList.isEmpty()) {
            Toast.makeText(this,"בקשה הוסף תמונה",Toast.LENGTH_LONG).show(); // TODO - Maybe a dialog to get the attention of the user?

        } else if(carNumber.isEmpty()){
            Toast.makeText(this,"בקשה רשום מספר שלדה",Toast.LENGTH_LONG).show(); // TODO - Maybe a dialog to get the attention of the user?

        } else {
            String sheet = Util.getHebrewMonth(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date())); // Sheet depend on the month
            // Add data to main table of action
            new PostData(SendMailActivity.this).execute(Util.SHEET_ID, sheet, PostData.OLD, carNumber, carChassis,
                    selectedModel, currentTime, calcTotalPrice(), getSelectedImprovements());
            // Add/Update data to the total sum of improvements
            new PostData(SendMailActivity.this).execute(Util.SHEET_ID_COSTS, sheet, PostData.INSERT, calcTotalPrice(), getSelectedImprovementsAndPrice());

            new SendMailTask(SendMailActivity.this).execute(fromEmail,
                    fromPassword, toEmailList, emailSubject, emailBody, getImageUrlList);

            i = 0; // Reset the flag
        }
    }
}

