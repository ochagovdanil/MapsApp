package com.example.mapsapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mapsapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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
