package se.miun.holi1900.dt031g.bathingsites;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BathingSitesView extends ConstraintLayout implements View.OnClickListener {
    private static final String TAG = "BathingSitesView";
    TextView textView; // textview that display text with number of bathing sites
    int counter = 0;// number of bathing sites

    /**
     * Interface definition for a callback to be invoked when the BathingSitesView is
     * is clicked.
     */
    public interface OnClickedListener{
        void onClick(BathingSitesView dialButtonView);
    }

    /**
     * Listener used to dispatch click events to.
     */
    private  BathingSitesView.OnClickedListener listener;

    /**
     * Register a callback to be invoked when the bathing sites fragment is clicked.
     *
     * @param listener The callback that will run when this view is clicked
     */
    public void setOnClickedListener(OnClickedListener listener){
        this.listener = listener;
    }

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
        setOnClickListener(this);


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
        Log.d(TAG, "setTextViewText: " + counter);
        textView.setText(text);

    }

    /**
     * sets the value of the counter which corresponds to number of bathing sites
     * @param number new number of bathing sites
     */
    public void setCounter(int number){
        Log.d(TAG, "setCounter: " +number);
        String text = number + " bathing sites";
        textView.setText(text);
        invalidate();
        requestLayout();

    }

    /**
     * Gets the value of the counter which corresponds to number of bathing sites
     * @return the value of counter
     */
    public  int getCounter(){
        return counter;
    }

    /**
     * overrides the onClick method of the OnClickListener class
     * on click the button animated with a zoomed in animation and plays sound that says the title of
     * the button
     * @param view view
     */
    @Override
    public void onClick(View view) {
        //When this button is clicked, call the dial button's  custom onClickedListener if one is set.
        if(listener!=null){
            listener.onClick(this);
        }
    }

}
