package com.example.signalementrenouvelement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private FirebaseAuth auth;
    private Button ResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailC);
        ResetButton = findViewById(R.id.envoie_email);

        auth = FirebaseAuth.getInstance();

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(forgotPassword.this, "Entrez votre adresse email", Toast.LENGTH_LONG).show();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(forgotPassword.this, "Email de réinitialisation envoyé", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(forgotPassword.this, "Échec de l'envoi de l'email", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(forgotPassword.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
    public void connection(View view) {
        Intent intent = new Intent(forgotPassword.this, MainActivity.class);
        startActivity(intent);
    }

    public void retourAccueil2(View view) {
        Intent intent = new Intent(forgotPassword.this, MainActivity.class);
        startActivity(intent);
    }
}
