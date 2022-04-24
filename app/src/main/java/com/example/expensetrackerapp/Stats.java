package com.example.expensetrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.expensetrackerapp.Model.Cat2;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Stats extends AppCompatActivity {

    // variable for our bar chart
    BarChart barChart,barChart2;

    // variable for our bar data.
    BarData barData;

    DatabaseReference mExpCatDiv,mIncCatDiv;

    // variable for our bar data set.
    BarDataSet barDataSet;
    TextView exp_bar_emp,inc_bar_emp;
    // array list for storing entries.
    ArrayList barEntriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Tracker");
        if (toolbar!=null)
            setSupportActionBar(toolbar);


        barChart = findViewById(R.id.exp_BarChart);
        barChart2=findViewById(R.id.inc_BarChart);
        exp_bar_emp=findViewById(R.id.exp_bar_txt);
        inc_bar_emp=findViewById(R.id.inc_bar_txt);


        barChart.getDescription().setText("");
        barChart.setNoDataText("");
        barChart.animateY(2000);
        barChart.setVisibleXRangeMaximum(5);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);

        barChart2.getDescription().setText("");
        barChart2.setNoDataText("");
        barChart2.animateY(2000);
        barChart2.setVisibleXRangeMaximum(5);
        barChart2.getAxisLeft().setDrawGridLines(false);
        barChart2.getXAxis().setDrawGridLines(false);
        barChart2.getAxisRight().setDrawGridLines(false);
        barChart2.getAxisRight().setDrawLabels(false);

        // Restarting chart views
        barChart.notifyDataSetChanged();
        barChart.invalidate();

        barChart2.notifyDataSetChanged();
        barChart2.invalidate();




        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpCatDiv= FirebaseDatabase.getInstance().getReference().child("ExpenseCategoryDivision").child(uid);
        mIncCatDiv=FirebaseDatabase.getInstance().getReference().child("IncomeCategoryDivision").child(uid);

        mExpCatDiv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categoriesNames = new ArrayList<>();
                List<BarEntry> entryPerCategory = new ArrayList<>();

                for(DataSnapshot mysnap:snapshot.getChildren())
                {
                    Cat2 c=mysnap.getValue(Cat2.class);

                    categoriesNames.add(c.getType());
                    entryPerCategory.add(new BarEntry(categoriesNames.size()-1,c.getAmount()));
                }
                if (categoriesNames.isEmpty()) {
                    exp_bar_emp.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                } else {
                    exp_bar_emp.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categoriesNames));
                BarDataSet dataSet = new BarDataSet(entryPerCategory, "Categories");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData = new BarData( dataSet);
                barChart.setData(barData);
                barChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mIncCatDiv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categoriesNames = new ArrayList<>();
                List<BarEntry> entryPerCategory = new ArrayList<>();

                for(DataSnapshot mysnap:snapshot.getChildren())
                {
                    Cat2 c=mysnap.getValue(Cat2.class);

                    categoriesNames.add(c.getType());
                    entryPerCategory.add(new BarEntry(categoriesNames.size()-1,c.getAmount()));
                }
                if (categoriesNames.isEmpty()) {
                    inc_bar_emp.setVisibility(View.VISIBLE);
                    barChart2.setVisibility(View.GONE);
                } else {
                    inc_bar_emp.setVisibility(View.GONE);
                    barChart2.setVisibility(View.VISIBLE);
                }
                XAxis xAxis = barChart2.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                barChart2.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categoriesNames));
                BarDataSet dataSet = new BarDataSet(entryPerCategory, "Categories");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData = new BarData( dataSet);
                barChart2.setData(barData);
                barChart2.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}