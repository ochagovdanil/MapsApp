package com.example.mapsapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mapsapp.R;

public class PinEditDialogFragment extends DialogFragment {

    private PinEditDialogListener mPinEditDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText editText = new EditText(getContext());
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);
        editText.setText(getArguments().getString("marker_title", "New marker"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change title")
                .setCancelable(false)
                .setView(editText)
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPinEditDialogListener.onSavePinEditDialogListener(editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    public void setOnSavePinEditDialogListener(PinEditDialogListener pinEditDialogListener) {
        mPinEditDialogListener = pinEditDialogListener;
    }

    public interface PinEditDialogListener {
        void onSavePinEditDialogListener(String text);
    }

}
