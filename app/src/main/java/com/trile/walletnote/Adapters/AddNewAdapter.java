package com.trile.walletnote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

    public EditText currentAmountEditTxt;
    public ImageView currentImgView;

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
        LinearLayout headerLay,bodyLay, imageLayout;
        AppCompatImageButton imageBtn;
        ImageView imgView;
    }

    //if notifyDataChange run, list view will load again and this function is called from beginning
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        String durationStatus = currentStatusPrefs.getDurationStatus();

            //get view
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(layout, null);

                ViewHolder viewHolder = new ViewHolder();

                viewHolder.reasonSpinner = rowView.findViewById(R.id.addNew_reason_spinner);
                viewHolder.durationSprinner = rowView.findViewById(R.id.addNew_duration_spinner);
                viewHolder.dateTxtView = rowView.findViewById(R.id.addNew_date_txt);
                viewHolder.amountEditTxt = rowView.findViewById(R.id.addNew_amount_editTxt);
                viewHolder.detailEditTxt = rowView.findViewById(R.id.addNew_detail_editTxt);
                viewHolder.bodyLay = rowView.findViewById(R.id.addNew_item_body_layout);
                viewHolder.headerLay = rowView.findViewById(R.id.addNew_item_header_layout);
                viewHolder.parentLay = rowView.findViewById(R.id.addNew_item_layout);
                viewHolder.imageLayout = rowView.findViewById(R.id.addnew_image_layout);
                viewHolder.imageBtn = rowView.findViewById(R.id.addNew_detail_img_btn);
                viewHolder.imgView = rowView.findViewById(R.id.addnew_image_result);

                viewHolder.itemCheckLayout = rowView.findViewById(R.id.addnew_listview_item_check_layout);
                viewHolder.checkBox = rowView.findViewById(R.id.addnew_listview_item_checkbox);

                // set duration spinner visible

                if (durationStatus.equals("normal")) {
                    viewHolder.bodyLay.removeView(viewHolder.durationSprinner);
                }else{
                    viewHolder.bodyLay.removeView(viewHolder.imageLayout);
                }
                rowView.setTag(viewHolder);
            }

            //fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            FinancialInformation FinInfo = FinList.get(position);

            holder.reasonSpinner.setAdapter(ReasonSpinnerValueAdapter);
            holder.durationSprinner.setAdapter(DurationSpinnerValueAdapter);

            int selectedIndexReason = ReasonSpinnerListValue.indexOf(FinInfo.getReason());
            holder.reasonSpinner.setSelection(selectedIndexReason);

            currentAmountEditTxt = holder.amountEditTxt;

            holder.dateTxtView.setText(FinInfo.getChosenDate());
            holder.dateTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.datePickDialog(holder.dateTxtView);
                }
            });

            holder.amountEditTxt.setText(moneyFormat.FormatMoneyForShowing(String.valueOf(FinInfo.getAmount())));

            holder.imageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentImgView = holder.imgView;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("position",position);
                    currentFragment.startActivityForResult(intent,0);
                }
            });

            if (checkboxMode == true) {
                holder.itemCheckLayout.setVisibility(View.VISIBLE);


                holder.checkBox.setSelected(FinInfo.isSelectValue());
                holder.checkBox.setTag(FinInfo);

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
                        if (position != ListView.INVALID_POSITION) {
                            FinInfo.setSelectValue(isChecked);
                            if (isChecked) {
//                                holder.itemCheckLayout.setBackgroundColor(Color.parseColor("#A6D3D3D3"));
                                listFinListItemChecked.add(FinInfo);
                            } else {
//                                holder.itemCheckLayout.setBackgroundColor(Color.TRANSPARENT);
                                if(listFinListItemChecked.contains(FinInfo))
                                    listFinListItemChecked.remove(FinInfo);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });

            } else {
                FinInfo.setSelectValue(false);
                holder.itemCheckLayout.setVisibility(View.INVISIBLE);
            }

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
            } else {
                if (FinInfo.getDetail() != null) {
                    holder.detailEditTxt.setText(FinInfo.getDetail().getFinDetContent());
                } else {
                    holder.detailEditTxt.setText("");
                }
            }


            if(holder.imgView.getDrawable()!= null)
                updateCheckLayoutHeight(holder);

            updateListAfterTextChange(holder, position);

        return rowView;
    }


//    void createItemCheckLayout(ViewHolder h){
//        int height = h.headerLay.getHeight() + h.bodyLay.getHeight()+10;
//        RelativeLayout result = new RelativeLayout(this.context);
//        result.setId(itemCheckLayId);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//
//        //convert 15dp to px
//        int marginSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15f,context.getResources().getDisplayMetrics());
//
//        //just get px
//        params.setMargins(marginSize,marginSize,marginSize,marginSize);
//
//        result.setLayoutParams(params);
////        result.setBackgroundColor(Color.RED);
//
//        //convert 5dp to px
//    //    int checkboxPaddingSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,context.getResources().getDisplayMetrics());
//
////        result.setPadding(0,checkboxPaddingSize,0,0);
//        result.setClickable(true);
//        result.setFocusable(true);
//
//        AppCompatCheckBox checkBox = new AppCompatCheckBox(this.context);
//        checkBox.setId(checkboxId);
//        checkBox.setButtonDrawable(R.drawable.custom_checkbox);
//        RelativeLayout.LayoutParams checkboxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        checkboxParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        checkboxParams.addRule(RelativeLayout.ALIGN_PARENT_END);
//        checkBox.setGravity(Gravity.TOP);
//        checkBox.setLayoutParams(checkboxParams);
//        result.addView(checkBox);
//
//
//        h.parentLay.addView(result);
//    }

    void updateCheckLayoutHeight(ViewHolder h){
        int height = h.headerLay.getHeight() + h.bodyLay.getHeight()+10;
        ViewGroup.LayoutParams params = h.itemCheckLayout.getLayoutParams();

        params.height = height;

        h.itemCheckLayout.setLayoutParams(params);
        h.itemCheckLayout.requestLayout();
    }

    void updateListAfterTextChange(ViewHolder holder, int pos){
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
                        holder.amountEditTxt.setError("Nhap so tien");
                    }else {
                        if(Integer.parseInt(input)!=0) {
                            listAmountForCheckEmpty.set(pos,Integer.parseInt(input));
                            String output = moneyFormat.FormatMoneyForShowing(input);
                            holder.amountEditTxt.setText(output);
                            FinList.get(pos).setAmount(Integer.parseInt(moneyFormat.FormatMoneyForSaving(input)));
                        }else {
                            listAmountForCheckEmpty.set(pos,0);
                            holder.amountEditTxt.setError("Nhap so tien");
                        }

                    }
                }

            }
        });

        holder.detailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pos<FinList.size()) {
                    String text = holder.detailEditTxt.getText().toString();
                    FinList.get(pos).getDetail().setFinDetContent(text);
                    updateCheckLayoutHeight(holder);
                }
            }
        });

        holder.dateTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pos<FinList.size()) {
                    String text = holder.dateTxtView.getText().toString();
                    FinList.get(pos).setChosenDate(text);
                }
            }
        });
    }




//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//
//        if(checkboxCheck == false){
//
//        }
//    }

}
