package com.example.signalementrenouvelement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String userUID;
    private FirebaseFirestore db;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_stats);

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

                if (id == R.id.pageaccueil) {
                    // Lancer l'activité pour éditer le profil
                    Intent intent = new Intent(StatsActivity.this, PageAccueil.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                }
                 else if (id == R.id.logout) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                    Toast.makeText(StatsActivity.this,
                            "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.signaler) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(StatsActivity.this, signalementRenouvelement.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                } else if (id == R.id.messignalements) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(StatsActivity.this, MesSigalementsActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                }
                else if (id == R.id.admin) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(StatsActivity.this, AdminActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                }
                else {
                    return false;
                }
                return true;
            }
        });

        Camembert();

    }

    private String categorizeAge(int age) {
        if (age <= 20) {
            return "0-20";
        } else if (age <= 40) {
            return "21-40";
        } else if (age <= 60) {
            return "41-60";
        } else {
            return "61+";
        }
    }

    private void createPieChart(Map<Integer, Integer> ageMap) {
        PieChart pieChart = findViewById(R.id.pieChart);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : ageMap.entrySet()) {
            // Ajoutez chaque âge et son comptage comme une entrée dans le graphique
            entries.add(new PieEntry(entry.getValue(), "" + entry.getKey()));
        }

        PieDataSet set = new PieDataSet(entries, "");
        PieData data = new PieData(set);
        pieChart.setData(data);
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // Rafraîchir le graphique
    }

    private void Camembert() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<Integer, Integer> ageMap = new HashMap<>();

        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Integer age = document.getLong("age").intValue(); // Récupérer l'âge
                    ageMap.merge(age, 1, Integer::sum); // Compter les occurrences de chaque âge
                }
                // Une fois les données récupérées et organisées, créez le graphique
                createPieChart(ageMap);
            } else {
                Log.w("erreur", "Erreur lors de la récupération des documents.", task.getException());
            }
        });
    }

}

