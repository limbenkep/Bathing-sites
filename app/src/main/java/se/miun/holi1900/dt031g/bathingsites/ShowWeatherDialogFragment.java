package se.miun.holi1900.dt031g.bathingsites;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class is a custom dialog fragment that displays weather information. The fragment displays
 * a  title, a weather icon and  text of the weather information
 * A string containing the weather information and  a drawable containing the weather icon is
 * passed to the constructor
 */
public class ShowWeatherDialogFragment extends DialogFragment {
    private static final String TAG = "ShowWeatherDialogFragment";
    private final String message;
    private final Drawable weatherIcon;

    /**
     * Constructor takes two parameter and set them to message and weather icon
     * @param message passed data information to be displayed
     * @param icon passed weather icon to be displayed
     */
    public ShowWeatherDialogFragment(String message, Drawable icon) {
        super();
        this.message = message;
        this.weatherIcon = icon;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_weather_dialog, container, false);
    }

    /**
     * Overwrites onViewCreated method to set the title, message and image with those pass in
     * the constructor. implement the logic for the ok button
     * @param view view
     * @param savedInstanceState saved instance
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        ImageView image = view.findViewById(R.id.weather_dialog_image);
        image.setImageDrawable(weatherIcon);
        TextView textView = view.findViewById(R.id.weather_dialog_message);
        TextView title = view.findViewById(R.id.weather_dialog_title);
        title.setText(R.string.show_weather_dialog_title);
        title.setTypeface(null, Typeface.BOLD);
        textView.setText(message);
        Button ok_button = view.findViewById(R.id.ok_button);
        ok_button.setOnClickListener(view1 -> this.dismiss());
    }
}
