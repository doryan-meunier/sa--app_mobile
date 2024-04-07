package com.example.signalementrenouvelement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MesSigalementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private List<String> listNom;
    private String userUID;
    private TextView aucunSignalement;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private List<String> myDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_sigalements);
        Intent intent = getIntent();
        userUID = intent.getStringExtra("userUID");
        listNom = new ArrayList<>();

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aucunSignalement = findViewById(R.id.pasdesignalement);

        myDataList = new ArrayList<>();
        adapter = new MyListAdapter(myDataList);
        recyclerView.setAdapter(adapter);

        // Récupérer les données depuis Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("medicaments signales").document(userUID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> listeCodesCip = (List<String>) documentSnapshot.get("code cip");
                if (listeCodesCip != null && !listeCodesCip.isEmpty()) {
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (String codeCip : listeCodesCip) {
                        Task<DocumentSnapshot> task = db.collection("signalementMedicament").document(codeCip).get();
                        tasks.add(task);
                    }

                    // Attendre que toutes les requêtes soient terminées
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                        listNom.clear(); // Assurez-vous que listNom est initialisé correctement ailleurs dans votre code
                        for (Object result : results) {
                            DocumentSnapshot document = (DocumentSnapshot) result;
                            String nomMedicament = document.getString("médicament"); // Assurez-vous que la clé correspond à ce que vous avez dans Firestore
                            if (nomMedicament != null) {
                                listNom.add(nomMedicament);
                            }
                        }

                        // Ici, toutes les données ont été chargées, mettez à jour l'UI ici
                        if (!listNom.isEmpty()) {
                            Log.d("signalements", "liste pas vide");
                            aucunSignalement.clearComposingText();
                            myDataList.clear();
                            myDataList.addAll(listNom);
                            adapter.notifyDataSetChanged();
                        }



                    });
                }
            } else {
                Log.d("aucun signalement", "liste vide");
                aucunSignalement.setText("Aucun signalement effectué");
            }
        });

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                drawerLayout = findViewById(R.id.drawer_layout);
                actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(actionBarDrawerToggle);
                actionBarDrawerToggle.syncState();
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
                                    intent = new Intent(MesSigalementsActivity.this, StatsActivity.class);
                                    intent.putExtra("userUID", userUID);
                                    startActivity(intent);
                                } else if (id == R.id.admin) { // id == R.id.admin
                                    intent = new Intent(MesSigalementsActivity.this, AdminActivity.class);
                                    intent.putExtra("userUID", userUID);
                                    startActivity(intent);
                                }
                                else {
                                    // L'utilisateur n'est pas un admin ou le champ admin est absent ou false
                                    //Toast.makeText(MesSigalementsActivity.this, "Accès restreint aux administrateurs.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                        if (id == R.id.logout) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                            // Lancer l'activité equipementsAlloues et passer l'UID
                            Intent intent = new Intent(MesSigalementsActivity.this, MainActivity.class);
                            intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                            startActivity(intent);
                            Toast.makeText(MesSigalementsActivity.this,
                                    "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.signaler) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                            // Lancer l'activité equipementsAlloues et passer l'UID
                            Intent intent = new Intent(MesSigalementsActivity.this, signalementRenouvelement.class);
                            intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                            startActivity(intent);
                        } else if (id == R.id.pageaccueil) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                            // Lancer l'activité equipementsAlloues et passer l'UID
                            Intent intent = new Intent(MesSigalementsActivity.this, PageAccueil.class);
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

        }