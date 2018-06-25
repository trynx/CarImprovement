package nicodo.com.myemail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nicodo.com.myemail.camera_test.CapturePicture;
import nicodo.com.myemail.camera_test.IOUtils;
import nicodo.com.myemail.camera_test.Mail;
import nicodo.com.myemail.recyclerview.ImprovementRVAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SendMailActivity extends Activity implements AdapterView.OnItemSelectedListener{

    // RecyclerView Declaration
    private RecyclerView rv;
    private ImprovementRVAdapter adapter;
    private List<Improvement> improvementList;

    // Email Extra body
    private List<String> bodyList = new ArrayList<>();

    private Spinner spinner;
    private static final String KONA = "Kona", TOSON = "Toson";
    private String currSelectedItem = "";

    private EditText unFocusText;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String emailBody = "";
    public final String APP_TAG = "MyCustomApp";
//    Uri photoURI;
//
//    private String imgPath = "";
//
//    public String photoFileName = "photo.jpg";
//    File photoFile;

    // Camera capture test
    CapturePicture capPic = new CapturePicture(this,this);

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(this,this.getPackageName() + ".provider", photoFile);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
        /*    //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.getStackTrace();
            }
            if (photoFile != null) {*/
//                photoURI = FileProvider.getUriForFile(this,this.getPackageName() + ".provider", photoFile);

            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
//                        }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail_const);
        final Button send = (Button) this.findViewById(R.id.button1);
        // Spinner Test
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> list = new ArrayList<>();
        list.add(KONA);
        list.add(TOSON);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        unFocusText = (EditText) findViewById(R.id.car_num);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");

                // Original with views
               /* String fromEmail = ((TextView) findViewById(R.id.editText1))
                        .getText().toString();
                String fromPassword = ((TextView) findViewById(R.id.editText2))
                        .getText().toString();
                String toEmails = ((TextView) findViewById(R.id.editText3))
                        .getText().toString();
                List<String> toEmailList = Arrays.asList(toEmails
                        .split("\\s*,\\s*"));
                Log.i("SendMailActivity", "To List: " + toEmailList);
                String emailSubject = ((TextView) findViewById(R.id.editText4))
                        .getText().toString();
                String emailBody = ((TextView) findViewById(R.id.editText5))
                        .getText().toString();*/

                // Test Hard coded
                DateFormat dateF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String currentTime2 = dateF.format(Calendar.getInstance().getTime());

                String carNumber = ((TextView) findViewById(R.id.car_num)).getText().toString();
                String fromEmail = "car.improvement.test@gmail.com";
                String fromPassword = "car123123test";
                List<String> toEmailList = new ArrayList<>();
                toEmailList.add("car.improvement.test@gmail.com");
                String emailSubject = "Car Improvement - " + currentTime2 + " - " + currSelectedItem + " - " +   carNumber.trim();
                emailBody = calcItemPrice();



                new SendMailTask(SendMailActivity.this).execute(fromEmail,
                        fromPassword, toEmailList, emailSubject, emailBody, getImageUrl);
            }
        });



        // RecyclerView Initialization
        rv = (RecyclerView) findViewById(R.id.rv_improvements);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        improvementList = new ArrayList<>();


    }

    private void initializeAdapter(){
        // Lazy Loading on the adapter, only update the recyclerview adapter if it already exists
        if(adapter == null){
            adapter = new ImprovementRVAdapter(improvementList, unFocusText);
            rv.setAdapter(adapter);
        } else {
            rv.setAdapter(adapter);
        }
    }

    private Improvement addImprovementName(String name, int price){
        return new Improvement(name, price);
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
//            if (data != null && data.getExtras() != null) {
//                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Bitmap imageBitmap =  BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                System.out.println("************** IN Activity RESULT **************");

                //                Uri uriImg = (Uri) data.getExtras().get("data");
                Uri uriImg = getImageUri(getApplicationContext(), imageBitmap);

                System.out.println("**************" + uriImg.getPath() + "**************");


                //                Uri uri = getImageUri(getApplicationContext(), imageBitmap);
                //                Uri uri = FileProvider.getUriForFile(SendMailActivity.this, BuildConfig.APPLICATION_ID + ".provider",imageBitmap);;

                File imgFile = new File(getRealPathFromURI(uriImg));

                System.out.println("**************" + imgFile.getPath() + "**************");
                //                mImageView.setImageBitmap(imageBitmap); // Here get the path of the image
//            }
        }
    }*/

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

   /* public File getPhotoFileUri(String fileName){

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }*/

    public void takePicture(View view) {

        captureImage(view);
//        if(imgPath == null) imgPath = ""; // Better to make it empty in case it's null
//                openCameraIntent();
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private String calcItemPrice(){
        String txt = "";
        int total = 0;
        for(Improvement impro : improvementList){
            if(impro.isSelected()){
                txt += impro.getName() + ": " + impro.getPrice() + "<br />"; // Should use HTML <br/> to do a new line
                total += impro.getPrice();
            }
        }

        txt += "Total: " + total;
        return txt;
    }

    // OnItemSelected Methods
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
     String item = adapterView.getItemAtPosition(position).toString();

     currSelectedItem = item;
     setRecyclerViewItems(item);

        Toast.makeText(this,"Selected: " + item,Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // Should get from DB
    private void setRecyclerViewItems(String category){
        switch (category){
            case TOSON:
                setToson();
                break;
            case KONA:
                setKona();
        }
    }

    private void setToson(){
        // Dummy Data
        improvementList.clear();
        improvementList.add(addImprovementName("Radio", 200));
        improvementList.add(addImprovementName("Light", 33));
        improvementList.add(addImprovementName("Rear Sensors", 240));
        improvementList.add(addImprovementName("Front Sensors", 120));
        improvementList.add(addImprovementName("Read Camera", 45));
        improvementList.add(addImprovementName("Front Camera", 70));

        initializeAdapter();
    }
    private void setKona(){
        // Dummy Data
        improvementList.clear();
        improvementList.add(addImprovementName("Springs", 430));
        improvementList.add(addImprovementName("Coco", 30));
        improvementList.add(addImprovementName("Bumpers", 120));
        improvementList.add(addImprovementName("Nitro", 123));
        improvementList.add(addImprovementName("Roof", 400));
        improvementList.add(addImprovementName("Brakes", 78));

        initializeAdapter();
    }



        public static final int PERMISSION_REQUEST_CODE = 114;//request code for Camera and External Storage permission
        private static final int CAMERA_REQUEST_CODE = 133;//request code for capture image

        private Uri fileUri = null;//Uri to capture image
        private String getImageUrl = "";
        private ImageView imageView;

        // EXTRA
        File photoFile;
//        public final String APP_TAG = "MyCustomApp";
        public String photoFileName = "photo.jpg";



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
            photoFile = getPhotoFileUri(photoFileName);

            fileUri = FileProvider.getUriForFile(this,this.getPackageName() + ".provider", photoFile);
       /* fileUri = CameraUtils.getOutputMediaFileUri(this);//get fileUri from CameraUtils
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent*/
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent*/
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
                            if (Build.VERSION.SDK_INT > 22)
                                //                            getImageUrl = ImagePath_MarshMallow.getPath(CapturePicture.this, fileUri);
                                getImageUrl = getFilePathFromURI(this, fileUri);
                            else
                                //else we will get path directly
                                getImageUrl = fileUri.getPath();


                            //After image capture show captured image over image view
                            //                        showCapturedImage();
                        } else
                            Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, R.string.capture_image_failed, Toast.LENGTH_SHORT).show();

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

            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

            return file;
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

                } catch (IOException e){
                    Log.e("IOError",e.getMessage());
                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** TESST **/

        public static boolean sendEmail(String to, String from, String subject,
                                        String message,String[] attachements) throws Exception {
            Mail mail = new Mail();
            if (subject != null && subject.length() > 0) {
                mail.setSubject(subject);
            } else {
                mail.setSubject("Subject");
            }

            if (message != null && message.length() > 0) {
                mail.setBody(message);
            } else {
                mail.setBody("Message");
            }

            mail.setTo(new String[] {to});

            if (attachements != null) {
                for (String attachement : attachements) {
                    mail.addAttachment(attachement);
                }
            }
            return mail.send();
        }

}


/*

import android.os.Bundle;

import android.app.Activity;

import android.util.Log;

import android.view.Menu;

import android.view.View;

import android.widget.Button;

import android.widget.Toast;


public class CapturePicture extends Activity {

    Button send, img;
    private static final int GALLERY_REQUEST = 100;
    ImageView imageView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_mail);


        send = (Button) findViewById(R.id.button1);
        img = (Button) findViewById(R.id.img_btn);

        imageView = (ImageView) findViewById(R.id.img_view);

        final String fromEmail = ((TextView) findViewById(R.id.editText1))
                .getText().toString();
        final String fromPassword = ((TextView) findViewById(R.id.editText2))
                .getText().toString();
        final String toEmails = ((TextView) findViewById(R.id.editText3))
                .getText().toString();

        send.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                // TODO Auto-generated method stub

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

                new Thread(new Runnable() {

                    public void run() {

                        try {



                            GMailSender sender = new GMailSender(

                                    fromEmail,

                                    fromPassword);


                            sender.addAttachment(imageView);

                            sender.sendMail("Test mail", "This mail has been sent from android app along with attachment",

                                    fromEmail,

                                    toEmails);


                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();


                        }

                    }

                }).start();

            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CapturePicture.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(CapturePicture.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
}
*/

