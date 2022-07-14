package com.example.cookbook.ui.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.cookbook.MainActivity;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityLoginBinding;
import com.example.cookbook.model.LoginUser;
import com.example.cookbook.viewmodel.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button goToRegisterButton;
    FirebaseAuth firebaseAuth;

    private LoginViewModel loginViewModel;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

        goToRegisterButton = findViewById(R.id.go_to_register_button);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        loginViewModel.getUser().observe(this, new Observer<LoginUser>() {
            @Override
            public void onChanged(LoginUser loginUser) {
                if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getLogin())) {
                    binding.emailLogin.setError("Enter an E-Mail Address");
                    binding.emailLogin.requestFocus();
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPassword())) {
                    binding.passwordLogin.setError("Enter a Password");
                    binding.passwordLogin.requestFocus();
                }
                else {
                    binding.emailLoginText.setText(loginUser.getLogin());
                    binding.passwordLoginText.setText(loginUser.getPassword());
                    firebaseLogin(loginUser);
                }
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
    }

    private void firebaseLogin(LoginUser loginUser) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(loginUser.getLogin(),loginUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this,"Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}