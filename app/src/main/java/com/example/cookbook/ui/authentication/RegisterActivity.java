package com.example.cookbook.ui.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.cookbook.MainActivity;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityRegisterBinding;
import com.example.cookbook.model.LoginUser;
import com.example.cookbook.viewmodel.RegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        binding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);

        binding.setLifecycleOwner(this);

        binding.setRegisterViewModel(registerViewModel);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        registerViewModel.getUser().observe(this, new Observer<LoginUser>() {
            @Override
            public void onChanged(LoginUser loginUser) {
                if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getFirstName())) {
                    binding.emailRegister.setError("Enter an E-Mail Address");
                    binding.emailRegister.requestFocus();
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getLastName())) {
                    binding.passwordRegister.setError("Enter a Password");
                    binding.passwordRegister.requestFocus();
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getFirstName())) {
                    binding.firstNameRegister.setError("Enter a First Name");
                    binding.firstNameRegister.requestFocus();
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getLastName())) {
                    binding.lastNameRegister.setError("Enter a Last Name");
                    binding.lastNameRegister.requestFocus();
                }
                else {
                    binding.emailRegisterText.setText(loginUser.getLogin());
                    binding.passwordRegisterText.setText(loginUser.getPassword());
                    binding.firstNameRegisterText.setText(loginUser.getFirstName());
                    binding.lastNameRegisterText.setText(loginUser.getLastName());
                    firebaseRegister(loginUser);
                }
            }
        });
    }

    private void firebaseRegister(LoginUser loginUser) {
        firebaseAuth.createUserWithEmailAndPassword(loginUser.getLogin(),loginUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            loginUser.setUserID(task.getResult().getUser().getUid());
                            registerViewModel.addUserToDatabase(loginUser);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}