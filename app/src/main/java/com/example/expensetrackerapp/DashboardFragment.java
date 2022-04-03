package com.example.expensetrackerapp;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.

 */
public class DashboardFragment extends Fragment {

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview...
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean
    private boolean isOpen=false;

    //Animation
    private Animation FadOpen,FadeClose;


    //DashBoard income and expense
    private TextView totalincomeresult;
    private TextView totalexpenseresult;

    //Recycler..
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    //Firebase....
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    public String TAG="Expense Tracker App";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //Connect floating button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);

        //Connect floating text
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_txt);

        // Total income and expense result set
        totalincomeresult=myview.findViewById(R.id.income_set_result);
        totalexpenseresult=myview.findViewById(R.id.expense_set_result);

        //Recycler..
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);



        //Animation connect...
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }
                else{
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum=0;
                for (DataSnapshot mysnap:snapshot.getChildren())
                {
                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalincomeresult.setText(stResult+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum=0;
                for(DataSnapshot mysnap:snapshot.getChildren())
                {
                    Data data=mysnap.getValue(Data.class);

                    totalsum+= data.getAmount();

                    totalexpenseresult.setText(String.valueOf(totalsum)+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler
        LinearLayoutManager linearLayoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        linearLayoutManagerIncome.setStackFromEnd(true);
        linearLayoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(linearLayoutManagerIncome);

        LinearLayoutManager linearLayoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        linearLayoutManagerExpense.setStackFromEnd(true);
        linearLayoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(linearLayoutManagerExpense);

        return myview;
    }

    private void addData(){
        //Fab Button Income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }

    //Floating Button Animation
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }
        else{
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydailog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydailog.setView(myview);
        AlertDialog dialog= mydailog.create();

        dialog.setCancelable(false);

        EditText editAmount=myview.findViewById(R.id.amount_edit);
        EditText editType=myview.findViewById(R.id.type_edit);
        EditText editNote=myview.findViewById(R.id.note_edit);

        Button btnCancel=myview.findViewById(R.id.btnCancel);
        Button btnSave=myview.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=editAmount.getText().toString().trim();
                String type=editType.getText().toString().trim();
                String note=editNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount))
                {
                    editAmount.setError("Required");
                    return;
                }

                if(TextUtils.isEmpty(type))
                {
                    editType.setError("Required");
                    return;
                }

                if(TextUtils.isEmpty(note))
                {
                    editNote.setError("Required");
                    return;
                }

                int ouramountint=Integer.parseInt(amount);


                String id=mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(ouramountint,type,note,id,mDate);

                assert id != null;
                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText amount=myview.findViewById(R.id.amount_edit);
        EditText note=myview.findViewById(R.id.note_edit);
        EditText type=myview.findViewById(R.id.type_edit);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmamount=amount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();

                if(TextUtils.isEmpty(tmamount))
                {
                    amount.setError("Required Field.....");
                    return;
                }

                int inamount=Integer.parseInt(tmamount);

                if(TextUtils.isEmpty(tmtype))
                {
                    type.setError("Required Field.....");
                    return;
                }
                if(TextUtils.isEmpty((tmnote)))
                {
                    note.setError("Required Field....");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                Data.class,
                R.layout.dashboard_income,
                DashboardFragment.IncomeViewHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int i) {

                incomeViewHolder.setIncomeType(data.getType());
                incomeViewHolder.setIncomeAmount(data.getAmount());
                incomeViewHolder.setIncomeDate(data.getDate());


            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);


        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                DashboardFragment.ExpenseViewHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {

                expenseViewHolder.setExpenseType(data.getType());
                expenseViewHolder.setExpenseAmount(data.getAmount());
                expenseViewHolder.setExpenseDate(data.getDate());


            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);

    }
    //Recycler
        public  static  class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){

            TextView mtype=mIncomeView.findViewById(R.id.type_income_ds);
            mtype.setText(type);

        }

        public  void setIncomeAmount(int amount)
        {
            TextView mammount=mIncomeView.findViewById(R.id.amount_income_ds);
            mammount.setText(String.valueOf(amount));
        }
        public void setIncomeDate(String date)
        {
            TextView mdate=mIncomeView.findViewById(R.id.date_income_ds);
            mdate.setText(date);
        }
    }

    public  static  class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){

            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);

        }

        public  void setExpenseAmount(int amount)
        {
            TextView mammount=mExpenseView.findViewById(R.id.amount_expense_ds);
            mammount.setText(String.valueOf(amount));
        }
        public void setExpenseDate(String date)
        {
            TextView mdate=mExpenseView.findViewById(R.id.date_expense_ds);
            mdate.setText(date);
        }
    }
}