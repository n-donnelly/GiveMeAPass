package dev.donnelly.neil.givemeapass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

public class AddServiceDialog extends DialogFragment {

    public interface ServiceCreationListener {
        public void onAddServiceDialogPosClick(DialogFragment dialog);
        public void onAddServiceDialogNegClick(DialogFragment dialog);
    }

    ServiceCreationListener mListener;

    @Override
    public void onAttach(Context context){
        //May need to change this - example uses onAttach with Activity not Context - so check here
        //if something is going wrong and make sure this is working as expected
        super.onAttach(context);
        Activity activity = new Activity();
        if(context instanceof Activity){
            activity = (Activity) context;
        }

        try {
            mListener = (ServiceCreationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceCreationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_add_service, null))
                .setPositiveButton(R.string.dialog_add_service, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        mListener.onAddServiceDialogPosClick(AddServiceDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAddServiceDialogNegClick(AddServiceDialog.this);
                    }
                });
        return builder.create();
    }

}
