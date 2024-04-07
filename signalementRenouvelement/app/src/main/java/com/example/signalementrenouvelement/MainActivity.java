package com.example.signalementrenouvelement;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private int age;
    private FirebaseAuth mAuth;
    private boolean premiereConnxion = false;
    private GoogleSignInClient mGoogleSignInClient;
    private String nomUtilisateur;
    private TextInputEditText emailEditText, passEditText;
    public static final String USER_UID_EXTRA = "userUID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailC);
        passEditText = findViewById(R.id.passCC);
        Button signInButton = findViewById(R.id.login);
        Button googleSignInButton = findViewById(R.id.google_login_button);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passEditText.getText().toString().trim();
            if (!password.isEmpty() && !email.isEmpty()) {
                signInWithEmail(email, password);
            }
            else {
                Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        googleSignInButton.setOnClickListener(view -> signInWithGoogle());


    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                nomUtilisateur = account.getDisplayName();
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("GoogleSignIn", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("GoogleSignIn", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void verifierPremiereConnexion(FirebaseUser user) {
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // L'utilisateur existe déjà, donc ce n'est pas la première connexion
                        Log.d("PremiereConnexion", "Pas la première connexion: " + user.getEmail());
                        Intent intent = new Intent(MainActivity.this, PageAccueil.class);
                        intent.putExtra("userUID", user.getUid()); // Ajout de l'UID à l'intent
                        startActivity(intent);
                        // Continuez avec votre logique d'application
                        premiereConnxion = true;
                    } else {
                        // C'est la première connexion, demandez l'âge
                        Log.d("PremiereConnexion", "Première connexion: " + user.getEmail());
                        demanderAge(user);
                    }
                } else {
                    Log.d("Firestore", "Erreur lors de la vérification de l'utilisateur", task.getException());
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String userUID = user.getUid(); // Obtenir l'UID de l'utilisateur
            String userEmail = user.getEmail();
            verifierPremiereConnexion(user);
            if (!premiereConnxion) {
                // L'utilisateur s'est déjà connecté, procédez en conséquence.
                Log.d("GoogleSignIn", "L'utilisateur est déjà connecté: ");

            } else {
                Log.d("connexion 1ere fois", "L'utilisateur est déjà connecté: ");
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("email", userEmail);
                userMap.put("nom", nomUtilisateur);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(user.getUid()).set(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(MainActivity.this, "Connexion réussie.", Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Connexion impossible.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void demanderAge(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String userUID = user.getUid();
        String userEmail = user.getEmail();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", userEmail);
        userMap.put("nom", nomUtilisateur);

        // Créer un EditText pour la saisie de l'âge
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Pour s'assurer que seul des nombres peuvent être entrés
        builder.setView(input);

        // Configurer le dialogue
        builder.setTitle("Entrez votre âge");
        builder.setMessage("Veuillez saisir votre âge :");

        // Bouton pour soumettre l'âge
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                age = Integer.parseInt(input.getText().toString());
                userMap.put("age", age);
                userMap.put("admin", false);
                Toast.makeText(MainActivity.this, "Connexion réussie.", Toast.LENGTH_SHORT).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(user.getUid()).set(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(MainActivity.this, "Connexion réussie.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, PageAccueil.class);
                            intent.putExtra("userUID", userUID); // Ajout de l'UID à l'intent
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Connexion impossible.", Toast.LENGTH_SHORT).show());
                // Faites quelque chose avec l'âge entré par l'utilisateur
            }
        });

        // Bouton pour annuler
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                user.delete();
                Toast.makeText(MainActivity.this, "Connexion annulée.", Toast.LENGTH_SHORT).show();
            }
        });

        // Afficher le dialogue
        builder.show();
    }

    public void register(View view) {
        Intent intent = new Intent(MainActivity.this, inscription.class);
        startActivity(intent);
    }

    public void mdp_forgot(View view) {
        Intent intent = new Intent(MainActivity.this, forgotPassword.class);
        startActivity(intent);
    }
    public void exit(View view) {
        System.exit(0);
    }


}
