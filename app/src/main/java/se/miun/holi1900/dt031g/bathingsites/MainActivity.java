package se.miun.holi1900.dt031g.bathingsites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        inflater.inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.settings_option){
            startActivity(new Intent(this, SettingsActivity.class));
            return  true;
        }
        if(item.getItemId()==R.id.download_option){
            startActivity(new Intent(this, DownloadActivity.class));
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**Called when the Call List button is clicked*/
    public void displayAddBathingSiteActivity(View view) {
        startActivity(new Intent(this, AddBathingSiteActivity.class));
    }
}