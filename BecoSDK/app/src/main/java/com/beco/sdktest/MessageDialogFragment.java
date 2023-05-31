package com.beco.sdktest;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {

    private String mMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Object message = getArguments().get("Message");

        if (message != null) {
            if (message instanceof String) {
                mMessage = (String) message;
            }
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (mMessage != null) {
            builder.setMessage(mMessage);
        }
        builder.setNeutralButton("OK", (dialog, id) -> {
        });
        return builder.create();
    }
}