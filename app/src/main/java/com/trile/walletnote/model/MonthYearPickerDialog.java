package com.trile.walletnote.model;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.trile.walletnote.R;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {
    private static final int MAX_YEAR = 2099;

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    int chosenMonth,chosenYear;
    TextView filterTxt;

    public  MonthYearPickerDialog(TextView filterTxt){
        this.filterTxt = filterTxt;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuiler = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.month_year_picker_layout, null);
        NumberPicker monthPicker = dialogView.findViewById(R.id.month_year_picker_month_picker);
        NumberPicker yearPicker = dialogView.findViewById(R.id.month_year_picker_year_picker);
        TextView dialogTitle = dialogView.findViewById(R.id.month_year_picker_title);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(MAX_YEAR);

        Calendar calendar = Calendar.getInstance();

        if(chosenMonth == 0 && chosenYear == 0) {
            //calendar month is 0-index, so it should add 1 more to show correct month.
            chosenMonth = calendar.get(Calendar.MONTH)+1;
            chosenYear = calendar.get(Calendar.YEAR);
        }

        monthPicker.setValue(chosenMonth);
        yearPicker.setValue(chosenYear);

        dialogTitle.setText(getString(R.string.month_year_picker_title,chosenMonth,chosenYear));

        AppCompatButton acceptBtn = dialogView.findViewById(R.id.month_year_picker_accept_btn);
        AppCompatButton cancelBtn = dialogView.findViewById(R.id.month_year_picker_cancel_btn);
        AppCompatButton deleteFilterBtn = dialogView.findViewById(R.id.month_year_picker_delete_filter_btn);

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                chosenMonth = newVal;
                dialogTitle.setText(getString(R.string.month_year_picker_title,chosenMonth,chosenYear));
            }
        });

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                chosenYear = newVal;
                dialogTitle.setText(getString(R.string.month_year_picker_title,chosenMonth,chosenYear));
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null,yearPicker.getValue(),monthPicker.getValue(),0);
                MonthYearPickerDialog.this.getDialog().dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog.this.getDialog().dismiss();
            }
        });

        deleteFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTxt.setText("");
                chosenMonth =0;
                chosenYear = 0;
                MonthYearPickerDialog.this.getDialog().dismiss();
            }
        });

        dialogBuiler.setView(dialogView);

        return dialogBuiler.create();
    }
}
