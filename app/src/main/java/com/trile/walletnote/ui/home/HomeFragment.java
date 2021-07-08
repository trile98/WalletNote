package com.trile.walletnote.ui.home;

import android.app.Activity;
import android.app.job.JobInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.trile.walletnote.Activities.MainActivity;
import com.trile.walletnote.Services.HandleMoneyFormat;
import com.trile.walletnote.Services.HandleMoneyFormatImpl;
import com.trile.walletnote.Services.HomeFragmentService;
import com.trile.walletnote.Services.HomeFragmentServiceImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.Adapters.FinancialInformationListAdapter;
import com.trile.walletnote.R;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    Activity activity;
    private HomeViewModel homeViewModel;

    SQLiteDatabase db;
    Database dbHelper;

    private View rootView;
    int fragmentHeight;

    HomeFragmentService homeService;

    ArrayList<Integer> totalArr;

    HandleMoneyFormat moneyFormat;

    CurrentStatusPrefs currentStatusPrefs;

    ImageButton incomingInfoBtn, outgoingInfoBtn, historyDirectBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        moneyFormat = new HandleMoneyFormatImpl(rootView.getContext());
        rootView.post(new Runnable() {
            @Override
            public void run() {
                // for instance
                fragmentHeight = rootView.getMeasuredHeight();
                setUpComponent();
            }
        });
//        setUpComponent();
        return rootView;
    }

    void setUpComponent() {
        Log.e("min",JobInfo.getMinFlexMillis()+"");
        currentStatusPrefs = new CurrentStatusPrefs(rootView.getContext());
        currentStatusPrefs.setStatusForShowingHistory(1,1);

        TextView balanceTotalTitleHome = rootView.findViewById(R.id.balance_title_home);
        TextView periodicIncomeHomeTotalContent = rootView.findViewById(R.id.home_periodic_incoming_total_content);
        TextView periodicOutgoingHomeTotalContent = rootView.findViewById(R.id.home_periodic_outgoing_total_content);

        incomingInfoBtn = rootView.findViewById(R.id.home_periodic_incoming_info_btn);
        outgoingInfoBtn = rootView.findViewById(R.id.home_periodic_outgoing_info_btn);
        historyDirectBtn = rootView.findViewById(R.id.home_history_direct_img_btn);

        LinearLayout homeHistoryLay = rootView.findViewById(R.id.home_history_layout);
        ListView homeHistory = rootView.findViewById(R.id.home_recent_history_listview);

        LinearLayout homeBlueBackLay = rootView.findViewById(R.id.home_blue_background_lay);
        LinearLayout homeGreyBackLay = rootView.findViewById(R.id.home_grey_background_lay);

        ViewGroup.LayoutParams blueBackLayParam = homeBlueBackLay.getLayoutParams();
        ViewGroup.LayoutParams greyBackLayParam = homeGreyBackLay.getLayoutParams();

        homeService = new HomeFragmentServiceImpl(rootView.getContext());

        totalArr = new ArrayList<>();
        totalArr = homeService.getTotal();

        //set-up background height
//        int fragmentHeight = rootView.getMeasuredHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        blueBackLayParam.height = (screenHeight /7)* 3;
        greyBackLayParam.height = (screenHeight / 7) * 4;

        homeBlueBackLay.setLayoutParams(blueBackLayParam);
        homeGreyBackLay.setLayoutParams(greyBackLayParam);
        homeBlueBackLay.requestLayout();
        homeGreyBackLay.requestLayout();


        //set up list view height
        ViewGroup.LayoutParams params = homeHistoryLay.getLayoutParams();
        params.height = (fragmentHeight / 3) * 2;
        homeHistoryLay.setLayoutParams(params);
        homeHistoryLay.requestLayout();

        periodicIncomeHomeTotalContent.setText(moneyFormat.FormatMoneyForShowing(""+totalArr.get(1)));
        periodicOutgoingHomeTotalContent.setText(moneyFormat.FormatMoneyForShowing(""+totalArr.get(2)));



        //set balance total
        balanceTotalTitleHome.setText(moneyFormat.FormatMoneyForShowing(totalArr.get(0) + ""));


        ArrayList<FinancialInformation> FIList = new ArrayList<FinancialInformation>();
        FIList = homeService.getTopFive();

        if (FIList != null) {
            FinancialInformationListAdapter adapter = new FinancialInformationListAdapter(getContext(), R.layout.financial_listview_item_layout, FIList,homeHistory);
            homeHistory.setAdapter(adapter);
        }

        setUpBtns();

    }

    void setUpBtns(){
        incomingInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatusPrefs.setStatusForShowingHistory(2,2);
                ((MainActivity) getActivity()).replaceFragment(R.id.navigation_history);
            }
        });

        outgoingInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatusPrefs.setStatusForShowingHistory(3,2);
                ((MainActivity) getActivity()).replaceFragment(R.id.navigation_history);
            }
        });

        historyDirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(R.id.navigation_history);
            }
        });
    }
}