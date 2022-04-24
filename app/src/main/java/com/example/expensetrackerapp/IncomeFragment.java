package com.example.expensetrackerapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.DateFormat;
import java.util.Date;


/**

 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    //Firebase database

    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mIncCatDiv;
    private int ans,intammount;

    //recyclerview
    private RecyclerView recyclerView;

    //Text View
    private TextView incomeTotalSum;

    //update edit text
    private EditText edtAmount;
    private  EditText edtType;
    private EditText edtNote;

    //button for update & delete
    private Button btnUpdate;
    private Button btnDelete;

    //Data item value
    private String type;
    private String note;
    private int amount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_income, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mIncCatDiv=FirebaseDatabase.getInstance().getReference().child("IncomeCategoryDivision").child(uid);

        incomeTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalvalue=0;

                for(DataSnapshot mysnapshot:dataSnapshot.getChildren())
                {
                    Data data=mysnapshot.getValue(Data.class);
                   totalvalue+=data.getAmount();
                   String stTotalvalue=String.valueOf(totalvalue);
                   incomeTotalSum.setText(stTotalvalue+".00");
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;

    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.income_recycler_data,
                MyViewHolder.class,
                mIncomeDatabase
        )

        {

            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();

                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note)
        {
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);

        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount)
        {
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            String stamount=String.valueOf(amount);//stamount->stammount
            mAmount.setText(stamount);
        }

    }

    private void updateDataItem(){

        ans=0;

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.update_data_item,null);

        mydialog.setView(myview);

        edtAmount=myview.findViewById(R.id.amount_edit);
        edtType=myview.findViewById(R.id.type_edit);
        edtNote=myview.findViewById(R.id.note_edit);

        //Set data to edit text
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);

        final AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevType=type;
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdammount=String.valueOf(amount);

                mdammount=edtAmount.getText().toString().trim();

                int myAmmount=Integer.parseInt(mdammount);

                if(prevType.equals(type))
                {
                    mIncCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 c=mysnap.getValue(Cat2.class);

                                if(c.getType().equals(type))
                                {
                                    mIncCatDiv.child(type).child("amount").setValue(c.getAmount()-amount+myAmmount);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(!prevType.equals(type))
                {

                    mIncCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            for(DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 c=mysnap.getValue(Cat2.class);
                                if(c.getType().equals(prevType))
                                {
                                    ans+=c.getAmount();
                                    if(ans==intammount)
                                        mIncCatDiv.child(prevType).removeValue();
                                    else
                                        mIncCatDiv.child(prevType).child("amount").setValue(ans-myAmmount);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mIncCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            boolean flag = false;
                            for (DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 t=mysnap.getValue(Cat2.class);
                                if(t.getType().equals(type))
                                {
                                    flag=true;
                                    int ans2=t.getAmount()+myAmmount;
                                    Cat2 c=new Cat2(ans2,type,type);
                                    mIncCatDiv.child(type).setValue(c);

                                }

                            }
                            if (flag==false)
                            {
                                Cat2 c=new Cat2(myAmmount,type,type);
                                mIncCatDiv.child(type).setValue(c);

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(myAmmount,type,note,post_key,mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mIncCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot mysnap:snapshot.getChildren())
                        {
                            Cat2 c=mysnap.getValue(Cat2.class);
                            if(c.getType().equals(type))
                            {
                                ans+=c.getAmount();
                                if(ans==amount)
                                    mIncCatDiv.child(type).removeValue();
                                else
                                    mIncCatDiv.child(type).child("amount").setValue(ans-amount);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();

    }
}