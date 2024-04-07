package com.example.signalementrenouvelement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdminActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    protected final int home = 1;
    protected final int liste = 2;
    protected final int profil = 3;private DrawerLayout drawerLayout;
    private String userUID;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Récupérer la référence de la ListView
        ListView listView = findViewById(R.id.scrollView);

        // Récupérer les données des utilisateurs depuis Firestore
        db.collection("signalementMedicament").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Medicament> medics = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        // Récupérer les données de chaque utilisateur et les ajouter à la liste
                        String codeCIS = document.getString("code cis");
                        String nomMedic = document.getString("médicament");
                        long nbSignalement =  document.getLong("nb signalement");
                        Medicament medicament = new Medicament(codeCIS,nomMedic,nbSignalement);

                        medics.add(medicament);
                    }
                    // Trier la liste en fonction du nombre de signalements
                    Collections.sort(medics, new Comparator<Medicament>() {
                        @Override
                        public int compare(Medicament medicament1, Medicament medicament2) {
                            return Long.compare(medicament2.getNbsignalement(), medicament1.getNbsignalement());
                        }
                    });
                    // Créer et configurer l'adaptateur pour afficher les utilisateurs dans la ListView
                    MedicamentAdapter adapter = new MedicamentAdapter(AdminActivity.this,
                            R.layout.item_medicament,
                            medics);
                    listView.setAdapter(adapter);
                } else {
                    // Gérer les erreurs de récupération des données depuis Firestore
                    // Vous pouvez afficher un message d'erreur ou effectuer une autre action ici
                    Exception e = task.getException();
                    Log.e("Firebase", "Error getting documents: " + e); // Ajouter ce log
                }
            }
        });

        BarChart barChart = findViewById(R.id.chart);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("signalementMedicament").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                List<String> xValues = new ArrayList<>();

                int index = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //Medicament medicament = document.toObject(Medicament.class);
                    Long nbSignalement = document.getLong("nb signalement");
                    // Utilisez medicament.getMedicament() pour le nom et medicament.getNbSignalement() pour la valeur
                    entries.add(new BarEntry(index, nbSignalement));
                    xValues.add(document.getId()); // Le nom du médicament comme label de l'axe X
                    index++;
                }

                setupBarChart(barChart, entries, xValues);
            } else {
                Log.d("FirestoreError", "Erreur lors de la récupération des documents: ", task.getException());
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

                if (id == R.id.stats) {
                    // Lancer l'activité pour éditer le profil
                    Intent intent = new Intent(AdminActivity.this, StatsActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                } else if (id == R.id.pageaccueil) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(AdminActivity.this, PageAccueil.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                } else if (id == R.id.logout) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                    Toast.makeText(AdminActivity.this,
                            "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.signaler) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(AdminActivity.this, signalementRenouvelement.class);
                    intent.putExtra("userUID", userUID); // Assurez-vous que userUID est bien récupéré et stocké dans AccueilActivity
                    startActivity(intent);
                } else if (id == R.id.messignalements) { // Assurez-vous que l'ID correspond à celui défini dans votre menu.xml
                    // Lancer l'activité equipementsAlloues et passer l'UID
                    Intent intent = new Intent(AdminActivity.this, MesSigalementsActivity.class);
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

    private void setupBarChart(BarChart barChart, ArrayList<BarEntry> entries, List<String> xValues) {
        BarDataSet dataSet = new BarDataSet(entries, "Nombre de Signalements");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate(); // Rafraîchir le graphique
    }
}