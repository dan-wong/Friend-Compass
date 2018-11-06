package com.daniel.friendcompass.activities.AuthenticationActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daniel.friendcompass.BaseApplication;
import com.daniel.friendcompass.R;
import com.daniel.friendcompass.activities.MainActivity.MainActivity;
import com.daniel.friendcompass.util.VerifyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();

    @BindView(R.id.emailEditText)
    EditText emailEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.signInBtn)
    Button signInButton;
    @BindView(R.id.signUpBtn)
    Button signUpButton;
    @BindView(R.id.forgotPasswordBtn)
    Button forgotPasswordButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email cannot be empty!");
                    return;
                } else if (!VerifyUtil.verifyEmail(email)) {
                    emailEditText.setError("Invalid email!");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password cannot be empty!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                signInUser(email, password);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            progressBar.setVisibility(View.INVISIBLE);
                            navigateToMainActivity();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toasty.error(BaseApplication.getInstance(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
}
