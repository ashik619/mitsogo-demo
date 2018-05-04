package in.ashik619.mitsogodemo.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import in.ashik619.mitsogodemo.R;

/**
 * Created by dilip on 3/5/18.
 */

public class IntervalDialog extends DialogFragment {

    private OnIntervalConfirmListner listner;
    private Context context = null;

    @SuppressLint("ValidFragment")
    public IntervalDialog() {
    }

    @SuppressLint("ValidFragment")
    public IntervalDialog(OnIntervalConfirmListner listner){
        this.listner = listner;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        context = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_interval, container,
                false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_rounded_corner);
        final EditText hourText = (EditText) rootView.findViewById(R.id.hourText);
        final EditText minText = (EditText) rootView.findViewById(R.id.minText);
        final Button confirm = (Button) rootView.findViewById(R.id.ok);
        final Button cancel = (Button) rootView.findViewById(R.id.cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _hour = hourText.getText().toString();
                String _min = minText.getText().toString();
                if (TextUtils.isEmpty(_hour)){
                    Toast.makeText(context,"Hour cant be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(_min)){
                    Toast.makeText(context,"Minutes cant be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    listner.onIntervalConfirmed(Integer.parseInt(_hour),Integer.parseInt(_min));
                    dismiss();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootView;
    }
    public interface OnIntervalConfirmListner{
        void onIntervalConfirmed(int hour, int min);
    }
}
