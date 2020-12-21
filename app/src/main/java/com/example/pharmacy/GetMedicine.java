package com.example.pharmacy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GetMedicine extends AppCompatActivity {
    private static final String urlGetMedicine = "http://n90926b9.beget.tech/getMedicine.php";
    private static final String urlUpdateUser = "http://n90926b9.beget.tech/updateSaves.php";
    private static final String urlGetSaves = "http://n90926b9.beget.tech/getSaves.php";
    private static final String urlEditMedicine = "http://n90926b9.beget.tech/editMedicine.php";
    private static final String urlDeleteMedicine = "http://n90926b9.beget.tech/deleteMedicine.php";
    private static final String urlCreateMedicine = "http://n90926b9.beget.tech/newMedicine.php";
    private static final String urlGetAllMedicine = "http://n90926b9.beget.tech/getAllMedicine.php";
    private static final String TAG_ID = "id";
    private static final String TAG_SAVES = "saves";
    private static final String TAG_USER = "user";
    private static final String TAG_MID = "mid";
    private static final String TAG_NAME = "name";
    private static final String TAG_WAYTOUSE = "waytouse";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PRODUCT = "products";
    private final JSONParser jsonParser = new JSONParser();
    private final ArrayList<String> names = new ArrayList<>();
    private ProgressDialog pDialog;
    private TextView txtName, txtWay, txtDesc;
    private EditText editName, editWay, editDesc;
    private JSONObject medicine;
    private String mid, id, stringSaves, newSaves, desc, waytouse, name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_medicine);
        LinearLayout buttons = findViewById(R.id.buttons);
        txtName = findViewById(R.id.outputName);
        txtWay = findViewById(R.id.outputWaytouse);
        txtDesc = findViewById(R.id.outputDescription);
        editName = findViewById(R.id.editName);
        editWay = findViewById(R.id.editWaytouse);
        editDesc = findViewById(R.id.editDescription);
        Button saveIn = findViewById(R.id.saveIn);
        Button save = findViewById(R.id.save);
        Button delete = findViewById(R.id.delete);
        Button create = findViewById(R.id.create);
        txtWay.setMovementMethod(new ScrollingMovementMethod());
        txtDesc.setMovementMethod(new ScrollingMovementMethod());
        Intent i = getIntent();
        if (i.getStringExtra("empty").equals("no")) {
            mid = i.getStringExtra(TAG_MID);
            id = i.getStringExtra(TAG_ID);
            if (!id.equals("1") && !id.equals("noId")) {
                saveIn.setVisibility(View.VISIBLE);
                buttons.setVisibility(View.GONE);
                create.setVisibility(View.GONE);
                txtName.setVisibility(View.VISIBLE);
                txtWay.setVisibility(View.VISIBLE);
                txtDesc.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);
                editWay.setVisibility(View.GONE);
                editDesc.setVisibility(View.GONE);
            } else if (id.equals("1")) {
                saveIn.setVisibility(View.GONE);
                buttons.setVisibility(View.VISIBLE);
                create.setVisibility(View.GONE);
                txtName.setVisibility(View.GONE);
                txtWay.setVisibility(View.GONE);
                txtDesc.setVisibility(View.GONE);
                editName.setVisibility(View.VISIBLE);
                editWay.setVisibility(View.VISIBLE);
                editDesc.setVisibility(View.VISIBLE);
            } else {
                saveIn.setVisibility(View.GONE);
                buttons.setVisibility(View.GONE);
                create.setVisibility(View.GONE);
                txtName.setVisibility(View.VISIBLE);
                txtWay.setVisibility(View.VISIBLE);
                txtDesc.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);
                editWay.setVisibility(View.GONE);
                editDesc.setVisibility(View.GONE);
            }
            new GetProductDetails().execute();
        } else {
            saveIn.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
            create.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.GONE);
            txtWay.setVisibility(View.GONE);
            txtDesc.setVisibility(View.GONE);
            editName.setVisibility(View.VISIBLE);
            editWay.setVisibility(View.VISIBLE);
            editDesc.setVisibility(View.VISIBLE);
        }
        saveIn.setOnClickListener(view -> new GetSaves().execute());
        save.setOnClickListener(view -> {
            name = editName.getText().toString();
            desc = editDesc.getText().toString();
            waytouse = editWay.getText().toString();
            new Save().execute();
        });
        delete.setOnClickListener(view -> new Delete().execute());
        create.setOnClickListener(view -> {
            name = editName.getText().toString();
            desc = editDesc.getText().toString();
            waytouse = editWay.getText().toString();
            if (name.equals("") || desc.equals("") || waytouse.equals("")){
                showAlertBox(7);
            }
            else{
                new LoadAllProducts().execute();
                try {
                    pDialog = new ProgressDialog(GetMedicine.this);
                    pDialog.setMessage("Проверка");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                check();
            }
        });
    }

    public void showAlertBox(int i) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        switch (i) {
            case (1):
                quitDialog.setTitle("Закладка успешно создана!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {});
                break;
            case (2):
                quitDialog.setTitle("Такой препарат уже существует!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    editName.setText("");
                    editDesc.setText("");
                    editWay.setText("");
                });
                break;
            case (3):
                quitDialog.setTitle("Это уже у Вас в закладках!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {});
                break;
            case (4):
                quitDialog.setTitle("Изменения сохранены!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    Intent intent = getIntent();
                    setResult(100, intent);
                    finish();
                });
                break;
            case (5):
                quitDialog.setTitle("Препарат успешно удален!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    Intent intent = getIntent();
                    setResult(100, intent);
                    finish();
                });
                break;
            case (6):
                quitDialog.setTitle("Препарат успешно создан!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> finish());
                break;
            case (7):
                quitDialog.setTitle("Необходимо заполнить все поля!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {});
                break;
        }
        quitDialog.show();
    }

    private void check() {
        for (String n : names) {
            if (n.toLowerCase().equals(name.toLowerCase())) {
                showAlertBox(2);
                return;
            }
        }
        new Create().execute();
    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetMedicine.this);
            pDialog.setMessage("Загрузка информации о лекарстве...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] params) {
            try {
                List<NameValuePair> par = new ArrayList<>();
                par.add(new BasicNameValuePair(TAG_ID, mid));
                JSONObject json = jsonParser.makeHttpRequest(urlGetMedicine, "POST", par);
                Log.d("Детали о лекарстве", json.toString());
                JSONArray medObj = json.getJSONArray(TAG_PRODUCT);
                medicine = medObj.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(() -> {
                try {
                    if (id.equals("1")) {
                        editName.setText(medicine.getString(TAG_NAME));
                        editWay.setText(medicine.getString(TAG_WAYTOUSE));
                        editDesc.setText(medicine.getString(TAG_DESCRIPTION));
                    } else {
                        txtName.setText(medicine.getString(TAG_NAME));
                        txtWay.setText(medicine.getString(TAG_WAYTOUSE));
                        txtDesc.setText(medicine.getString(TAG_DESCRIPTION));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
            pDialog.dismiss();
        }
    }


    class GetSaves extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetMedicine.this);
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
            if (stringSaves.equals("null")) {
                stringSaves = "";
            }
            String[] saves = stringSaves.split(" ");
            for (String save : saves) {
                if (save.equals(mid)) {
                    showAlertBox(3);
                    return;
                }
            }
            newSaves = stringSaves + mid + " ";
            new UpdateSaves().execute();
        }
    }

    class UpdateSaves extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetMedicine.this);
            pDialog.setMessage("Создание закладки...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] params) {
            List<NameValuePair> par = new ArrayList<>();
            par.add(new BasicNameValuePair(TAG_ID, id));
            par.add(new BasicNameValuePair(TAG_SAVES, newSaves));
            JSONObject json = jsonParser.makeHttpRequest(urlUpdateUser, "POST", par);
            Log.d("Результат создания", json.toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            showAlertBox(1);
        }
    }

    class Save extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetMedicine.this);
            pDialog.setMessage("Внесение изменений...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] params) {
            List<NameValuePair> par = new ArrayList<>();
            try {
                par.add(new BasicNameValuePair(TAG_ID, mid));
                par.add(new BasicNameValuePair(TAG_NAME, URLEncoder.encode(name, "UTF-8")));
                par.add(new BasicNameValuePair(TAG_DESCRIPTION, URLEncoder.encode(desc, "UTF-8")));
                par.add(new BasicNameValuePair(TAG_WAYTOUSE, URLEncoder.encode(waytouse, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject json = jsonParser.makeHttpRequest(urlEditMedicine, "POST", par);
            Log.d("Результат изменения", json.toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            showAlertBox(4);
            pDialog.dismiss();
        }
    }

    class Delete extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetMedicine.this);
            pDialog.setMessage("Удаление...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] params) {
            List<NameValuePair> par = new ArrayList<>();
            par.add(new BasicNameValuePair(TAG_ID, mid));
            JSONObject json = jsonParser.makeHttpRequest(urlDeleteMedicine, "POST", par);
            Log.d("Результат удаления", json.toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            showAlertBox(5);
            pDialog.dismiss();
        }
    }

    class Create extends AsyncTask<String, String, String> {

        protected String doInBackground(String[] params) {
            List<NameValuePair> par = new ArrayList<>();
            try {
                par.add(new BasicNameValuePair(TAG_NAME, URLEncoder.encode(name, "UTF-8")));
                par.add(new BasicNameValuePair(TAG_DESCRIPTION, URLEncoder.encode(desc, "UTF-8")));
                par.add(new BasicNameValuePair(TAG_WAYTOUSE, URLEncoder.encode(waytouse, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject json = jsonParser.makeHttpRequest(urlCreateMedicine, "POST", par);
            Log.d("Создание", json.toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            showAlertBox(6);
            pDialog.dismiss();
        }
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            JSONObject json = jsonParser.makeHttpRequest(urlGetAllMedicine, "POST", new ArrayList<>());
            Log.d("Все лекарства", json.toString());
            try {
                JSONArray medicine = json.getJSONArray(TAG_PRODUCT);
                for (int i = 0; i < medicine.length(); i++) {
                    names.add(i, medicine.getJSONObject(i).getString(TAG_NAME));
                }
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