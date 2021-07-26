package com.trile.walletnote.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.Services.DetailFragmentService;
import com.trile.walletnote.Services.DetailFragmentServiceImpl;
import com.trile.walletnote.Services.HandleMoneyFormat;
import com.trile.walletnote.Services.HandleMoneyFormatImpl;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.DurationPrefs;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    LinearLayout mainLay;

    Spinner reasonSpinner, durationSpinner;
    EditText amountTxt, detailTxt;
    TextView dateTxt;

    RadioGroup radioGroup;
    RadioButton incomeRadio, outgoRadio;

    ImageView imgView;
    AppCompatImageButton cameraBtn, imgDeleteBtn;

    AppCompatButton updateFinishBtn, deleteCancelBtn;

    ArrayList<String> ReasonSpinnerListValue, DurationSpinnerListValue;

    HandleMoneyFormat moneyFormat;
    ChangeFormatDateService changeFormatDateService;

    ReasonPrefs reasonPrefs;
    DurationPrefs durationPrefs;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;

    CustomDialog customDialog;

    boolean currentPeriodType; //true - normal, false - period
    boolean currentEditMode = false;

    int screenWidth;

    FinancialInformation tempInfo;

    DetailFragmentService detailFragmentService;

    AlertDialog deleteDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        FinancialInformation info = (FinancialInformation) intent.getSerializableExtra("objectFin");
        currentPeriodType = intent.getBooleanExtra("currentPeriodType",true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        setupComponent();

        fillContent(info);

        setUpBtns(info);
    }

    void setupComponent(){

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.super.onBackPressed();
            }
        });

        mainLay = findViewById(R.id.detail_main_layout);

        reasonSpinner = findViewById(R.id.detail_reason_spinner);
        durationSpinner = findViewById(R.id.detail_duration_spinner);

        amountTxt = findViewById(R.id.detail_amount_editTxt);
        detailTxt = findViewById(R.id.detail_detail_editTxt);

        dateTxt = findViewById(R.id.detail_date_txt);

        imgView = findViewById(R.id.detail_image_result);
        cameraBtn = findViewById(R.id.detail_detail_img_btn);
        imgDeleteBtn = findViewById(R.id.detail_delete_detail_img_btn);

        updateFinishBtn = findViewById(R.id.detail_update_finish_btn);
        deleteCancelBtn = findViewById(R.id.detail_delete_cancel_btn);

        radioGroup = findViewById(R.id.detail_radio_group);
        incomeRadio = findViewById(R.id.detail_radio_income);
        outgoRadio = findViewById(R.id.detail_radio_outgo);

        tempInfo = new FinancialInformation();

        detailFragmentService = new DetailFragmentServiceImpl(this);

        moneyFormat = new HandleMoneyFormatImpl(this);
        changeFormatDateService = new ChangeFormatDateServiceImpl(this);

        reasonPrefs = new ReasonPrefs(this);
        durationPrefs = new DurationPrefs(this);
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(this);

        customDialog = new CustomDialogImpl(this);

        reasonSpinner.setEnabled(false);
//        reasonSpinner.setClickable(false);

        durationSpinner.setEnabled(false);
//        durationSpinner.setClickable(false);



        setUpSpinnerValue();

        if(currentPeriodType) {
            mainLay.removeView(durationSpinner);
        }else{
            mainLay.removeView(imgView);
            mainLay.removeView(cameraBtn);
        }
    }

    void fillContent(FinancialInformation info){
        int selectedIndexReason = ReasonSpinnerListValue.indexOf(reasonPrefs.getContentForShowing(Integer.parseInt(info.getReason())));
        reasonSpinner.setSelection(selectedIndexReason);

        if(!currentPeriodType){
            int selectedIndexDuration = DurationSpinnerListValue.indexOf(durationPrefs.getContentForShowing(info.getDurationType()));
            durationSpinner.setSelection(selectedIndexDuration);
        }else{
            if(info.getDetail().getFinDetImage() != null){
                byte[] arrayImg = info.getDetail().getFinDetImage();
                Bitmap bitmapImg = BitmapFactory.decodeByteArray(arrayImg,0,arrayImg.length);

                imgView.getLayoutParams().width = screenWidth * 9/10;
                imgView.getLayoutParams().height = screenWidth ; //(9/10) * (2/3)
                imgView.requestLayout();

                imgView.setImageBitmap(bitmapImg);

            }
        }

        dateTxt.setText(info.getChosenDate());

        amountTxt.setText(moneyFormat.FormatMoneyForShowing(String.valueOf(info.getAmount())));

        if(info.getType())
            radioGroup.check(R.id.detail_radio_outgo);
        else
            radioGroup.check(R.id.detail_radio_income);

        detailTxt.setText(info.getDetail().getFinDetContent());


    }

    private void setUpSpinnerValue(){
        ReasonPrefs reasonPrefs = new ReasonPrefs(getApplicationContext());
        DurationPrefs durationPrefs = new DurationPrefs(getApplicationContext());

        ReasonSpinnerListValue = reasonPrefs.getReasonContentList();
        DurationSpinnerListValue = durationPrefs.getDurationContentList();

        ArrayAdapter<String> ReasonSpinnerValueAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,ReasonSpinnerListValue);
        ReasonSpinnerValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> DurationSpinnerValueAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,DurationSpinnerListValue);
        DurationSpinnerValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        reasonSpinner.setAdapter(ReasonSpinnerValueAdapter);
        durationSpinner.setAdapter(DurationSpinnerValueAdapter);
    }

    void setUpBtns(FinancialInformation info){

        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.datePickDialog(dateTxt);
            }
        });


        updateFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentEditMode) {//update
                    currentEditMode = true;
                    tempInfo = new FinancialInformation();
                    updateFinishBtn.setText(R.string.finish_btn);
                    deleteCancelBtn.setText(R.string.cancel_btn);

                    if(currentPeriodType){
                        cameraBtn.setVisibility(View.VISIBLE);
                        imgDeleteBtn.setVisibility(View.VISIBLE);
                    }else{
                        durationSpinner.setEnabled(true);
                    }

                    reasonSpinner.setEnabled(true);
                    dateTxt.setEnabled(true);
                    amountTxt.setEnabled(true);
                    incomeRadio.setEnabled(true);
                    outgoRadio.setEnabled(true);
                    detailTxt.setEnabled(true);

                    detailTxt.setFocusable(true);
                    amountTxt.setFocusable(true);
                    detailTxt.setFocusableInTouchMode(true);
                    amountTxt.setFocusableInTouchMode(true);

                }else{//save
                    ReturnData returnData = new ReturnData();

                    fillRemainContent(info);

                    if(currentPeriodType){
                        returnData = detailFragmentService.updateFinancialInformation(tempInfo);
                    }else{
                        returnData = periodFinancialInformationPrefs.updatePeriodInfo(tempInfo);
                    }

                    if(returnData.getResult() == 1){
                        Intent intent = new Intent(DetailActivity.this,MainActivity.class);
                        customDialog.updateDeleteSuccessDialog(intent,getString(R.string.custom_save_success_dialog_update_title),getString(R.string.custom_save_success_dialog_update_content),DetailActivity.this);
                    }
                    if(returnData.getResult() == 2)
                        customDialog.warningDialog(getString(R.string.update_fail)+": "+returnData.getMessage());
                }
            }
        });

        deleteCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentEditMode){//delete

                    showDeleteDialog(info.getID(), info.getType());


                }else{ // cancel
                    currentEditMode = false;
                    updateFinishBtn.setText(R.string.update_btn);
                    deleteCancelBtn.setText(R.string.delete_btn);

                    if(currentPeriodType){
                        cameraBtn.setVisibility(View.INVISIBLE);
                        imgDeleteBtn.setVisibility(View.INVISIBLE);
                    }else{
                        durationSpinner.setEnabled(false);
                    }

                    reasonSpinner.setEnabled(false);
                    dateTxt.setEnabled(false);
                    amountTxt.setEnabled(false);
                    incomeRadio.setEnabled(false);
                    outgoRadio.setEnabled(false);
                    detailTxt.setEnabled(false);

                    fillContent(info);

                }
            }
        });


        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int reasonItemSelectPosition, long id) {
                tempInfo.setReason(parent.getItemAtPosition(reasonItemSelectPosition).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        amountTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                String input = editText.getText().toString();
                if(hasFocus){

                    if(input.equals(""))
                        amountTxt.setText(input);
                    else {
                        if (Integer.parseInt(moneyFormat.FormatMoneyForSaving(input)) == 0)
                            amountTxt.setText("");
                        else {
                            String output = moneyFormat.FormatMoneyForSaving(input);
                            amountTxt.setText(output);
                        }
                        amountTxt.setSelection(amountTxt.getText().length());
                    }
                }
                else {
                    if(input.equals("")){
                        amountTxt.setError("Nhap so tien");
                    }else {
                        if(Integer.parseInt(input)!=0) {
                            String output = moneyFormat.FormatMoneyForShowing(input);
                            amountTxt.setText(output);
                            tempInfo.setAmount(Integer.parseInt(moneyFormat.FormatMoneyForSaving(input)));
                        }else {
                            amountTxt.setError("Nhap so tien");

                        }
                    }
                }

            }
        });

        detailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = detailTxt.getText().toString();
                tempInfo.getDetail().setFinDetContent(text);
            }
        });

        dateTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = dateTxt.getText().toString();
                tempInfo.setChosenDate(text);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.detail_radio_income){
                    tempInfo.setType(false);
                }else{
                    tempInfo.setType(true);
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        imgDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempInfo.getDetail().setFinDetImage(null);
                imgView.setImageBitmap(null);
                imgView.getLayoutParams().height = 0;
                imgView.requestLayout();
            }
        });



    }

    void fillRemainContent(FinancialInformation info){ // fill other component for saving
        tempInfo.setID(info.getID());

        if(tempInfo.getType() == null)
            tempInfo.setType(info.getType());

        if(tempInfo.getChosenDate() == null)
            tempInfo.setChosenDate(info.getChosenDate());

        if(tempInfo.getAmount() == 0)
            tempInfo.setAmount(info.getAmount());

        if(tempInfo.getReason()== null)
            tempInfo.setReason(info.getReason());

        tempInfo.getDetail().setFinDetId(info.getDetail().getFinDetId());

        FinancialDetail detail = tempInfo.getDetail();
        if(detail.getFinDetImage() == null)
            tempInfo.getDetail().setFinDetImage(info.getDetail().getFinDetImage());

        if(detail.getFinDetContent()==null)
            tempInfo.getDetail().setFinDetContent(info.getDetail().getFinDetContent());

        if (!currentPeriodType)
            if(tempInfo.getDurationType() == 0)
                tempInfo.setDurationType(info.getDurationType());
    }

    private void showDeleteDialog(int id, boolean addNewType) {
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View deleteDialogView = inflater.inflate(R.layout.custom_delete_dialog_layout,null);
        deleteDialogBuilder.setView(deleteDialogView);

        TextView deleteDialogTitle = deleteDialogView.findViewById(R.id.custom_delete_dialog_title);
        deleteDialogTitle.setText(R.string.custom_delete_dialog_title);

        TextView deleteDialogContent = deleteDialogView.findViewById(R.id.custom_delete_dialog_content);
        deleteDialogContent.setMovementMethod(new ScrollingMovementMethod());
        deleteDialogContent.setText(R.string.custom_delete_dialog_message);

        AppCompatButton deleteDialogDeleteBtn= deleteDialogView.findViewById(R.id.custom_delete_dialog_delete_btn);
        deleteDialogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteDialogDeleteButton(id, addNewType);
            }
        });

        AppCompatButton deleteDialogCancelBtn= deleteDialogView.findViewById(R.id.custom_delete_dialog_cancel_btn);
        deleteDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog = deleteDialogBuilder.create();

        deleteDialog.show();
    }

    void clickDeleteDialogDeleteButton(int id, boolean addNewType){
        ReturnData deleteReturnData = new ReturnData();
        if(currentPeriodType)
            deleteReturnData = detailFragmentService.deleteFinancialInformation(id);
        else
            deleteReturnData = detailFragmentService.deletePeriodicFinancialInformation(id, addNewType);

        deleteDialog.dismiss();

        if(deleteReturnData.getResult() == 1){
            Intent intent = new Intent(DetailActivity.this,MainActivity.class);
            customDialog.updateDeleteSuccessDialog(intent, getString(R.string.custom_save_success_dialog_delete_title), getString(R.string.custom_save_success_dialog_delete_content),DetailActivity.this);
        }else{
            customDialog.warningDialog(getString(R.string.delete_fail)+": "+deleteReturnData.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //add to object
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] imgArray = stream.toByteArray();

            tempInfo.getDetail().setFinDetImage(imgArray);
            imgView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onBackPressed() {
        //do not thing
    }
}