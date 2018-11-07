package com.daniel.friendcompass.activities.AuthenticationActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.daniel.friendcompass.userrepository.UserRepository;
import com.daniel.friendcompass.util.VerifyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.emailEditText)
    EditText emailEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.confirmPasswordEditText)
    EditText confirmPasswordEditText;
    @BindView(R.id.signUpBtn)
    Button signUpButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    nameEditText.setError("Name cannot be empty!");
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email cannot be empty");
                    return;
                } else if (!VerifyUtil.verifyEmail(email)) {
                    emailEditText.setError("Invalid email!");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password cannot be empty!");
                    return;
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    passwordEditText.setError("Confirm password cannot be empty!");
                    return;
                } else if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                signUpUser(name, email, password);
            }
        });
    }

    private void signUpUser(final String name, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            setUserDisplayName(name);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toasty.error(BaseApplication.getInstance(), "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void setUserDisplayName(String name) {
        FirebaseUser user = auth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserRepository.getInstance().createNewUser(auth.getCurrentUser());
                            progressBar.setVisibility(View.INVISIBLE);
                            showSuccessfulDialog();
                        }
                    }
                });
    }

    private void showSuccessfulDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have successfully signed up!")
                .setTitle("Success");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                navigateToMainActivity();
            }
        });
        builder.create().show();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
}
