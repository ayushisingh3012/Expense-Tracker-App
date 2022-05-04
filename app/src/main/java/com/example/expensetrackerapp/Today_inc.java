package com.example.expensetrackerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensetrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class Today_inc extends Fragment
{

    private RecyclerView inc;
    private DatabaseReference mInc;


    public  Today_inc(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View mview= inflater.inflate(R.layout.activity_today_inc,container,false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mInc= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        inc=mview.findViewById(R.id.recycler_exp_date);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        inc.setHasFixedSize(true);
        inc.setLayoutManager(layoutManager);





        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.exp_date_recycler,
               MyViewHolder.class,
                mInc
        ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int i)
            {
                String mDate= DateFormat.getDateInstance().format(new Date());
                if(mDate.equals(model.getDate()))
                {
                    viewHolder.setType(model.getType());
                    viewHolder.setNote(model.getNote());
                    viewHolder.setDate(model.getDate());
                    viewHolder.setAmount(model.getAmount());
                }
                else
                {
                    viewHolder.setType("");
                    viewHolder.setNote("");
                    viewHolder.setDate("");
                    viewHolder.setAmount(0);
                }
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

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note)
        {
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);

        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setAmount(int amount)
        {
            TextView mAAmount=mView.findViewById(R.id.amount_txt_expense);
            String stramount=String.valueOf(amount);//strammount->stramount
            mAAmount.setText(stramount);
        }

        private  void setBackground(){
            LinearLayout l=mView.findViewById(R.id.back);
            l.setBackgroundColor(Color.parseColor("#EDC548DC"));

        }
    }
}