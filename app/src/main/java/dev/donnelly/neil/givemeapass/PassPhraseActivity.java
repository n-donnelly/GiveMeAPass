package dev.donnelly.neil.givemeapass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;

public class PassPhraseActivity extends AppCompatActivity {
    PassPhraseController ppController = new PassPhraseController();
    TextView.OnEditorActionListener enterListener;
    String pass_phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_phrase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Restore Preferences
        SharedPreferences settings = getSharedPreferences(getString(R.string.phrase_prefs_file),0);
        pass_phrase = settings.getString("passphrase","");

        Bundle extras = getIntent().getExtras();
        boolean wantManualChange = false;
        if(extras!=null) {
            wantManualChange = (boolean) extras.get("ManualChange");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(pass_phrase != "" && !wantManualChange){
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra(getString(R.string.phrase_prefs_file),pass_phrase);
            startActivity(intent);
        } else {
            enterListener = new TextView.OnEditorActionListener(){
                public boolean onEditorAction(TextView tv, int actionId, KeyEvent event){
                    if(actionId == EditorInfo.IME_NULL
                            && event.getAction() == KeyEvent.ACTION_DOWN){
                        onPassSubmit(tv);
                    }
                    return true;
                }
            };
            EditText et = (EditText)findViewById(R.id.editPassPhrase);
            et.setOnEditorActionListener(enterListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            if(pass_phrase == null)
                return false;
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra(getString(R.string.phrase_prefs_file),pass_phrase);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPassSubmit(View view){
        //Restore Preferences
        EditText tv = (EditText)findViewById(R.id.editPassPhrase);
        String passPhrase = tv.getText().toString();
        if (passPhrase.equals(""))
            return;

        String hashedPhrase = "";

        try {
            hashedPhrase = ppController.hashPassPhrase(passPhrase);
        } catch (NoSuchAlgorithmException e){

        } finally {
            if (hashedPhrase.equals(""))
                return;
            SharedPreferences settings = getSharedPreferences(getString(R.string.phrase_prefs_file),
                    0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("passphrase",hashedPhrase);
            editor.commit();
        }

        Intent intent = new Intent(this, PasswordActivity.class);
        intent.putExtra(getString(R.string.phrase_prefs_file),hashedPhrase);
        startActivity(intent);
    }
}
