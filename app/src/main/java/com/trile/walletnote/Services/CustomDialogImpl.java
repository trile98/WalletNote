package com.trile.walletnote.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.trile.walletnote.Activities.MainActivity;
import com.trile.walletnote.R;
import com.trile.walletnote.model.FinancialInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomDialogImpl implements CustomDialog{

    Context context;
    AlertDialog updateDeleteSuccess, saveSuccess, total, warning;

    public CustomDialogImpl(Context context){
        this.context = context;
    }

    @Override
    public void warningDialog(String message) {
        AlertDialog.Builder warningBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customWaringDialogView = inflater.inflate(R.layout.custom_warning_dialog_layout,null);
        warningBuilder.setView(customWaringDialogView);

        TextView waringTitle = customWaringDialogView.findViewById(R.id.waring_dialog_title);
        waringTitle.setText(R.string.custom_dialog_warning_title);

        TextView warningContent = customWaringDialogView.findViewById(R.id.waring_dialog_content);
        warningContent.setMovementMethod(new ScrollingMovementMethod());
        warningContent.setText(message);

        AppCompatButton cancelBtn = customWaringDialogView.findViewById(R.id.custom_warning_dialog_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.dismiss();
            }
        });

        warning = warningBuilder.create();
        warning.show();
    }

    @Override
    public void datePickDialog(final TextView masterView, FinancialInformation item) {
        Calendar calendar = Calendar.getInstance();

        int date, month, year;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


        String currentDateTxtValue = masterView.getText().toString();

        if(currentDateTxtValue != null) {
            try {
                calendar.setTime(format.parse(currentDateTxtValue));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        date = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);


                masterView.setText(format.format(calendar.getTime()));

                item.setChosenDate(format.format(calendar.getTime()));
            }
        },year,month,date);


        datePicker.show();
    }

    @Override
    public void totalDialog(String totalString) {
        AlertDialog.Builder totalDialogBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View totalDialogView = inflater.inflate(R.layout.custom_warning_dialog_layout,null);
        totalDialogBuilder.setView(totalDialogView);

        TextView totalDialogTitle = totalDialogView.findViewById(R.id.waring_dialog_title);
        totalDialogTitle.setText(R.string.custom_dialog_total_title);

        TextView TotalDialogContent = totalDialogView.findViewById(R.id.waring_dialog_content);
        TotalDialogContent.setMovementMethod(new ScrollingMovementMethod());
        TotalDialogContent.setText(totalString);

        AppCompatButton cancelBtn = totalDialogView.findViewById(R.id.custom_warning_dialog_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total.dismiss();
            }
        });

        total = totalDialogBuilder.create();
        total.show();
    }


    @Override
    public void updateDeleteSuccessDialog(Intent intent, String title, String content, Activity currentActivity) {

        AlertDialog.Builder updateDeleteSuccessDialogBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View updateDeleteSuccessDialogView = inflater.inflate(R.layout.custom_dialog_save_success_layout,null);
        updateDeleteSuccessDialogBuilder.setView(updateDeleteSuccessDialogView);

        TextView updateDeleteTitle = updateDeleteSuccessDialogView.findViewById(R.id.custom_dialog_save_success_title);
        updateDeleteTitle.setText(title);

        TextView updateDeleteContent = updateDeleteSuccessDialogView.findViewById(R.id.custom_dialog_save_success_content);
        updateDeleteContent.setMovementMethod(new ScrollingMovementMethod());
        updateDeleteContent.setText(content);

        AppCompatButton cancelBtn = updateDeleteSuccessDialogView.findViewById(R.id.custom_dialog_save_success_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity.finish();
                context.startActivity(intent);
                updateDeleteSuccess.dismiss();
            }
        });

        updateDeleteSuccess = updateDeleteSuccessDialogBuilder.create();
        updateDeleteSuccess.show();

    }

    @Override
    public void saveSuccessDialog(MainActivity activity) {
        AlertDialog.Builder saveSuccessDialogBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View saveSuccessDialogView = inflater.inflate(R.layout.custom_dialog_save_success_layout,null);
        saveSuccessDialogBuilder.setView(saveSuccessDialogView);

        TextView saveSuccessTitle = saveSuccessDialogView.findViewById(R.id.custom_dialog_save_success_title);
        saveSuccessTitle.setText(R.string.custom_save_success_dialog_save_title);

        TextView saveSuccessContent = saveSuccessDialogView.findViewById(R.id.custom_dialog_save_success_content);
        saveSuccessContent.setMovementMethod(new ScrollingMovementMethod());
        saveSuccessContent.setText(R.string.custom_save_success_dialog_save_content);

        AppCompatButton cancelBtn = saveSuccessDialogView.findViewById(R.id.custom_dialog_save_success_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.replaceFragment(R.id.navigation_home);
                saveSuccess.dismiss();
            }
        });

        saveSuccess = saveSuccessDialogBuilder.create();
        saveSuccess.show();
    }


}
