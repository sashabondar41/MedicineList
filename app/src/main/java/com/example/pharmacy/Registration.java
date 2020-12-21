package com.example.pharmacy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class Registration extends AppCompatActivity {

    private static final String urlNewUser = "http://n90926b9.beget.tech/newUser.php";
    private static final String urlGetLogin = "http://n90926b9.beget.tech/getLogin.php";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASS = "password";
    private static final String TAG_USER = "user";
    private final JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private EditText loginTxt, passTxt, repeatTxt;
    private String login, pass, repeatPass;
    boolean exists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Button regBtn = findViewById(R.id.btnReg);
        loginTxt = findViewById(R.id.regLogin);
        passTxt = findViewById(R.id.regPass);
        repeatTxt = findViewById(R.id.repeatPass);
        regBtn.setOnClickListener(view -> {
            login = loginTxt.getText().toString();
            pass = passTxt.getText().toString();
            repeatPass = repeatTxt.getText().toString();
            new GetUser().execute();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (pass.length()>=8 ){
                if (login.length()<=11 && login.length()>=5){
                    if (pass.equals(repeatPass)){
                        if (!exists) {
                            new NewUser().execute();
                            showAlertBox(3);
                        }else{
                            showAlertBox(2);
                        }
                    }else{
                        showAlertBox(1);
                    }
                }else{
                    showAlertBox(5);
                }
            }else{
                showAlertBox(4);
            }
        });
    }

    public void showAlertBox(int i){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(Registration.this);
        switch (i) {
            case(1):
                quitDialog.setTitle("Пароли не совпадают!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    passTxt.setText("");
                    repeatTxt.setText("");
                });
                break;
            case(2):
                quitDialog.setTitle("Юзер с таким логином уже существует!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    loginTxt.setText("");
                    passTxt.setText("");
                    repeatTxt.setText("");
                });
                break;
            case(3):
                quitDialog.setTitle("Вы успешно зарегистрировались!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> finish());
                break;
            case(4):
                quitDialog.setTitle("Пароль должен быть длиннее 7 символов!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    passTxt.setText("");
                    repeatTxt.setText("");
                });
                break;
            case(5):
                quitDialog.setTitle("Логин должен быть короче 12 символов и длиннее 4!");
                quitDialog.setPositiveButton("Понял!", (dialog, which) -> {
                    loginTxt.setText("");
                    passTxt.setText("");
                    repeatTxt.setText("");
                });
                break;
        }
        quitDialog.show();
    }

    class NewUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Создание профиля...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(TAG_LOGIN, login));
            params.add(new BasicNameValuePair(TAG_PASS, pass));
            JSONObject json = jsonParser.makeHttpRequest(urlNewUser, "POST", params);
            Log.d("Отчет о создании", json.toString());
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }

    class GetUser extends AsyncTask<String, String, String> {
        JSONArray userObj;

        protected String doInBackground(String[] params) {
            try {
                List<NameValuePair> par = new ArrayList<>();
                par.add(new BasicNameValuePair(TAG_LOGIN, login));
                JSONObject json = jsonParser.makeHttpRequest(urlGetLogin, "POST", par);
                Log.d("Детали пользователя", json.toString());
                userObj = json.getJSONArray(TAG_USER);
                exists = userObj.length() != 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}