package com.trile.walletnote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trile.walletnote.Activities.DetailActivity;
import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.Services.HandleMoneyFormat;
import com.trile.walletnote.Services.HandleMoneyFormatImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

import java.util.ArrayList;

public class FinancialInformationListAdapter extends BaseAdapter {
    private ArrayList<FinancialInformation> FIList;
    private Context context;
    private int layout;
    private CustomDialog customDialog;
    private ChangeFormatDateService changeFormatDateService;

    private HandleMoneyFormat moneyFormat;

    ReasonPrefs reasonPrefs;

    public boolean checkboxMode = false;

    public ArrayList<Integer> listIdOfCheckedItems;

    public boolean currentMoneyPeriodicType; //true - normal, false - period
    public boolean checkAllBtnCheck;

    public FinancialInformationListAdapter(Context context, int rowLayout, ArrayList<FinancialInformation> list, ListView lv){
        this.context = context;
        layout = rowLayout;
        FIList = list;
        customDialog = new CustomDialogImpl(this.context);
        changeFormatDateService = new ChangeFormatDateServiceImpl(this.context);

        moneyFormat = new HandleMoneyFormatImpl(this.context);
        reasonPrefs = new ReasonPrefs(this.context);

        listIdOfCheckedItems = new ArrayList<>();

        currentMoneyPeriodicType = true;
        checkAllBtnCheck = false;
    }

    @Override
    public int getCount() {
        return FIList.size();
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
        TextView listViewItemReasonTxt,listViewItemDateTxt, listviewItemAmountTxt, listviewItemDetailTxt;
        ImageView listViewItemCatalogImg;
        ImageButton listViewItemInfoBtn;
        RelativeLayout checkBoxLayout;
        CheckBox checkBox;
        LinearLayout mainLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(layout, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.listViewItemReasonTxt = rowView.findViewById(R.id.listview_item_reason_txt);
                viewHolder.listViewItemDateTxt = rowView.findViewById(R.id.listview_item_date_txt);
                viewHolder.listviewItemAmountTxt = rowView.findViewById(R.id.listview_item_amount_txt);
                viewHolder.listViewItemCatalogImg = rowView.findViewById(R.id.listview_item_catalog_img);
                viewHolder.listviewItemDetailTxt = rowView.findViewById(R.id.listview_item_detail_txt);
                viewHolder.listViewItemInfoBtn = rowView.findViewById(R.id.listview_item_info_btn);
                viewHolder.checkBoxLayout = rowView.findViewById(R.id.financial_listview_item_check_layout);
                viewHolder.checkBox = rowView.findViewById(R.id.financial_listview_item_checkbox);
                viewHolder.mainLayout = rowView.findViewById(R.id.financial_item_layout);


                rowView.setTag(viewHolder);
            }


            //fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            FinancialInformation info = FIList.get(position);

            int height = holder.mainLayout.getHeight();
            ViewGroup.LayoutParams params = holder.checkBoxLayout.getLayoutParams();
            params.height = height;

            holder.checkBoxLayout.setLayoutParams(params);

            rowView.requestLayout();

            holder.listViewItemReasonTxt.setText(reasonPrefs.getContentForShowing(Integer.parseInt( info.getReason())));
            holder.listViewItemDateTxt.setText(info.getChosenDate());



            if (info.getType()) { //outgoing value
                holder.listviewItemAmountTxt.setTextColor(Color.parseColor("#F80000"));
                holder.listviewItemAmountTxt.setText("- " + moneyFormat.FormatMoneyForShowing(String.valueOf(info.getAmount())));
            } else {
                holder.listviewItemAmountTxt.setTextColor(Color.parseColor("#4caf50"));
                holder.listviewItemAmountTxt.setText(moneyFormat.FormatMoneyForShowing(String.valueOf(info.getAmount())));
            }

            int img = reasonPrefs.getImage(Integer.parseInt( info.getReason()));
            if(img != 0){
                holder.listViewItemCatalogImg.setImageResource(img);
            }
            if(info.getDetail().getFinDetContent()!=null)
                holder.listviewItemDetailTxt.setText(prepareContent(info.getDetail().getFinDetContent()));

            if(checkboxMode){
                holder.listViewItemInfoBtn.setVisibility(View.INVISIBLE);
                holder.checkBoxLayout.setVisibility(View.VISIBLE);
                holder.checkBox.setVisibility(View.VISIBLE);

                holder.checkBox.setTag(info);
                holder.checkBox.setId(position);
                holder.checkBox.setChecked(info.isSelectValue());
//                if(info.isSelectValue())
//                    holder.checkBoxLayout.setBackgroundColor(Color.parseColor("#A6D3D3D3"));

//
            }else{
                info.setSelectValue(false);
//                holder.checkBoxLayout.setBackgroundColor(Color.TRANSPARENT);
                holder.listViewItemInfoBtn.setVisibility(View.VISIBLE);
                holder.checkBoxLayout.setVisibility(View.INVISIBLE);
                holder.checkBox.setVisibility(View.INVISIBLE);

            }

            holder.checkBoxLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.checkBox.isChecked())
                        holder.checkBox.setChecked(false);
                    else
                        holder.checkBox.setChecked(true);
                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if(buttonView.isShown()){
                       Log.e("test id ", "info.getID()item.getID() "+position);
                       if(position != ListView.INVALID_POSITION) {
                           FinancialInformation item = FIList.get(position);
                           item.setSelectValue(isChecked);
                           Log.e("test id ", info.getID()+","+item.getID());
                           if (isChecked) {
//                            holder.checkBoxLayout.setBackgroundColor(Color.parseColor("#A6D3D3D3"));
                               listIdOfCheckedItems.add(item.getID());


                           } else {
                               if(checkAllBtnCheck)
                                   checkAllBtnCheck = false;
//                            holder.checkBoxLayout.setBackgroundColor(Color.TRANSPARENT);
                               if (listIdOfCheckedItems.contains(item.getID()))
                                   listIdOfCheckedItems.remove(Integer.valueOf(item.getID()));
                           }
                       }
                   }
                }
            });

            holder.listViewItemInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,DetailActivity.class);
                    intent.putExtra("objectFin",info);
                    Log.e("boolean adapter", currentMoneyPeriodicType+"");
                    intent.putExtra("currentPeriodType", currentMoneyPeriodicType);

                    context.startActivity(intent);
                }
            });

        return rowView;
    }

    String prepareContent(String content){
        int length = content.length();
        String result="";
        if(length>8){
            result = content.substring(0,8)+"...";
        }else
            result = content;
        return result;
    }


}
