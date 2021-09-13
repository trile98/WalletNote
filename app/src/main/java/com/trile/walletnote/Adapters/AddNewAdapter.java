package com.trile.walletnote.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.Services.HandleMoneyFormat;
import com.trile.walletnote.Services.HandleMoneyFormatImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.trile.walletnote.sharePreferencces.DurationPrefs;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

import java.util.ArrayList;

public class AddNewAdapter extends BaseAdapter {

    Context context;
    ArrayList<FinancialInformation> FinList;
    int layout;
    ArrayList<String> ReasonSpinnerListValue,DurationSpinnerListValue;
    ArrayAdapter<String> ReasonSpinnerValueAdapter,DurationSpinnerValueAdapter;
    public boolean checkboxMode = false;
    public ArrayList<FinancialInformation> listFinListItemChecked;
    Fragment currentFragment;


    ReasonPrefs reasonPrefs;
    DurationPrefs durationPrefs;
    CurrentStatusPrefs currentStatusPrefs;

    static int checkboxId = View.generateViewId();
    static int itemCheckLayId = View.generateViewId();

    CustomDialog customDialog;
    HandleMoneyFormat moneyFormat;

    public EditText currentAmountEditTxt, currentDetailEditTxt;
    public int currentImagePosition;

    public ArrayList<Integer> listAmountForCheckEmpty;

    public AddNewAdapter(int l, ArrayList<FinancialInformation> list, Fragment fragment){
        currentFragment = fragment;
        context = fragment.getContext();
        FinList = list;
        layout = l;
        reasonPrefs = new ReasonPrefs(this.context);
        durationPrefs = new DurationPrefs(this.context);
        currentStatusPrefs = new CurrentStatusPrefs(this.context);

        listFinListItemChecked = new ArrayList<>();

        customDialog = new CustomDialogImpl(context);
        moneyFormat = new HandleMoneyFormatImpl(context);

        listAmountForCheckEmpty = new ArrayList<Integer>();
        listAmountForCheckEmpty.add(0);

        setUpSpinnerValue();
    }

    private void setUpSpinnerValue(){
        ReasonSpinnerListValue = reasonPrefs.getReasonContentList();
        DurationSpinnerListValue = durationPrefs.getDurationContentList();

        ReasonSpinnerValueAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,ReasonSpinnerListValue);
        ReasonSpinnerValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        DurationSpinnerValueAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,DurationSpinnerListValue);
        DurationSpinnerValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public int getCount() {
        return FinList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        Spinner reasonSpinner,durationSprinner;
        TextView dateTxtView;
        EditText amountEditTxt, detailEditTxt;
        AppCompatCheckBox checkBox;
        RelativeLayout parentLay, itemCheckLayout;
        LinearLayout headerLay,bodyLay, imageLayout, mainLay;
        AppCompatImageButton imageBtn, imagedeleteBtn;
        AppCompatImageView imgView;
    }

    //if notifyDataChange run, list view will load again and this function is called from beginning
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String durationStatus = currentStatusPrefs.getDurationStatus();

        final ViewHolder holder;
            //get view
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layout, null);

                holder = new ViewHolder();

                holder.reasonSpinner = convertView.findViewById(R.id.addNew_reason_spinner);
                holder.durationSprinner = convertView.findViewById(R.id.addNew_duration_spinner);
                holder.dateTxtView = convertView.findViewById(R.id.addNew_date_txt);
                holder.amountEditTxt = convertView.findViewById(R.id.addNew_amount_editTxt);
                holder.detailEditTxt = convertView.findViewById(R.id.addNew_detail_editTxt);
                holder.bodyLay = convertView.findViewById(R.id.addNew_item_body_layout);
                holder.headerLay = convertView.findViewById(R.id.addNew_item_header_layout);
                holder.parentLay = convertView.findViewById(R.id.addNew_item_layout);
                holder.imageLayout = convertView.findViewById(R.id.addnew_image_layout);
                holder.imageBtn = convertView.findViewById(R.id.addNew_detail_img_btn);
                holder.imagedeleteBtn = convertView.findViewById(R.id.addnew_detail_img_delete);
                holder.imgView = (AppCompatImageView) convertView.findViewById(R.id.addnew_image_result);
                holder.mainLay = convertView.findViewById(R.id.addNew_listview_item_layout);

                holder.itemCheckLayout = convertView.findViewById(R.id.addnew_listview_item_check_layout);
                holder.checkBox = convertView.findViewById(R.id.addnew_listview_item_checkbox);

                // set duration spinner visible

                if (durationStatus.equals("normal")) {
                    holder.bodyLay.removeView(holder.durationSprinner);
                }else{
                    holder.bodyLay.removeView(holder.imageLayout);
                }

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            //fill data
            FinancialInformation FinInfo = FinList.get(position);

            int height = holder.mainLay.getHeight();
            ViewGroup.LayoutParams params = holder.itemCheckLayout.getLayoutParams();
            params.height = height;

            holder.itemCheckLayout.setLayoutParams(params);

            convertView.requestLayout();

            holder.reasonSpinner.setAdapter(ReasonSpinnerValueAdapter);
            holder.durationSprinner.setAdapter(DurationSpinnerValueAdapter);

            int selectedIndexReason = ReasonSpinnerListValue.indexOf(FinInfo.getReason());
            holder.reasonSpinner.setSelection(selectedIndexReason);

            currentAmountEditTxt = holder.amountEditTxt;
            currentDetailEditTxt = holder.detailEditTxt;

            holder.dateTxtView.setText(FinInfo.getChosenDate());
            holder.dateTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.datePickDialog(holder.dateTxtView, FinInfo);
                }
            });

            holder.amountEditTxt.setText(moneyFormat.FormatMoneyForShowing(String.valueOf(FinInfo.getAmount())));

            if (FinInfo.getDetail() != null) {
                holder.detailEditTxt.setText(FinInfo.getDetail().getFinDetContent());
            } else {
                holder.detailEditTxt.setText("");
            }

            holder.imgView.setTag(FinInfo);
            holder.imgView.setId(position);
            if(FinInfo.getDetail().getFinDetImage()!= null){

                byte[] arrayImg = FinInfo.getDetail().getFinDetImage();
                Bitmap bitmapImg = BitmapFactory.decodeByteArray(arrayImg,0,arrayImg.length);
                holder.imgView.setImageBitmap(bitmapImg);
                updateCheckLayoutHeight(holder,convertView);
            }else{
                holder.imgView.setImageBitmap(null);
            }

            holder.imageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    currentImagePosition = position;

                    currentFragment.startActivityForResult(intent, 0);
                }
            });

            holder.imagedeleteBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(holder.imgView.getDrawable()!= null){
                            holder.imgView.setImageBitmap(null);
                            FinInfo.getDetail().setFinDetImage(null);
                            notifyDataSetChanged();
                        }
                    }
                });

            if (checkboxMode == true) {
                holder.itemCheckLayout.setVisibility(View.VISIBLE);
            } else {
                FinInfo.setSelectValue(false);
                holder.itemCheckLayout.setVisibility(View.INVISIBLE);
            }

            holder.checkBox.setTag(FinInfo);
            holder.checkBox.setId(position);

            holder.checkBox.setChecked(FinInfo.isSelectValue());

            if (durationStatus.equals("period")) {
                int selectedIndexDuration = DurationSpinnerListValue.indexOf(durationPrefs.getContentForShowing(FinInfo.getDurationType()));
                holder.durationSprinner.setSelection(selectedIndexDuration);

                holder.durationSprinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int durationItemSelectPosition, long id) {
                        FinList.get(position).setDurationType(durationPrefs.getDurationIdForSaving(parent.getItemAtPosition(durationItemSelectPosition).toString()));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            updateListAfterTextChange(holder, position, convertView);

        return convertView;
    }

    void updateCheckLayoutHeight(ViewHolder h, View rowView){
//        int height = h.headerLay.getHeight() + h.bodyLay.getHeight()+10;
//        ViewGroup.LayoutParams params = h.itemCheckLayout.getLayoutParams();
//
//        params.height = height;
//
//        h.itemCheckLayout.setLayoutParams(params);
//        h.itemCheckLayout.requestLayout();

        int height = h.mainLay.getHeight();
        ViewGroup.LayoutParams params = h.itemCheckLayout.getLayoutParams();
        params.height = height;

        h.itemCheckLayout.setLayoutParams(params);

        rowView.requestLayout();
    }

    void updateListAfterTextChange(final ViewHolder holder, final int pos, View rowView){
        holder.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int reasonItemSelectPosition, long id) {
                FinList.get(pos).setReason(parent.getItemAtPosition(reasonItemSelectPosition).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.amountEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                String input = editText.getText().toString();
                if(hasFocus){
                    if(input.equals(""))
                        holder.amountEditTxt.setText(input);
                    else {
                        if (Integer.parseInt(moneyFormat.FormatMoneyForSaving(input)) == 0)
                            holder.amountEditTxt.setText("");
                        else {
                            String output = moneyFormat.FormatMoneyForSaving(input);
                            holder.amountEditTxt.setText(output);
                        }
                        holder.amountEditTxt.setSelection(holder.amountEditTxt.getText().length());
                    }
                }
                else {
                    if(input.equals("")){
                        listAmountForCheckEmpty.set(pos,0);
                        holder.amountEditTxt.setError(context.getString(R.string.add_new_amount_empty_error));
                    }else {
                        if(Integer.parseInt(input)!=0) {
                            listAmountForCheckEmpty.set(pos,Integer.parseInt(input));
                            String output = moneyFormat.FormatMoneyForShowing(input);
                            holder.amountEditTxt.setText(output);
                            FinList.get(pos).setAmount(Integer.parseInt(moneyFormat.FormatMoneyForSaving(input)));
                        }else {
                            listAmountForCheckEmpty.set(pos,0);
                            holder.amountEditTxt.setError(context.getString(R.string.add_new_amount_empty_error));
                        }

                    }
                }

            }
        });

        holder.detailEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String text = holder.detailEditTxt.getText().toString();
                    FinList.get(pos).getDetail().setFinDetContent(text);
                    updateCheckLayoutHeight(holder, rowView);
                }
            }
        });

        holder.itemCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                } else {
                    holder.checkBox.setChecked(true);
                }
            }
        });


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isShown()){
                    if(pos != ListView.INVALID_POSITION) {
                        FinancialInformation item = FinList.get(pos);
                        item.setSelectValue(isChecked);
                        if (isChecked) {
//                            holder.checkBoxLayout.setBackgroundColor(Color.parseColor("#A6D3D3D3"));
                            listFinListItemChecked.add(item);

                            if(item.getDetail().getFinDetImage()!= null)
                                Log.e("image","true"+ pos);

                        } else {
                            if (listFinListItemChecked.contains(item))
                                listFinListItemChecked.remove(item.getID());
                        }
                    }
                }
            }
        });


    }


}
