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
import java.util.HashMap;

public class Month_inc extends Fragment {

    private RecyclerView exp;
    private DatabaseReference mExp;
    HashMap<String,Integer> h=new HashMap<>();

    private int prevd = 0,tdays=0,newd,newm,newy,m,da,y;

    public Month_inc(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        h.put("Jan",1); h.put("Feb",2); h.put("Mar",3); h.put("Apr",4); h.put("May",5); h.put("Jun",6); h.put("Jul",7); h.put("Aug",8);
        h.put("Sep",9); h.put("Oct",10); h.put("Nov",11); h.put("Dec",12);
        View mview= inflater.inflate(R.layout.activity_month_inc,container,false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExp= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        exp=mview.findViewById(R.id.recycler_exp_date);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        exp.setHasFixedSize(true);
        exp.setLayoutManager(layoutManager);


        String mDate= DateFormat.getDateInstance().format(new Date());
        String[] d=mDate.split(" ");

        m=h.get(d[1]);
        da=Integer.parseInt(d[0]);
        y=Integer.parseInt(d[2]);

        newd=da;
        newm=m-1;
        newy=y;




        if(newm==0)
        {
            newm=12;
            newy=newy-1;
            tdays=daysOfmonth(newm,newy);
        }





        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.exp_date_recycler,
                MyViewHolder.class,
                mExp
        ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int i)
            {

                String c[]=model.getDate().split(" ");
                int cd=Integer.parseInt(c[0]);
                int cm=h.get(c[1]);
                int cy=Integer.parseInt(c[2]);



                if((cd<=da && cm==m && cy==y) || (cd>=da && cm==newm && cy==newy))
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

        exp.setAdapter(adapter);




    }

    private int daysOfmonth(int newm,int year) {

        int ans;
        switch (newm)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                ans= 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                ans=30;
                break;
            case 2:
                if(((year % 4 == 0) && (year % 100!= 0)) || (year%400 == 0))
                    ans=29;
                else
                    ans=28;

                break;
            default:
                ans=30;
        }
        return ans;
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