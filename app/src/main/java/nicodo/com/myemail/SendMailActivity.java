package nicodo.com.myemail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class SendMailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button send = (Button) this.findViewById(R.id.button1);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");

                String fromEmail = ((TextView) findViewById(R.id.editText1))
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
                        .getText().toString();
                new SendMailTask(SendMailActivity.this).execute(fromEmail,
                        fromPassword, toEmailList, emailSubject, emailBody);
            }
        });
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

        setContentView(R.layout.activity_main);


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

