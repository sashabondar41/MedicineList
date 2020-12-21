package com.example.pharmacy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkScreen extends AppCompatActivity {
    private static final String urlGetSaves = "http://n90926b9.beget.tech/getSaves.php";
    private static final String TAG_ID = "id";
    private static final String TAG_SAVES = "saves";
    private static final String TAG_USER = "user";
    private final JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private String id, stringSaves;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_screen);
        Button getSaves = findViewById(R.id.getSaves);
        Button searchMedicine = findViewById(R.id.allMedicine);
        Button newMedicine = findViewById(R.id.newMedicine);
        EditText search = findViewById(R.id.search);
        id = getIntent().getStringExtra(TAG_ID);
        if (!id.equals("1") && !id.equals("noId")){
            getSaves.setVisibility(View.VISIBLE);
            newMedicine.setVisibility(View.GONE);
        }else if(id.equals("1")){
            getSaves.setVisibility(View.GONE);
            newMedicine.setVisibility(View.VISIBLE);
        }else{
            getSaves.setVisibility(View.GONE);
            newMedicine.setVisibility(View.GONE);
        }
        searchMedicine.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchMedicine.class);
            String searchQuery = search.getText().toString();
            intent.putExtra("searchQuery", searchQuery);
            intent.putExtra(TAG_ID, id);
            startActivity(intent);
        });
        getSaves.setOnClickListener(view -> {
            new GetSaves().execute();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), ShowSaves.class);
            if (stringSaves.equals("null")){
                showAlertBox(1);
            }
            else{
                intent.putExtra(TAG_ID, id);
                intent.putExtra(TAG_SAVES, stringSaves);
                startActivity(intent);
            }
        });
        newMedicine.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GetMedicine.class);
            intent.putExtra("empty", "yes");
            startActivity(intent);
        });
    }

    public void showAlertBox(int i){
        if (i==1) {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(WorkScreen.this);
            quitDialog.setTitle("Закладки отсутствуют!");
            quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
            });
            quitDialog.show();
        }
    }

    class GetSaves extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WorkScreen.this);
            pDialog.setMessage("Загрузка закладок пользователя...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] params) {
            try {
                List<NameValuePair> par = new ArrayList<>();
                par.add(new BasicNameValuePair(TAG_ID, id));
                JSONObject json = jsonParser.makeHttpRequest(urlGetSaves, "POST", par);
                Log.d("Список закладок", json.toString());
                JSONArray userObj = json.getJSONArray(TAG_USER);
                JSONObject user = userObj.getJSONObject(0);
                stringSaves = user.getString(TAG_SAVES);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}