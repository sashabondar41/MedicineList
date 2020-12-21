package com.example.pharmacy;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchMedicine extends ListActivity {
    private static final String urlGetAllMedicine = "http://n90926b9.beget.tech/getAllMedicine.php";
    private static final String TAG_ID = "id";
    private static final String TAG_MID = "mid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRODUCT = "products";
    private final JSONParser jParser = new JSONParser();
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> medicineList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);
        medicineList = new ArrayList<>();
        new LoadAllProducts().execute();
        ListView listView = getListView();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String uid = getIntent().getStringExtra(TAG_ID);
            String mid = ((TextView) view.findViewById(R.id.mid)).getText().toString();
            Intent intent = new Intent(getApplicationContext(), GetMedicine.class);
            intent.putExtra(TAG_MID, mid);
            intent.putExtra(TAG_ID, uid);
            intent.putExtra("empty", "no");
            startActivityForResult(intent, 100);
        });

    }

    public void showAlertBox(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(SearchMedicine.this);
        quitDialog.setTitle("Не найдено совпадений!");
        quitDialog.setPositiveButton("Понял!", (dialog, which) -> finish());
        quitDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent i = getIntent();
            finish();
            startActivity(i);
        }

    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchMedicine.this);
            pDialog.setMessage("Поиск лекарств...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            JSONObject json = jParser.makeHttpRequest(urlGetAllMedicine, "POST", new ArrayList<>());
            Log.d("All Products: ", json.toString());
            try {
                JSONArray medicine = json.getJSONArray(TAG_PRODUCT);
                String searchQuery = getIntent().getStringExtra("searchQuery");
                for (int i = 0; i < medicine.length(); i++) {
                    JSONObject drug = medicine.getJSONObject(i);
                    String mid = drug.getString(TAG_ID);
                    String name = drug.getString(TAG_NAME);
                    if (name.toLowerCase().contains(searchQuery.toLowerCase())) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(TAG_ID, mid);
                        map.put(TAG_NAME, name);
                        medicineList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (medicineList.isEmpty()){
                showAlertBox();
            }else{
                runOnUiThread(() -> {
                    ListAdapter adapter = new SimpleAdapter(SearchMedicine.this, medicineList, R.layout.list_item, new String[] {TAG_ID, TAG_NAME}, new int[] {R.id.mid, R.id.name});
                    setListAdapter(adapter);
                });
            }
        }

    }

}