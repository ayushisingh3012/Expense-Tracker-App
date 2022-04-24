package com.example.expensetrackerapp;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Income_cat_div extends AppCompatActivity {

    private DatabaseReference mIncCatDiv;

    private RecyclerView inc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_cat_div);

        Toolbar toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Tracker");
        if (toolbar!=null)
            setSupportActionBar(toolbar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncCatDiv= FirebaseDatabase.getInstance().getReference().child("IncomeCategoryDivision").child(uid);
        inc=findViewById(R.id.recycler_inc_cat);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        inc.setHasFixedSize(true);
        inc.setLayoutManager(layoutManager);
    }

    public void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Cat2, MyViewHolder> adapter=new FirebaseRecyclerAdapter<Cat2, MyViewHolder>(
                Cat2.class,
                R.layout.inc_cat_div_recycler_data,
                MyViewHolder.class,
                mIncCatDiv
        ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Cat2 cat2, int i)
            {
                myViewHolder.setCategory(cat2.getType());
                myViewHolder.setAmount(cat2.getAmount());
            }

        };

        inc.setAdapter(adapter);




    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setCategory(String category){
            TextView mDate=mView.findViewById(R.id.inc_exp);
            mDate.setText(category);
        }

        private void setAmount(int amount)
        {
            TextView mAmount=mView.findViewById(R.id.amount_inc);
            String stamount=String.valueOf(amount);//stamount->stammount
            mAmount.setText(stamount);
        }


    }
}