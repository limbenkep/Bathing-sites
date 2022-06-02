package se.miun.holi1900.dt031g.bathingsites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class is a custom progress dialog that made up of a spinner and a message that is passed
 * through he constructor
 */
public class CustomProgressDialogView extends DialogFragment {
    CharSequence  text;
    CustomProgressDialogView(CharSequence message){
        this.text = message;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.view_custom_progress_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar progressSpinner = view.findViewById(R.id.show_weather_progress_bar);
        progressSpinner.setVisibility(View.VISIBLE);
        TextView textView = view.findViewById(R.id.show_weather_title);
        textView.setText(text);
    }
}
