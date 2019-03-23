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

public class PinTitleDialogFragment extends DialogFragment {

    private PinTitleDialogListener mPinTitleDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText editText = new EditText(getContext());
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set a new title")
                .setCancelable(false)
                .setView(editText)
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPinTitleDialogListener.onSavePinTitleDialogListener(editText.getText().toString());
                    }
                });
        return builder.create();
    }

    public void setOnSavePinTitleDialogListener(PinTitleDialogListener pinTitleDialogListener) {
        mPinTitleDialogListener = pinTitleDialogListener;
    }

    public interface PinTitleDialogListener {
        void onSavePinTitleDialogListener(String text);
    }

}
