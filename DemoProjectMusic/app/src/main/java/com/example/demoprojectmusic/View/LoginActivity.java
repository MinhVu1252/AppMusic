package com.example.demoprojectmusic.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoprojectmusic.Model.UserDataManager;
import com.example.demoprojectmusic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtEmail, edtPassword;
    Button btnLogin;
    CheckBox cb_Remember;
    SharedPreferences mPrefs;

    private static final String PREFS_NAME = "PrefsFile";
    FirebaseAuth mAuth;
    TextView tvRegisterNow, tvForget_password;
    ProgressBar progressBar;

    ImageView imv_show_password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        addControls();
        addEvents();
        mAuth = FirebaseAuth.getInstance();
        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        // Lấy email và mật khẩu từ Intent
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        // Điền email và mật khẩu vào các trường đăng nhập
        edtEmail.setText(email);
        edtPassword.setText(password);
        getReferensData();
    }

    private void addEvents(){
        imv_show_password.setImageResource(R.drawable.baseline_visibility_off_24);
        imv_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    imv_show_password.setImageResource(R.drawable.baseline_visibility_off_24);
                }else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imv_show_password.setImageResource(R.drawable.baseline_visibility_24);
                }

            }
        });

        tvRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvForget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString();

                Log.e("cshacv", email );
                Log.e("cshacscsccv", password );
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplication(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    edtEmail.setText("");
                    Toast.makeText(getApplication(), "Invalid Email Format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra kết nối mạng
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Lỗi mạng, kiểm tra kết nối Internet", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplication(), "Password is Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 5){
                    edtPassword.setText("");
                    Toast.makeText(getApplication(), "Password Must be >= 5 Characters", Toast.LENGTH_SHORT).show();
                    return;
                }




                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    if (cb_Remember.isChecked()) {
                                        Boolean boolIsChecked = cb_Remember.isChecked();
                                        SharedPreferences.Editor editor = mPrefs.edit();
                                        editor.putString("pref_email", edtEmail.getText().toString());
                                        editor.putString("pref_password", edtPassword.getText().toString());
                                        editor.putBoolean("pref_check", boolIsChecked);
                                        editor.apply();
                                        Toast.makeText(getApplicationContext(), "Settings have been saved.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mPrefs.edit().clear().apply();
                                    }
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        int userId = user.getUid().hashCode();
                                        Toast.makeText(getApplicationContext(), "Login Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                                        intent.putExtra("user_id", userId);
                                        Log.e("user_id", String.valueOf(userId));
                                        startActivity(intent);
                                        finish();
                                    } else {

                                    }
                                }
                                else {
                                    // Xảy ra lỗi khi đăng nhập
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthInvalidUserException || exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        // Sai tài khoản hoặc mật khẩu
                                        Toast.makeText(getApplication(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                                    } else if (exception instanceof FirebaseNetworkException) {
                                        // Lỗi mạng
                                        Toast.makeText(getApplication(), "Lỗi mạng, kiểm tra kết nối Internet", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Lỗi khác
                                        Toast.makeText(getApplication(), "Lỗi khi đăng nhập: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    edtPassword.getText().clear();
                                    edtEmail.getText().clear();
                                }
                            }
                        });

            }
        });
    }

    private void addControls(){
        edtEmail = (TextInputEditText) findViewById(R.id.edtEmail);
        edtPassword = (TextInputEditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvRegisterNow = (TextView)findViewById(R.id.tvRegisterNow) ;
        imv_show_password = (ImageView) findViewById(R.id.imv_show_password);
        tvForget_password = (TextView) findViewById(R.id.tvForget_Password);
        cb_Remember = (CheckBox) findViewById(R.id.checkbox_Remember);
    }


    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@gmail.com";
        return email.matches(emailPattern);
    }

    private void getReferensData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(sp.contains("pref_email")){
            String e  = sp.getString("pref_email", "not found");
            edtEmail.setText(e.toString());
        }
        if (sp.contains("pref_password")){
            String p = sp.getString("pref_password", "not found");
            edtPassword.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            Boolean b = sp.getBoolean("pref_check", false);
            cb_Remember.setChecked(b);
        }
    }
}
