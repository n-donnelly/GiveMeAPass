package dev.donnelly.neil.givemeapass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class AddServiceDialog extends DialogFragment {

    public interface ServiceCreationListener {
        public void onAddServiceDialogPosClick(Service service);
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

        final View dView =  inflater.inflate(R.layout.content_add_service, null);

        Spinner spinner = (Spinner) dView.findViewById(R.id.num_char_field);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(dView.getContext(),
                R.array.pass_length_array, R.layout.pass_spinner_item);

        adapter.setDropDownViewResource(R.layout.pass_spinner_dropdown);
        spinner.setAdapter(adapter);

        builder.setView(dView)
                .setPositiveButton(R.string.dialog_add_service, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){

                        EditText serviceText = (EditText)dView.findViewById(R.id.service_text);
                        String service = serviceText.getText().toString();

                        Spinner numCharSpin = (Spinner) dView.findViewById(R.id.num_char_field);
                        int num_char = Integer.parseInt(numCharSpin.getSelectedItem().toString());

                        boolean want_specs = ((CheckBox) dView.findViewById(R.id.incl_spec_check))
                                .isChecked();

                        boolean want_nums = ((CheckBox) dView.findViewById(R.id.incl_nums_check))
                                .isChecked();

                        Service ser = new Service(service,num_char,want_specs,want_nums);
                        mListener.onAddServiceDialogPosClick(ser);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AddServiceDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
