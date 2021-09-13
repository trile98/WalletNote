package com.trile.walletnote.ui.addnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toolbar;

import com.trile.walletnote.Activities.MainActivity;
import com.trile.walletnote.Adapters.AddNewAdapter;
import com.trile.walletnote.Services.AddNewFragmentService;
import com.trile.walletnote.Services.AddNewFragmentServiceImpl;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.google.android.material.floatingactionbutton.*;

import com.trile.walletnote.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddNewFragment extends Fragment{

    private AddNewViewModel mViewModel;
    private View rootView;
    private ListView lvAddNew;
    private ArrayList<FinancialInformation> FinList;
    Toolbar toolbar;
    FloatingActionButton addBtn, removeBtn;
    int currentMenuItemId;

    boolean addNewType;

    AppCompatButton cancelBtn;

    AddNewAdapter adapter;
    AddNewFragmentService addNewFragmentService;

    CustomDialog customDialog;
    CurrentStatusPrefs currentStatusPrefs;

    ChangeFormatDateService changeFormatDateService;

//    ArrayList<Integer> listIdOfListViewItemChecked = new ArrayList<Integer>();
//    ArrayList<Integer> listIdOfFinListItemChecked = new ArrayList<>();

    public static AddNewFragment newInstance() {
        return new AddNewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_new, container, false);
        toolbar = rootView.findViewById(R.id.addNew_toolbar_popup);
        currentStatusPrefs = new CurrentStatusPrefs(rootView.getContext());
        changeFormatDateService = new ChangeFormatDateServiceImpl(rootView.getContext());

        addNewType = currentStatusPrefs.getAddNewStatus();

        if(addNewType)
            toolbar.setTitle(R.string.add_new_action_bar_title_outgo);
        else
            toolbar.setTitle(R.string.add_new_action_bar_title_income);

        setUpComponent();
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.addnew_toolbar_popup_menu);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = ((MainActivity)getActivity());
                activity.navView.setSelectedItemId(activity.previousTabId);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //                hide keyboard
                InputMethodManager imm = (InputMethodManager)rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                adapter.currentAmountEditTxt.clearFocus();
                adapter.currentDetailEditTxt.clearFocus();

                int id = item.getItemId();
                if(id == R.id.addnew_toolbar_popup_menu_Delete){
                    currentMenuItemId = id;
                    deleteToolbarClick();
                }

                if(id == R.id.addnew_toolbar_popup_menu_Save){
                    currentMenuItemId = id;
                    saveToolbarClick();
                }
                return true;
            }
        });
        return rootView;
    }

    private void setUpComponent(){

        addBtn = rootView.findViewById(R.id.addNew_addBtn);
        removeBtn = rootView.findViewById(R.id.addNew_removeBtn);
        FinList = new ArrayList<FinancialInformation>();

        String durationType = currentStatusPrefs.getDurationStatus();

        if(durationType.equals("period"))
            FinList.add(new FinancialInformation(currentStatusPrefs.getAddNewStatus(),changeFormatDateService.getCurrentDateForShowing(),0,1,new FinancialDetail("")));
        else if(durationType.equals("normal"))
            FinList.add(new FinancialInformation(currentStatusPrefs.getAddNewStatus(),changeFormatDateService.getCurrentDateForShowing(),0,new FinancialDetail("")));


        adapter = new AddNewAdapter(R.layout.addnew_listview_item_layout,FinList,this);
        lvAddNew = rootView.findViewById(R.id.addNew_listview);

        lvAddNew.setAdapter(adapter);
        addNewFragmentService = new AddNewFragmentServiceImpl(rootView.getContext(),adapter);

        cancelBtn = new AppCompatButton(rootView.getContext());
        cancelBtn.setText("Hủy");

        Toolbar.LayoutParams layout = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.END;
        layout.rightMargin = 5;
        cancelBtn.setBackgroundColor(Color.parseColor("#1fcecb"));
        cancelBtn.setLayoutParams(layout);

        customDialog = new CustomDialogImpl(rootView.getContext());

        setUpEvents();
    }

    void setUpEvents(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                addNewFragmentService.PutMoreAddNewItem(FinList);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelToolbarClick();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = adapter.listFinListItemChecked.size();
                Log.e("test1",length+"");
                if(FinList.size()>1 && length != FinList.size()) {
                    FinancialInformation removeItem;
                    for (int i = length-1; i >=0; i--) {
                        removeItem = adapter.listFinListItemChecked.get(i);
                        int checkAmountItem = adapter.listAmountForCheckEmpty.get(FinList.indexOf(removeItem));
                        FinList.remove(removeItem);
                        adapter.listAmountForCheckEmpty.remove(Integer.valueOf(checkAmountItem));
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    customDialog.warningDialog("de lai 1 khung");
                }

                adapter.listFinListItemChecked.clear();
                adapter.notifyDataSetChanged();

                cancelToolbarClick();
            }
        });


    }

    private void deleteToolbarClick(){
        addBtn.setVisibility(View.INVISIBLE);
        removeBtn.setVisibility(View.VISIBLE);
        toolbar.getMenu().clear();
        toolbar.addView(cancelBtn);
        adapter.checkboxMode = true;
        adapter.notifyDataSetChanged();

    }

    private void saveToolbarClick(){
        if(adapter.listAmountForCheckEmpty.contains(0)){
            customDialog.warningDialog("nhap du thong tin");
        }else {
            ReturnData returnData = addNewFragmentService.SaveAddNewInfo(FinList);

            if (returnData.getResult() == 1) {
                customDialog.saveSuccessDialog((MainActivity) getActivity());
            } else if (returnData.getResult() == 2) {
                customDialog.warningDialog(rootView.getContext().getString(R.string.saveFail) + ": " + returnData.getMessage());
            }
        }
    }

    private void cancelToolbarClick(){
        addBtn.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.INVISIBLE);
        toolbar.removeView(cancelBtn);
        toolbar.inflateMenu(R.menu.addnew_toolbar_popup_menu);
        adapter.checkboxMode = false;
        adapter.notifyDataSetChanged();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK&& requestCode == 0) {
            int pos = adapter.currentImagePosition;
            super.onActivityResult(requestCode, resultCode, data);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Log.e("imgPos",pos+"");
            //add to object
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
            byte[] imgArray = stream.toByteArray();

            FinList.get(pos).getDetail().setFinDetImage(imgArray);

            adapter.notifyDataSetChanged();
        }
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

    //    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(AddNewViewModel.class);
//        // TODO: Use the ViewModel
//    }

}