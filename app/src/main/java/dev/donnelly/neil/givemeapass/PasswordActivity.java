package dev.donnelly.neil.givemeapass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity
                                implements AddServiceDialog.ServiceCreationListener{

    String FILENAME = "service_hash_map_file";
    HashMap<String,Service> serviceHM = new HashMap<String,Service>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    //The AddServiceDialog receives this Activity on attach and class the following methods
    // through the ServiceCreationListener fragment
    @Override
    public void onAddServiceDialogPosClick(DialogFragment dialog){

    }

    //The user no longer wants to add a service so just cancel the dialog
    @Override
    public void onAddServiceDialogNegClick(DialogFragment dialog){
        dialog.getDialog().cancel();
    }

}
