package com.daniel.friendcompass.activities.AuthenticationActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daniel.friendcompass.BaseApplication;
import com.daniel.friendcompass.R;
import com.daniel.friendcompass.util.VerifyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @BindView(R.id.emailEditText)
    EditText emailEditText;
    @BindView(R.id.sendEmailButton)
    Button sendEmailButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email cannot be empty!");
                    return;
                } else if (!VerifyUtil.verifyEmail(email)) {
                    emailEditText.setError("Invalid email!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            showSuccessDialog();
                        } else {
                            Toasty.error(BaseApplication.getInstance(), "An error occurred. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please check your email. We have just sent an email with instructions on how to reset your password.")
                .setTitle("Email Sent!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                navigateToSignInActivity();
            }
        });
        builder.create().show();
    }

    private void navigateToSignInActivity() {
        Intent mainIntent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
}
