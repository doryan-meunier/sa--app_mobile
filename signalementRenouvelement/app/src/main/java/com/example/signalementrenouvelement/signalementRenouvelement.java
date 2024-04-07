package com.example.signalementrenouvelement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class signalementRenouvelement extends AppCompatActivity {
    long nbSignalement = 1;
    private boolean pasEncoreSignale = false;
    private DrawerLayout drawerLayout;
    EditText editTextCipCode;
    private String userUID;
    Button buttonEnterCip, buttonScanDataMatrix;
    private FirebaseFirestore db;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signalementrenouvelement);
        db = FirebaseFirestore.getInstance();


        editTextCipCode = findViewById(R.id.etCipCode);
        buttonEnterCip = findViewById(R.id.btnEnterCipCode);
        buttonScanDataMatrix = findViewById(R.id.btnScanDataMatrix);



        buttonEnterCip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeCIP = editTextCipCode.getText().toString();
                if (!codeCIP.isEmpty()) {
                    lireFichierMedicaments(codeCIP);
                } else {
                    Toast.makeText(signalementRenouvelement.this, "Veuillez entrer un code CIP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonScanDataMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lancer le scanner ZXing
                new IntentIntegrator(signalementRenouvelement.this).initiateScan();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Intent intent = getIntent();
        userUID = intent.getStringExtra("userUID");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Fermer le drawer après un clic
                drawerLayout.closeDrawer(GravityCompat.START);
                int id = item.getItemId();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(userUID);

                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getBoolean("admin") != null && documentSnapshot.getBoolean("admin")) {
                        // L'utilisateur est un admin, lancez l'activité correspondante
                        Intent intent;
                        if (id == R.id.stats) {
                            intent = new Intent(signalementRenouvelement.this, StatsActivity.class);
                            intent.putExtra("userUID", userUID);
                            startActivity(intent);
                        } else if (id == R.id.admin) { // id == R.id.admin
                            intent = new Intent(signalementRenouvelement.this, AdminActivity.class);
                            intent.putExtra("userUID", userUID);
                            startActivity(intent);
                        }
                        else {
                            // L'utilisateur n'est pas un admin ou le champ admin est absent ou false
                            //Toast.makeText(signalementRenouvelement.this, "Accès restreint aux administrateurs.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                if (id == R.id.logout) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(signalementRenouvelement.this, MainActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                    Toast.makeText(signalementRenouvelement.this,
                            "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.messignalements) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(signalementRenouvelement.this, MesSigalementsActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                }
                else if (id == R.id.pageaccueil) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(signalementRenouvelement.this, PageAccueil.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                }

                else {
                    return false;
                }
                return true;
            }
        });
    }


    private void afficherDialogueConfirmation(String titre, String message, String nomMedicament, String codeCIP, String codeCIS) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titre);
        builder.setMessage(message);

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Ajoutez ici l'action à effectuer après confirmation
                Map<String, Object> medicament = new HashMap<>();
                medicament.put("médicament", nomMedicament);
                medicament.put("code cis", codeCIS);
                medicament.put("nb signalement", nbSignalement);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef2 = db.collection("medicaments signales").document(userUID);

                docRef2.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Le document a été récupéré avec succès
                            List<String> monTableau = (List<String>) document.get("code cip");
                            if (monTableau.contains(codeCIP)) {
                                Log.d("Firestore", "La valeur est présente dans le tableau.");
                                pasEncoreSignale = true;
                                Toast.makeText(signalementRenouvelement.this,
                                        "Médicament déjà signalé.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("Firestore", "La valeur n'est pas présente dans le tableau.");
                                pasEncoreSignale = false;
                            }
                        } else {
                                // Le tableau n'existe pas dans le document
                            pasEncoreSignale = false;
                            }
                    } else {
                            Log.d("Firestore", "Aucun document trouvé");
                        pasEncoreSignale = false;
                    }
                });

                DocumentReference docRef = db.collection("signalementMedicament").document(codeCIP);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && !pasEncoreSignale) {
                            nbSignalement = documentSnapshot.getLong("nb signalement");
                            Log.d("nbSignalement", String.valueOf(nbSignalement));
                            db.collection("signalementMedicament").document(codeCIP)
                                    .update("nb signalement", FieldValue.increment(1))
                                    .addOnSuccessListener(aVoid -> Toast.makeText(signalementRenouvelement.this,
                                            "Signalement effectué.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(signalementRenouvelement.this,
                                            "Erreur lors du signalement : ", Toast.LENGTH_LONG).show());

                        }


                        if (!pasEncoreSignale && !documentSnapshot.exists()) {
                            db.collection("signalementMedicament").document(codeCIP).set(medicament)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(signalementRenouvelement.this,
                                                "Signalement effectué.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(signalementRenouvelement.this,
                                            "Erreur lors du signalement : ", Toast.LENGTH_LONG).show());
                        }

                        Map<String, Object> data = new HashMap<>();
                        data.put("code cip", FieldValue.arrayUnion(codeCIP));

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Assurez-vous que userUID ou codeCIP est l'ID de document souhaité
                        DocumentReference docRef2 = db.collection("medicaments signales").document(userUID);

                        docRef2.set(data, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document créé ou mis à jour avec succès"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Erreur lors de la création ou de la mise à jour du document", e));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gestion de l'échec de la récupération du document
                        Toast.makeText(getApplicationContext(), "Erreur lors de la récupération du document.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String lireFichierMedicaments(String codeCip13Recherche) {
        AssetManager assetManager = getAssets();
        String codeCisRecherche = null;

        // Première recherche : Trouver le Code CIS avec le Code CIP
        try {
            InputStream inputStream = assetManager.open("medicaments.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String ligne;

            while ((ligne = reader.readLine()) != null) {
                String[] champs = ligne.split("\t"); // ou utilisez le séparateur correct
                String codeCip13 = champs[6]; // Assurez-vous que c'est le bon indice pour le code CIP

                if (codeCip13.equals(codeCip13Recherche)) {
                    codeCisRecherche = champs[0]; // Obtenez le Code CIS
                    break; // Arrêtez la boucle une fois trouvé
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (codeCisRecherche == null) {
            Toast.makeText(this, "Médicament non trouvé.", Toast.LENGTH_LONG).show();
            return "Code CIS non trouvé";
        }

        // Deuxième recherche : Trouver la Dénomination du médicament avec le Code CIS
        try {
            InputStream inputStream = assetManager.open("nomMedicaments.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String ligne;

            while ((ligne = reader.readLine()) != null) {
                String[] champs = ligne.split("\t"); // ou utilisez le séparateur correct
                String codeCis = champs[0]; // Assurez-vous que c'est le bon indice pour le code CIS

                if (codeCis.equals(codeCisRecherche)) {
                    String denominationMedicament = champs[1]; // Obtenez la Dénomination
                    // Mise à jour de l'UI doit être faite sur le thread UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            afficherDialogueConfirmation("Confirmation", "Êtes-vous sûr de vouloir signaler ce médicament : " + denominationMedicament + " ?", denominationMedicament, codeCip13Recherche, codeCis);
                        }
                    });
                    return ligne; // Retournez la ligne ou la dénomination selon le besoin
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Médicament non trouvé";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_LONG).show();
            } else {
                String scannedData = result.getContents();
                String firstThirteenChars = scannedData.length() >= 13 ? scannedData.substring(4, 17) : scannedData;
                lireFichierMedicaments(firstThirteenChars);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
