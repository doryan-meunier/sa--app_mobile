package com.example.signalementrenouvelement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class PageAccueil extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String userUID;
    private FirebaseFirestore db;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_page_accueil);

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
                            intent = new Intent(PageAccueil.this, StatsActivity.class);
                            intent.putExtra("userUID", userUID);
                            startActivity(intent);
                        } else if (id == R.id.admin) { // id == R.id.admin
                            intent = new Intent(PageAccueil.this, AdminActivity.class);
                            intent.putExtra("userUID", userUID);
                            startActivity(intent);
                        }
                        else {
                            // L'utilisateur n'est pas un admin ou le champ admin est absent ou false
                            //Toast.makeText(PageAccueil.this, "Accès restreint aux administrateurs.", Toast.LENGTH_LONG).show();

                    }
                    }
                });
                if (id == R.id.logout) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(PageAccueil.this, MainActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                    Toast.makeText(PageAccueil.this,
                            "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.signaler) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(PageAccueil.this, signalementRenouvelement.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                } else if (id == R.id.messignalements) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(PageAccueil.this, MesSigalementsActivity.class);
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

    public void btn_messignalements(View view) {
        Intent intent = new Intent(PageAccueil.this, MesSigalementsActivity.class);
        intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
        startActivity(intent);
    }
    public void btn_signaler(View view) {
        Intent intent = new Intent(PageAccueil.this, signalementRenouvelement.class);
        intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
        startActivity(intent);
    }
    public void btn_age(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userUID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.getBoolean("admin") != null && documentSnapshot.getBoolean("admin")) {

                Intent intent = new Intent(PageAccueil.this, StatsActivity.class);
                intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                startActivity(intent);

            } else {
                // L'utilisateur n'est pas un admin ou le champ admin est absent ou false
                Toast.makeText(PageAccueil.this, "Accès restreint aux administrateurs.", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void btn_medocsignales(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userUID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.getBoolean("admin") != null && documentSnapshot.getBoolean("admin")) {

                Intent intent = new Intent(PageAccueil.this, AdminActivity.class);
                intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                startActivity(intent);

            } else {
                // L'utilisateur n'est pas un admin ou le champ admin est absent ou false
                Toast.makeText(PageAccueil.this, "Accès restreint aux administrateurs.", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void btn_deco(View view) {
        Intent intent = new Intent(PageAccueil.this, MainActivity.class);
        intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
        startActivity(intent);
    }


}