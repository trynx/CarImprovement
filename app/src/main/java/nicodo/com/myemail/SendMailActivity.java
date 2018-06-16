package nicodo.com.myemail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nicodo.com.myemail.RecycleView.ImprovementRVAdapter;

public class SendMailActivity extends Activity implements AdapterView.OnItemSelectedListener{

    // RecyclerView Declaration
    private RecyclerView rv;
    private ImprovementRVAdapter adapter;
    private List<Improvement> improvementList;

    // Email Extra body
    private List<String> bodyList = new ArrayList<>();

    private Spinner spinner;
    private static final String KONA = "Kona", TOSON = "Toson";


    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String emailBody = "";
    public final String APP_TAG = "MyCustomApp";
    Uri photoURI;

    public String photoFileName = "photo.jpg";
    File photoFile;

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
        setContentView(R.layout.activity_send_mail);
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
                String carNumber = ((TextView) findViewById(R.id.car_num)).getText().toString();
                String fromEmail = "car.improvement.test@gmail.com";
                String fromPassword = "CI Testingout18";
                List<String> toEmailList = new ArrayList<>();
                toEmailList.add("car.improvement.test@gmail.com");
                String emailSubject = "Car Improvement " + carNumber.trim();
                emailBody = calcItemPrice();

                new SendMailTask(SendMailActivity.this).execute(fromEmail,
                        fromPassword, toEmailList, emailSubject, emailBody);
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
            adapter = new ImprovementRVAdapter(improvementList/*, this*/);
            rv.setAdapter(adapter);
        } else {
            rv.setAdapter(adapter);
        }
    }

    private Improvement addImprovementName(String name, int price){
        return new Improvement(name, price);
    }

   /* @Override
    public void passImprovementSelected(View view, List<Integer> positions) {
        for(int position: positions){

            String name = improvementList.get(position).name;
            if(bodyList.contains(name)){
                bodyList.remove(name);
            } else {
                bodyList.add(name);
            }
        }

        for(String name: bodyList){

            System.out.println("************** " + name + " **************");
        }

    }*/

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
    }

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

    public void takePicture(View view) {
                openCameraIntent();
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
}


/*

import android.os.Bundle;

import android.app.Activity;

import android.util.Log;

import android.view.Menu;

import android.view.View;

import android.widget.Button;

import android.widget.Toast;


public class MainActivity extends Activity {

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
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(MainActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
}
*/

