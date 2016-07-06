package dev.donnelly.neil.givemeapass;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
                android.R.layout.simple_spinner_item,
                serviceNames);

        hashedPhrase = (String) getIntent().getExtras().get(getString(R.string.phrase_prefs_file));

        serviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    //The AddServiceDialog receives this Activity on attach and class the following methods
    // through the ServiceCreationListener fragment
    @Override
    public void onAddServiceDialogPosClick(Service service) {
        serviceMapWrapper.addService(service,this);

        serviceNames.add(service.getName());

        serviceArrayAdapter.notifyDataSetChanged();

    }
    public void onGenerateBtnPress(View view){

        EditText editText = (EditText) findViewById(R.id.password_text);
        String password = editText.getText().toString();

        Service service = serviceHM.get(serviceSpinner.getSelectedItem().toString());
        String generatedPassword = passGen.buildPassword(password,
                service.getName(),
                hashedPhrase,
                service.getNumCharacters(),
                service.doesWantSpecialChars(),
                service.doesWantNumbers());

        EditText passwordView = (EditText) findViewById(R.id.gen_pass_field);
        passwordView.setText(generatedPassword, TextView.BufferType.NORMAL);
        passwordView.setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.gen_pass_label);
        tv.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

}
