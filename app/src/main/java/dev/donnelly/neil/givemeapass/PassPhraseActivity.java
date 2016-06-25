package dev.donnelly.neil.givemeapass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PassPhraseActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "GiveMeAPassPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_phrase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Restore Preferences
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String pass_phrase = settings.getString("passphrase","");

        if(pass_phrase != ""){
            Intent intent = new Intent(this, Password.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_phrase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPassSubmit(View view){
        //Restore Preferences
        TextView tv = (TextView)view.findViewById(R.id.editPassPhrase);
        String passPhrase = tv.getText().toString();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("passphrase",passPhrase);
        String pass_phrase = settings.getString("passphrase","");

        Intent intent = new Intent(this, Password.class);
        startActivity(intent);
    }
}
