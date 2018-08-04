package nicodo.com.myemail.googlesheet;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import nicodo.com.myemail.Improvement;
import nicodo.com.myemail.googlesheet.parser.JSONParser;
import nicodo.com.myemail.googlesheet.util.Keys;
import nicodo.com.myemail.recyclerview.ImprovementRVAdapter;


/**
 * Creating Get Data Task for Getting Data From Web
 */
public class GetDataTask extends AsyncTask<String, Void, String> {

    ProgressDialog dialog;
    private int jIndex;

    private Context context;

    private List<Improvement> improvementList;
    private ImprovementRVAdapter adapter;
    private ProgressBar progessSpinner;


    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;
    private boolean isSpinner = false;


    public GetDataTask(Context context) {
        this.context = context;
    }

    // If only want to update the list
    public void setImprovementListAndAdapter(List<Improvement> list, ImprovementRVAdapter adapter, ProgressBar progessSpinner) {
        this.improvementList = list;
        this.adapter = adapter;
        this.progessSpinner = progessSpinner;
    }

    public void setSpinnerList(List<String> list, ArrayAdapter<String> adapter) {
        this.spinnerAdapter = adapter;
        this.spinnerList = list;
        isSpinner = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /**
         * Progress Dialog for User Interaction
         */

        if(!isSpinner) progessSpinner.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    protected String doInBackground(String... params) {

        switch (params[0].toLowerCase()) {
            case "list":
                setImprovementList(params[1]);
                break;
            case "spinner":
                setSpinner();
        }
        return params[0].toLowerCase();
    }

    private void setSpinner() {

        //Getting JSON Object from Web Using okHttp
        JSONObject jsonObject = JSONParser.getDataFromWeb();

        try {
            if (jsonObject != null && jsonObject.length() > 0) {

                // Getting Array of the sheet which is needed to work with
                JSONArray array = jsonObject.names();

                for (int i = 0; i < array.length(); i++) {
                    spinnerList.add(array.get(i).toString());
                }
            }

        } catch (JSONException je) {
            Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
        }
    }

    private void setImprovementList(String sheet) {
        improvementList.clear();
        jIndex = 0;

        //Getting JSON Object from Web Using okHttp
        JSONObject jsonObject = JSONParser.getDataFromWeb(sheet);

        try {
            if (jsonObject != null && jsonObject.length() > 0) {

                // Getting Array of the sheet which is needed to work with
                JSONArray array = jsonObject.getJSONArray(sheet);

                // Check Length of Array...
                int lenArray = array.length();
                if (lenArray > 0) {
                    for (; jIndex < lenArray; jIndex++) {
                        JSONObject innerObject = array.getJSONObject(jIndex);

                        String name = innerObject.optString(Keys.KEY_NAME);
                        int price = innerObject.optInt(Keys.KEY_PRICE);
                        Improvement modelData = new Improvement(name, price);

                        // Only if there really is a data it will add to the list
                        if (!name.isEmpty()) {
                            improvementList.add(modelData);
                        }
                    }

                }
            }
        } catch (JSONException je) {
            Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Depend on what it was fetched, update accordingly
        switch (result) {
            case "list":
                if (improvementList.size() > 0) adapter.notifyDataSetChanged();
                progessSpinner.setVisibility(View.GONE);
                break;
            case "spinner":
                if (spinnerList.size() > 0) spinnerAdapter.notifyDataSetChanged();

        }
    }
}