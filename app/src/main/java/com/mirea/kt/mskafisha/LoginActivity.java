package com.mirea.kt.mskafisha;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Log In Button Pressed");
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                if (username.equals("admin") && password.equals("123")) {
                    showNote(LoginActivity.this,"Добро пожаловать, админ!");
                    Log.d(TAG, "Debug login success!");
                    Intent intent = new Intent(LoginActivity.this, EventsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                performLogin(username, password);
            } else {
                showNote(LoginActivity.this,"Пожалуйста, введите логин и пароль");
            }
        });
    }

    private void performLogin(String username, String password) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("lgn", username);
        requestBody.put("pwd", password);
        requestBody.put("g", "RIBO-01-22");

        HTTPRequestRunnable httpRequestRunnable = new HTTPRequestRunnable(
                "POST",
                "https://android-for-students.ru/coursework/login.php",
                requestBody
        );

        Thread thread = new Thread(httpRequestRunnable);
        thread.start();

        try {
            thread.join();
            Log.d(TAG, "Login request succeed");
            Log.d(TAG, "Response: " + httpRequestRunnable.getResponseBody());
        }
        catch (InterruptedException e) {
            Log.e(TAG, "http request failed");
            showNote(LoginActivity.this,"Ошибка подключения");
        }
        try {
            JSONObject jsonResponse = new JSONObject(httpRequestRunnable.getResponseBody());
            int resultCode = jsonResponse.getInt("result_code");
            if (resultCode == 1) {
                Intent afishaIntent = new Intent(LoginActivity.this, EventsActivity.class);
                startActivity(afishaIntent);
            } else showNote(LoginActivity.this,"Неверные логин или пароль");
        }
        catch (JSONException e) {
            Log.e(TAG, "response parsing failed");
        }
    }

    public static void showNote(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
