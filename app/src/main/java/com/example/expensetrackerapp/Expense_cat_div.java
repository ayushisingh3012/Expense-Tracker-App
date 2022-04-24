package com.example.expensetrackerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.expensetrackerapp.Model.Cat2;
import com.example.expensetrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Expense_cat_div extends AppCompatActivity {

    private DatabaseReference mExpCatDiv;

    private RecyclerView exp;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_cat_div);


        Toolbar toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Tracker");
        if (toolbar!=null)
            setSupportActionBar(toolbar);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpCatDiv= FirebaseDatabase.getInstance().getReference().child("ExpenseCategoryDivision").child(uid);
        exp=findViewById(R.id.recycler_exp_cat);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        exp.setHasFixedSize(true);
        exp.setLayoutManager(layoutManager);

    }
    @Override
    public void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Cat2,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Cat2, MyViewHolder>(
                Cat2.class,
                R.layout.exp_cat_div_recycler_data,
                MyViewHolder.class,
                mExpCatDiv
        ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Cat2 cat2, int i)
            {
                myViewHolder.setCategory(cat2.getType());
                myViewHolder.setAmount(cat2.getAmount());
            }

        };

        exp.setAdapter(adapter);




    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setCategory(String category){
            TextView mDate=mView.findViewById(R.id.cat_exp);
            mDate.setText(category);
        }

        private void setAmount(int amount)
        {
            TextView mAmount=mView.findViewById(R.id.amount_exp);
            String stamount=String.valueOf(amount);//stamount->stammount
            mAmount.setText(stamount);
        }


    }




}