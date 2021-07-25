 package com.trile.walletnote.ui.history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.trile.walletnote.Adapters.FinancialInformationListAdapter;
import com.trile.walletnote.R;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.Services.HandleMoneyFormat;
import com.trile.walletnote.Services.HandleMoneyFormatImpl;
import com.trile.walletnote.Services.HistoryFragmentService;
import com.trile.walletnote.Services.HistoryFragmentServiceImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.MonthYearPickerDialog;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HistoryFragment extends Fragment{

    View rootView;
    int fragmentWidth, filterChosenMonth, filterChosenYear;

    HistoryFragmentService historyFragmentService;
    FinancialInformationListAdapter listAdapter;
    ArrayList<FinancialInformation> FinList;

    AppCompatButton periodBtn, normalBtn, allBtn, incomingBtn, outgoingBtn;

    ImageButton searchBtn, filterBtn;
    EditText taskbarTxt;
    TextView filterTxtView;

    FloatingActionButton checkFAB, taskFAB;

    HandleMoneyFormat handleMoneyFormat;

    CustomDialog customDialog;

    CurrentStatusPrefs currentStatusPrefs;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;

    MonthYearPickerDialog monthYearPickerDialog;

    String contentForSearch;

    Context wrapper;

    AlertDialog confirmDeleteDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        handleMoneyFormat = new HandleMoneyFormatImpl(rootView.getContext());
        customDialog = new CustomDialogImpl(rootView.getContext());

        wrapper = new ContextThemeWrapper(rootView.getContext(),R.style.popupMenuStyle);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                fragmentWidth = rootView.getWidth();
                setUpComponent();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    void setUpComponent(){

        currentStatusPrefs = new CurrentStatusPrefs(rootView.getContext());
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(rootView.getContext());

        FinList = new ArrayList<>();
        ListView historyListView = rootView.findViewById(R.id.history_listview);

        historyFragmentService = new HistoryFragmentServiceImpl(rootView.getContext());
        listAdapter = new FinancialInformationListAdapter(rootView.getContext(),R.layout.financial_listview_item_layout,FinList,historyListView);


        searchBtn = rootView.findViewById(R.id.history_taskbar_search_btn);
        filterBtn = rootView.findViewById(R.id.history_taskbar_filter_btn);
        taskbarTxt = rootView.findViewById(R.id.history_taskbar_edittext);
        filterTxtView = rootView.findViewById(R.id.history_filter_text_view);


        setUpBackgroundSize();

        periodBtn = rootView.findViewById(R.id.history_period_btn);
        normalBtn = rootView.findViewById(R.id.history_normal_btn);
        allBtn = rootView.findViewById(R.id.history_all_btn);
        incomingBtn = rootView.findViewById(R.id.history_incoming_btn);
        outgoingBtn = rootView.findViewById(R.id.history_outgoing_btn);


        checkFAB = rootView.findViewById(R.id.history_checkboxBtn);
        taskFAB = rootView.findViewById(R.id.history_taskBtn);

        monthYearPickerDialog = new MonthYearPickerDialog(filterTxtView);

        Resources resources = rootView.getResources();

        monthYearPickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                filterChosenMonth = month;
                filterChosenYear = year;
                filterTxtView.setText(getString(R.string.month_year_picker_title,month,year));
                Log.e("adfs",filterTxtView.getText().toString());
            }
        });

        initializeList();
        historyListView.setAdapter(listAdapter);

        periodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allBtn.isSelected()){
                    historyFragmentService.clickFinTypeBtns(2);

                    incomingBtn.setTextColor(Color.WHITE);
                    incomingBtn.setSelected(true);

                    allBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                    allBtn.setSelected(false);
                }

                historyFragmentService.clickPeriodBtns(2);

                allBtn.setEnabled(false);

                periodBtn.setSelected(true);
                periodBtn.setTextColor(Color.WHITE);

                normalBtn.setSelected(false);
                normalBtn.setTextColor(rootView.getResources().getColor( R.color.appThemeColor));

                listAdapter.currentMoneyPeriodicType = false;


                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
                listAdapter.notifyDataSetChanged();

            }
        });

        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyFragmentService.clickPeriodBtns(1);

                allBtn.setEnabled(true);

                normalBtn.setTextColor(Color.WHITE);
                normalBtn.setSelected(true);

                periodBtn.setSelected(false);
                periodBtn.setTextColor(resources.getColor( R.color.appThemeColor));

                listAdapter.currentMoneyPeriodicType = true;


                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyFragmentService.clickFinTypeBtns(1);

                allBtn.setTextColor(Color.WHITE);
                allBtn.setSelected(true);

                incomingBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                incomingBtn.setSelected(false);
                outgoingBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                outgoingBtn.setSelected(false);

                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear,contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });

        incomingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyFragmentService.clickFinTypeBtns(2);

                incomingBtn.setTextColor(Color.WHITE);
                incomingBtn.setSelected(true);

                allBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                allBtn.setSelected(false);
                outgoingBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                outgoingBtn.setSelected(false);

                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear,contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });

        outgoingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyFragmentService.clickFinTypeBtns(3);

                outgoingBtn.setTextColor(Color.WHITE);
                outgoingBtn.setSelected(true);

                incomingBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                incomingBtn.setSelected(false);
                allBtn.setTextColor(resources.getColor( R.color.appThemeColor));
                allBtn.setSelected(false);

                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear,contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });


        checkFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFAB.setVisibility(View.INVISIBLE);
                taskFAB.setVisibility(View.VISIBLE);

                periodBtn.setEnabled(false);
                normalBtn.setEnabled(false);

                allBtn.setEnabled(false);
                incomingBtn.setEnabled(false);
                outgoingBtn.setEnabled(false);


                listAdapter.checkboxMode = true;
                listAdapter.notifyDataSetChanged();
            }
        });

        taskFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(wrapper,taskFAB);
                popupMenu.getMenuInflater().inflate(R.menu.history_task_fab_menu,popupMenu.getMenu());

                Menu taskFabOpts = popupMenu.getMenu();

                if(listAdapter.listIdOfCheckedItems.size()== FinList.size())
                    listAdapter.checkAllBtnCheck =true;

                if(listAdapter.checkAllBtnCheck) {
                    taskFabOpts.getItem(2).setTitle(R.string.history_task_fab_uncheckall_content);
                }else {
                    taskFabOpts.getItem(2).setTitle(R.string.history_task_fab_checkall_content);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.history_task_fab_menu_Delete){
                            if(listAdapter.listIdOfCheckedItems.isEmpty()||listAdapter.listIdOfCheckedItems.size()==0){
                                customDialog.warningDialog(getString(R.string.history_empty_delete_warning_dialog_message));
                            }
                            else {
                                showDeleteDialog();
                            }
                        }
                        if(id == R.id.history_task_fab_menu_Total){
                            taskFABTotalClick();
                        }
                        if(id == R.id.history_task_fab_menu_CheckAll){
                            if(listAdapter.checkAllBtnCheck) {
                                listAdapter.checkAllBtnCheck = false;
                                listAdapter.listIdOfCheckedItems.clear();
                                for(FinancialInformation finItem : FinList){
                                    finItem.setSelectValue(false);
                                }
                            }else {
                                listAdapter.checkAllBtnCheck = true;
                                listAdapter.listIdOfCheckedItems.clear();
                                for(FinancialInformation finItem : FinList){
                                    finItem.setSelectValue(true);
                                    listAdapter.listIdOfCheckedItems.add(Integer.valueOf(finItem.getID()));
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                        }
                        if(id == R.id.history_task_fab_menu_Cancel){
                            taskFABCancelClick();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthYearPickerDialog.show(getActivity().getSupportFragmentManager(),"MonthYearPickerDialog");
            }
        });

        filterTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = filterTxtView.getText().toString();
                if(text.equals("")||text.isEmpty()){
                    historyFragmentService.setFilterMode(false);
                }else{
                    historyFragmentService.setFilterMode(true);
                }

                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                FinList.clear();
                FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
                listAdapter.notifyDataSetChanged();
            }
        });

        taskbarTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contentForSearch = taskbarTxt.getText().toString();

                if(contentForSearch.isEmpty()|| contentForSearch.equals("") || contentForSearch== null)
                    historyFragmentService.setSearchMode(false);
                else{
                    historyFragmentService.setSearchMode(true);
                }
            }
        });

    }
    void taskFABCancelClick(){
        checkFAB.setVisibility(View.VISIBLE);
        taskFAB.setVisibility(View.INVISIBLE);

        periodBtn.setEnabled(true);
        normalBtn.setEnabled(true);

        incomingBtn.setEnabled(true);
        outgoingBtn.setEnabled(true);

        if(normalBtn.isSelected())
            allBtn.setEnabled(true);

        listAdapter.checkboxMode = false;
        listAdapter.checkAllBtnCheck = false;
        listAdapter.listIdOfCheckedItems.clear();
        listAdapter.notifyDataSetChanged();
    }

    void taskFABTotalClick(){
        if(listAdapter.listIdOfCheckedItems.isEmpty()||listAdapter.listIdOfCheckedItems.size()<=1){
            customDialog.warningDialog(getString(R.string.history_empty_total_warning_dialog_message));
        }
        else {
            int total;
            if (normalBtn.isSelected()) {
                total = historyFragmentService.getTotal(listAdapter.listIdOfCheckedItems);

            } else {
                if (outgoingBtn.isSelected())
                    total = periodFinancialInformationPrefs.getPeriodCustomTotal(listAdapter.listIdOfCheckedItems, true);
                else
                    total = periodFinancialInformationPrefs.getPeriodCustomTotal(listAdapter.listIdOfCheckedItems, false);

            }

            String message = handleMoneyFormat.FormatMoneyForShowing(String.valueOf(total));
            customDialog.totalDialog(message);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(rootView.getContext());

        LayoutInflater inflater = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                deleteInformations();
                confirmDeleteDialog.dismiss();
            }
        });

        AppCompatButton deleteDialogCancelBtn= deleteDialogView.findViewById(R.id.custom_delete_dialog_cancel_btn);
        deleteDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteDialog.dismiss();
            }
        });

        confirmDeleteDialog = deleteDialogBuilder.create();

        confirmDeleteDialog.show();
    }

    private void deleteInformations(){
        ReturnData returnData = new ReturnData();
        if (normalBtn.isSelected()) {
            returnData = historyFragmentService.deleteFinancialInformations(listAdapter.listIdOfCheckedItems);

        } else {
            if (outgoingBtn.isSelected())
                returnData = periodFinancialInformationPrefs.deletePeriodInfo(listAdapter.listIdOfCheckedItems, true);
            else
                returnData = periodFinancialInformationPrefs.deletePeriodInfo(listAdapter.listIdOfCheckedItems, false);

        }

        customDialog.warningDialog(returnData.getMessage());
        if(returnData.getResult()==1) {
            FinList.clear();
            FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
            taskFABCancelClick();
        }
    }

    void setUpBackgroundSize(){
        int searchBtnWidth = searchBtn.getWidth();
        int filterBtnWidth = filterBtn.getWidth();
        int editTxtWidth = fragmentWidth - (searchBtnWidth+filterBtnWidth+90);

        ViewGroup.LayoutParams editTxtLP = taskbarTxt.getLayoutParams();

        Log.e("test width ", fragmentWidth +","+searchBtnWidth+", "+filterBtnWidth); //-1 96 0

        editTxtLP.width = editTxtWidth;

        taskbarTxt.setLayoutParams(editTxtLP);

        LinearLayout periodLayout = rootView.findViewById(R.id.history_period_button_layout);
        LinearLayout financialTypeLayout = rootView.findViewById(R.id.history_financial_type_button_layout);

        ViewGroup.LayoutParams periodLayoutParam = periodLayout.getLayoutParams();
        ViewGroup.LayoutParams financialTypeLayoutParam = financialTypeLayout.getLayoutParams();

        periodLayoutParam.width = (fragmentWidth / 10) * 9;
        financialTypeLayoutParam.width = (int) ( (fragmentWidth / 10) * 9.5);

        periodLayout.setLayoutParams(periodLayoutParam);
        financialTypeLayout.setLayoutParams(financialTypeLayoutParam);

        periodLayout.requestLayout();
        financialTypeLayout.requestLayout();
    }

    void initializeList(){
        int FinType = currentStatusPrefs.getFinancialTypeForHistory();
        int DurationType = currentStatusPrefs.getDurationTypeForHistory();

        if(DurationType == 1){
            normalBtn.setTextColor(Color.WHITE);
            normalBtn.setSelected(true);
            historyFragmentService.clickPeriodBtns(1);

            if(FinType == 1){
                allBtn.setTextColor(Color.WHITE);
                allBtn.setSelected(true);
                historyFragmentService.clickFinTypeBtns(1);
            }

        }else{
            periodBtn.setTextColor(Color.WHITE);
            periodBtn.setSelected(true);
            historyFragmentService.clickPeriodBtns(2);

            allBtn.setEnabled(false);

            if(FinType == 1){
                incomingBtn.setTextColor(Color.WHITE);
                incomingBtn.setSelected(true);
                historyFragmentService.clickFinTypeBtns(2);
            }

        }

        if (FinType == 2){
            incomingBtn.setTextColor(Color.WHITE);
            incomingBtn.setSelected(true);
            historyFragmentService.clickFinTypeBtns(2);
        }
        if(FinType == 3){
            outgoingBtn.setTextColor(Color.WHITE);
            outgoingBtn.setSelected(true);
            historyFragmentService.clickFinTypeBtns(3);
        }
        FinList.clear();
        FinList.addAll(historyFragmentService.getListHistoryFinancialInformation(filterChosenMonth,filterChosenYear, contentForSearch));
    }

    void hideSoftKeyboard() {
        Activity activity = getActivity();
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}