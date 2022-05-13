package se.miun.holi1900.dt031g.bathingsites;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BathingSitesView extends ConstraintLayout {
    TextView textView; // textview that display text with number of bathing sites
    int counter = 0;// number of bathing sites

    /**
     * Constructor to use when creating a DialButtonView from code.
     *
     * @param context context the BathingSitesView is running in
     */
    public BathingSitesView(@NonNull Context context) {

        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a DialButtonView from XML.
     *
     * @param context context the BathingSitesView is running in
     * @param attrs he attributes of the XML tag that is inflating the view.
     */
    public BathingSitesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Initializes the BathingSitesView when it is created
     * @param context context the BathingSitesView is running in
     */
    public void init(Context context){
        inflate(context, R.layout.view_bathing_sites, this);

        //Get reference to the textView and the imageView
        textView = findViewById(R.id.bathSiteViewText);
        ImageView image = findViewById(R.id.bathImage);

        this.setOnClickListener(view -> {
            counter = counter + 1;
            setTextViewText();
        });

        //set the text of the bathing site view with
        // the current number of bathing sites
        setTextViewText();
    }

    /**
     * sets the text of the bathing site updating the
     * number of bathing sites with the current value of
     * the counter
     */
    public void setTextViewText(){
        String text = counter + " bathing sites";
        textView.setText(text);

    }

    /**
     * sets the value of the counter which corresponds to number of bathing sites
     * @param number new number of bathing sites
     */
    public void setCounter(int number){
        counter = number;

    }

    /**
     * Gets the value of the counter which corresponds to number of bathing sites
     * @return the value of counter
     */
    public  int getCounter(){
        return counter;
    }

}
