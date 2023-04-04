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

/**
 * The fragment contain a BathingSiteView and
 * defines the behavior when the BathingSiteView is clicked
 */
public class BathingSitesFragment extends Fragment implements BathingSitesView.OnClickedListener {
    private BathingSitesView bathingSitesView;
    private static final String TAG = "BathingSitesFragment";

    /**
     * On create, the View is inflated with the view,s layout and
     * a listener is set for the bathing site view component.
     * @param inflater inflates view with it's layout
     * @param container base of the layout and defines ViewGroup.LayoutParams which serve as
     *                  base for class layout parameters
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     * @return inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bathing_sites, container, false);
        bathingSitesView  = view.findViewById(R.id.bathingSiteView);
        bathingSitesView.setOnClickedListener(this);
        return view;
    }

    /**
     * updates number od bathing sites displayed on the BathingSiteView when view is created
     * @param view view
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateNumberOfBathingSites();
    }

    /**
     * updates number od bathing sites displayed on the BathingSiteView when view is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        updateNumberOfBathingSites();
    }

    /**
     * Get number of bathing sites stored in the database and sets is as the number of bathing
     * sites displayed on the BathingSiteView
     */
    private void updateNumberOfBathingSites() {
        new BathingSitesRepository(requireContext()).numberOfBathingSites().observe(getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "onChanged: "+integer);
            bathingSitesView.setCounter(integer);
        });
    }

    /**
     * Overrides onClick method for the BathingSiteView custom OnClickedListener.
     * Displays a random bathing site from the database whenever the BathingSiteView in
     * this fragment is clicked
     * @param dialButtonView dialButtonView that is clicked
     */
    @Override
    public void onClick(BathingSitesView dialButtonView) {
        new BathingSitesRepository(requireContext()).getAllBathingSites()
                .observe(getViewLifecycleOwner(), this::displayRandomBathingSite);
    }

    /**
     * Selects a random bathing site and displays information about the bathing site in a Toast
     * @param list list of BathingSite objects
     */
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