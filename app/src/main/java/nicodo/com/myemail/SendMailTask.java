package nicodo.com.myemail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import javax.mail.MessagingException;


public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private  Activity  sendMailActivity;

    SendMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("מתכונן...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("מעבד נתונים...");
            GMail androidEmail = new GMail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
            publishProgress("מכין את הנתונים...");
            androidEmail.createEmailMessage();
            androidEmail.addAttachment((List) args[5]);
            publishProgress("שולח נתונים...");
            try { // Check for when the email could be a problem
                androidEmail.sendEmail();
            }catch (MessagingException e){
                // Problem with e-mail -> tell the user so
                Log.e("SendMailTask", e.getMessage());
                return false;
            }
            publishProgress("נתונים נשלחו בהצלחה.");
            Log.i("SendMailTask", "Mail Sent.");
            return true;
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
        if (Boolean.parseBoolean(result.toString())) sendMailActivity.finish();
        else Toast.makeText(sendMailActivity, "יש בעיה באימייל", Toast.LENGTH_LONG).show();

    }

}