package com.example.signalementrenouvelement;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> {
    Activity activity;
    int itemResourceId;
    List<Medicament> items;

    public MedicamentAdapter(Activity activity, int itemResourceId,
                          List<Medicament> items){
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View layout = convertView;
        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);
        }
        TextView medicTv = (TextView) layout.findViewById(R.id.medicTV);
        TextView nbSignal = (TextView) layout.findViewById(R.id.nbSignalement);

        Medicament medicament = items.get(position);

        medicTv.setText(medicament.getNom());
        nbSignal.setText(String.valueOf(medicament.getNbsignalement()));

        return layout;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
