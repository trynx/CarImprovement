package nicodo.com.myemail.googlesheet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class PostData extends AsyncTask<Object, Void, String> {

    public static String INSERT = "insert";
    public static String READ = "read";
    public static String UPDATE = "update";
    public static String DELETE = "delete";
    public static String READ_ALL = "readAll";
    public static String OLD = "old";

    private Context context;

    public PostData(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(Object... arg0) {

        try {
            //Change your web app deployed URL or u can use this for attributes (name, country)
            URL url = new URL("https://script.google.com/macros/s/AKfycbwKWuSfI15wND3ANFaizWXjxwj5lXNmPUGJ4zByyFCExsO_LR7F/exec?");

            JSONObject postDataParams = new JSONObject();

            String action = arg0[2].toString();
            if(action.equals(INSERT)){

                postDataParams.put("id", arg0[0].toString());
                postDataParams.put("sheet", arg0[1].toString());
                postDataParams.put("cost", Integer.parseInt(arg0[3].toString()));
                postDataParams.put("action", action); // Which type of action to do

                JSONArray improvements = new JSONArray(arg0[4]);
                postDataParams.put("improv", improvements);

            } else if(action.equals(OLD)){
                postDataParams.put("id", arg0[0].toString());
                postDataParams.put("sheet", arg0[1].toString());
                postDataParams.put("carNumber", arg0[3].toString());
                postDataParams.put("carChassis", arg0[4].toString());
                postDataParams.put("model", arg0[5].toString());
                postDataParams.put("date", arg0[6].toString());
                postDataParams.put("cost", Integer.parseInt(arg0[7].toString()));

                postDataParams.put("action", action); // Which type of action to do

                JSONArray improvements = new JSONArray(arg0[8]);
                postDataParams.put("improv", improvements);
            }


            Log.e("params", postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {

//        Toast.makeText(context, result,
//                Toast.LENGTH_LONG).show();



    }


    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
