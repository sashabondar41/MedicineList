package com.example.pharmacy;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowSaves extends ListActivity {
    private static final String urlGetMedicine = "http://n90926b9.beget.tech/getMedicine.php";
    private static final String TAG_ID = "id";
    private static final String TAG_SAVES = "saves";
    private static final String TAG_MID = "mid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRODUCTS = "products";
    private final JSONParser jParser = new JSONParser();
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> medicineList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        new LoadAllSaves().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);
        medicineList = new ArrayList<>();
        ListView listView = getListView();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String uid = getIntent().getStringExtra(TAG_ID);
            String mid = ((TextView) view.findViewById(R.id.mid)).getText().toString();
            Intent intent = new Intent(getApplicationContext(), GetMedicine.class);
            intent.putExtra(TAG_MID, mid);
            intent.putExtra(TAG_ID, uid);
            intent.putExtra("empty", "no");
            startActivity(intent);
        });
    }

    class LoadAllSaves extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowSaves.this);
            pDialog.setMessage("Поиск закладок...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            String[] saves = getIntent().getStringExtra(TAG_SAVES).split(" ");
            HashMap<String, String> map = new HashMap<>();
            for (String save : saves){
                List<NameValuePair> par = new ArrayList<>();
                par.add(new BasicNameValuePair(TAG_ID, save));
                JSONObject json = jParser.makeHttpRequest(urlGetMedicine, "POST", par);
                Log.d("Закладка", json.toString());
                try {
                    JSONArray medicine = json.getJSONArray(TAG_PRODUCTS);
                    JSONObject drug = medicine.getJSONObject(0);
                    String name = drug.getString(TAG_NAME);
                    map.put(TAG_ID, save);
                    map.put(TAG_NAME, name);
                    medicineList.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(() -> {
                ListAdapter adapter = new SimpleAdapter(ShowSaves.this, medicineList, R.layout.list_item, new String[] {TAG_ID, TAG_NAME}, new int[] {R.id.mid, R.id.name});
                setListAdapter(adapter);
            });

        }

    }
}