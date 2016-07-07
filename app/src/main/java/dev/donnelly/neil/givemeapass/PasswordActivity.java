package dev.donnelly.neil.givemeapass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity
                                implements AddServiceDialog.ServiceCreationListener{

    String FILENAME = "service_hash_map_file";
    HashMap<String,Service> serviceHM = new HashMap<String,Service>();
    HashMapWrapper serviceMapWrapper = new HashMapWrapper();
    ArrayAdapter<String> serviceArrayAdapter;
    Spinner serviceSpinner;
    PasswordGenerator passGen = new PasswordGenerator();
    String hashedPhrase;
    ArrayList<String> serviceNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serviceHM = serviceMapWrapper.getServiceMap(this);
        serviceNames.addAll(serviceHM.keySet());
        serviceArrayAdapter = new ArrayAdapter(this,
                R.layout.pass_spinner_item,
                serviceNames);

        hashedPhrase = (String) getIntent().getExtras().get(getString(R.string.phrase_prefs_file));

        serviceArrayAdapter.setDropDownViewResource(R.layout.pass_spinner_dropdown);
        serviceSpinner = (Spinner) findViewById(R.id.service_spinner);
        serviceSpinner.setAdapter(serviceArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment serviceDialog = new AddServiceDialog();
                serviceDialog.show(getSupportFragmentManager(), "services");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_phrase, menu);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_passphrase) {
            Intent intent = new Intent(this, PassPhraseActivity.class);
            intent.putExtra("ManualChange",true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //The AddServiceDialog receives this Activity on attach and class the following methods
    // through the ServiceCreationListener fragment
    @Override
    public void onAddServiceDialogPosClick(Service service) {
        if(serviceMapWrapper.addService(service,this)){
            serviceNames.add(service.getName());
            serviceArrayAdapter.notifyDataSetChanged();
        }

    }

    //Generates the password for the given password and service - includes pass phrase
    public void onGenerateBtnPress(View view){

        EditText editText = (EditText) findViewById(R.id.password_text);
        String password = editText.getText().toString();

        if(password.equals(""))
            return;

        Service service = serviceHM.get(serviceSpinner.getSelectedItem().toString());
        String generatedPassword = passGen.buildPassword(password,
                service.getName(),
                hashedPhrase,
                service.getNumCharacters(),
                service.doesWantSpecialChars(),
                service.doesWantNumbers());

        //Write the generated password to EditText and reveal the label and field
        EditText passwordView = (EditText) findViewById(R.id.gen_pass_field);
        passwordView.setText(generatedPassword, TextView.BufferType.NORMAL);
        passwordView.setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.gen_pass_label);
        tv.setVisibility(View.VISIBLE);

        //Hide the keyboard once done
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

    }

}
