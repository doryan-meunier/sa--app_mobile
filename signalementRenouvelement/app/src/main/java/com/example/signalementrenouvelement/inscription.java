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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class inscription extends AppCompatActivity {
    private FirebaseAuth auth; // Ajout de l'instance FirebaseAuth
    private TextInputEditText pseudoEditText, emailEditText, passEditText, passCEditText, ageval;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        passCEditText = findViewById(R.id.passC);
        pseudoEditText = findViewById(R.id.nom);
        ageval = findViewById(R.id.age);
        Button boutonClique = findViewById(R.id.btn_valid);
        boutonClique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validerChamps();
            }
        });
    }

    private void validerChamps() {
        String email = emailEditText.getText().toString().trim();
        String pass = passEditText.getText().toString().trim();
        String passC = passCEditText.getText().toString().trim();
        String age2 = ageval.getText().toString().trim();
        String nom = pseudoEditText.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty() || passC.isEmpty() || age2.isEmpty() || nom.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(passC)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nouvelle méthode pour inscrire l'utilisateur avec Firebase Authentication
        inscriptionUtilisateur(email, pass, age2, nom);
    }

    private void inscriptionUtilisateur(String email, String pass, String age, String nom) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inscription réussie
                        FirebaseUser user = auth.getCurrentUser();

                        int age1 = Integer.parseInt(age);
                        // Obtenir une instance de Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("nom", nom);
                        userMap.put("age", age1);
                        userMap.put("admin", false);

                        // Vérifier si l'utilisateur n'est pas null
                        if (user != null) {
                            db.collection("users").document(user.getUid()).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(inscription.this, "Inscription réussie avec stockage des informations.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(inscription.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(inscription.this, "Erreur lors du stockage des informations.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // Si l'inscription échoue, affichage d'un message à l'utilisateur
                        Toast.makeText(inscription.this, "L'inscription a échoué.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void register(View view){
        startActivity(new Intent(inscription.this, MainActivity.class));
    }

    public void retourAccueil1(View view) {
        Intent intent = new Intent(inscription.this, MainActivity.class);
        startActivity(intent);
    }

    public void connect(View view) {
        Intent intent = new Intent(inscription.this, MainActivity.class);
        startActivity(intent);
    }
}
