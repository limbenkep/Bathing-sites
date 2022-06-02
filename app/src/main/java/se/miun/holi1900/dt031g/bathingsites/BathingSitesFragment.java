package se.miun.holi1900.dt031g.bathingsites;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import se.miun.holi1900.dt031g.bathingsites.db.BathingSite;
import se.miun.holi1900.dt031g.bathingsites.db.BathingSitesRepository;


public class BathingSitesFragment extends Fragment implements BathingSitesView.OnClickedListener {
    private BathingSitesView bathingSitesView;
    private static final String TAG = "BathingSitesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bathing_sites, container, false);
        bathingSitesView  = view.findViewById(R.id.bathingSiteView);
        bathingSitesView.setOnClickedListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new BathingSitesRepository(requireContext()).numberOfBathingSites().observe(getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "onChanged: "+integer);
            bathingSitesView.setCounter(integer);
        });
    }

    @Override
    public void onClick(BathingSitesView dialButtonView) {
        new BathingSitesRepository(requireContext()).getAllBathingSites()
                .observe(getViewLifecycleOwner(), this::displayRandomBathingSite);
    }

    private void displayRandomBathingSite(List<BathingSite> list){
        Context context = requireContext();
        if(list.size() !=0){
            Collections.shuffle(list);
            Random rand = new Random();
            BathingSite site = list.get(rand.nextInt(list.size()));
            Log.d(TAG, "displayRandomBathingSite: " + site.siteName);

            CharSequence text = "Name: " + site.siteName + "\n"
                    + "Description: " + site.description + "\n"
                    + "Address: " + site.address + "\n"
                    + "Latitude: " + site.latitude + "\n"
                    + "Longitude: " + site.longitude + "\n"
                    + "Grade: " + site.grade;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            Toast.makeText(context, "No bathing sites found in the database.", Toast.LENGTH_SHORT).show();
        }
    }
}