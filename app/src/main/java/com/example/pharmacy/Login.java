package com.example.pharmacy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private static final String urlGetUser = "http://n90926b9.beget.tech/getUser.php";
    private static final String TAG_ID = "id";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASS = "password";
    private static final String TAG_USER = "user";
    private final JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private EditText loginTxt, passTxt;
    private String login, password;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginBtn = findViewById(R.id.btnLogin);
        loginTxt = findViewById(R.id.inputLogin);
        passTxt = findViewById(R.id.inputPass);
        loginBtn.setOnClickListener(view -> {
            login = loginTxt.getText().toString();
            password = passTxt.getText().toString();
            new GetUser().execute();
        });
    }

    public void showAlertBox(int i){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(Login.this);
        if (i == 1) {
            quitDialog.setTitle("Неверный логин или пароль!");
            quitDialog.setPositiveButton("Понял!", (dialog, which) -> passTxt.setText(""));
        }else{
            quitDialog.setTitle("Вы успешно вошли!");
            quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                startActivity(intent);
                finish();
            });
        }
        quitDialog.show();
    }

    class GetUser extends AsyncTask<String, String, String> {
        JSONArray userObj;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Загрузка пользователей...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected String doInBackground(String[] params) {
            try {
                pDialog.dismiss();
                List<NameValuePair> par = new ArrayList<>();
                par.add(new BasicNameValuePair(TAG_LOGIN, login));
                par.add(new BasicNameValuePair(TAG_PASS, password));
                JSONObject json = jsonParser.makeHttpRequest(urlGetUser, "POST", par);
                Log.d("Детали пользователя", json.toString());
                userObj = json.getJSONArray(TAG_USER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (userObj.length()>0){
                try {
                    JSONObject user = userObj.getJSONObject(0);
                    intent = new Intent(getApplicationContext(), WorkScreen.class);
                    intent.putExtra(TAG_ID, user.getString(TAG_ID));
                    showAlertBox(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showAlertBox(1);
            }
            pDialog.dismiss();
        }
    }
}