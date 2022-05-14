package se.miun.holi1900.dt031g.bathingsites;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;



public class AddBathingSiteFragment extends Fragment {
    EditText name;
    EditText description;
    EditText address;
    EditText latitude;
    EditText longitude;
    RatingBar grade;
    EditText waterTemp;
    EditText tempDate;
    Drawable error_icon;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bathing_site, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_options, menu);
        inflater.inflate(R.menu.settings_menu, menu);
        inflater.inflate(R.menu.menu_show_weather, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.save_form_option){
            return saveEnteredInformation();
        }
        if(item.getItemId()==R.id.delete_form_option){
            clearEntryFields();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get references to the editTexts in the fragment
        name = view.findViewById(R.id.nameEntry);
        description  = view.findViewById(R.id.descriptionEntry);
        address = view.findViewById(R.id.addressEntry);
        latitude = view.findViewById(R.id.latitudeEntry);
        longitude = view.findViewById(R.id.longitudeEntry);
        grade = view.findViewById(R.id.gradeEntry);
        waterTemp = view.findViewById(R.id.tempEntry);
        tempDate = view.findViewById(R.id.dateWaterEntry);

        error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        if(error_icon !=null){
            error_icon.setBounds(0, 0, error_icon.getIntrinsicWidth(), error_icon.getIntrinsicHeight());
        }
    }

    /**
     * validate if all mandatory fields are filled. Name must be fields and
     * address and/or longitude must be filled.
     * @return true if mandatory fields are filled else false
     */
    public boolean validateMandatoryField(){
        String nameText = name.getText().toString();
        String addressText = address.getText().toString();
        String longitudeText  = longitude.getText().toString();
        String latitudeText = latitude.getText().toString();

        //If name and address are entered, the entries are valid
        if(!nameText.equals("") && !addressText.equals("")){
            return true;
        }
        //If name latitude and longitude are entered, the entries are valid
        if(!nameText.equals("") && !latitudeText.equals("") && !longitudeText.equals("")){
            return true;
        }

        //Drawable error_icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_info_24);
        //if the name field is empty, display error icon and message
        if(nameText.equals("")){
            name.setError(getString(R.string.name), error_icon);
        }
        //if the address field is empty, display error icon and message
        if(addressText.equals("")){
            address.setError(getString(R.string.address), error_icon);
        }
        //if the latitude field is empty, display error icon and message
        if(latitudeText.equals("")){
            latitude.setError(getString(R.string.latitude), error_icon);
        }
        //if the longitude field is empty, display error icon and message
        if(longitudeText.equals("")){
            longitude.setError(getString(R.string.longitude), error_icon);
        }
        return false;
    }

    public boolean saveEnteredInformation(){
        if(validateMandatoryField()){
            //TODO: Implement
            Context context = requireContext();
            CharSequence text = "Name: " + name.getText() + "\n"
                    + "Description: " + description.getText() + "\n"
                    + "Address: " + address.getText() + "\n"
                    + "Latitude: " + latitude.getText() + "\n"
                    + "Longitude: " + longitude.getText() + "\n"
                    + "Grade: " + grade.getRating();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return true;
        }
        return false;
    }

    /**
     * clear all editText text and rating
     */
    public  void clearEntryFields(){
        name.getText().clear();
        description.getText().clear();
        address.getText().clear();
        latitude.getText().clear();
        longitude.getText().clear();
        grade.setRating(0f);
        waterTemp.getText().clear();
        tempDate.getText().clear();
    }
}