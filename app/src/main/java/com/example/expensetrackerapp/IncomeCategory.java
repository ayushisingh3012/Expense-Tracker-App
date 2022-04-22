package com.example.expensetrackerapp;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.expensetrackerapp.Model.Cat1;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomeCategory extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mCategoryDatabase;

    private boolean isOpen=false;

    //Recyclerview
    private RecyclerView recyclerView;

    private EditText catEdit;

    private String category1;
    private String post_key;

    private Button btnCnl;
    private  Button btnSave;

    private Animation FadOpen,FadeClose;

    private FloatingActionButton cat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_income_category, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mCategoryDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeCategory").child(uid);

        recyclerView=view.findViewById(R.id.inc_cat_list);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        cat=view.findViewById(R.id.fb_inc_cat);


        //Animation connect...
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                addData();



            }
        });



        // Set the adapter
        /*if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }*/
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Cat1, IncomeCategoryViewHolder> adapter=new FirebaseRecyclerAdapter<Cat1, IncomeCategoryViewHolder>(
                Cat1.class,
                R.layout.content_income_category,
                IncomeCategory.IncomeCategoryViewHolder.class,
                mCategoryDatabase
        )

        {

            @Override
            protected void populateViewHolder(IncomeCategoryViewHolder viewHolder, Cat1 model, int position) {
                viewHolder.setCat(model.getCategory());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        category1=model.getCategory();

                        updateItem();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }
    public static class IncomeCategoryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public IncomeCategoryViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setCat(String category){
            TextView mType=mView.findViewById(R.id.item_number1);
            mType.setText(category);
        }
    }
    public  void updateItem()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View mview=inflater.inflate(R.layout.edit_category,null);
        mydialog.setView(mview);

        catEdit=mview.findViewById(R.id.cat_edit);

        catEdit.setText(category1);
        catEdit.setSelection(category1.length());

        btnCnl=mview.findViewById(R.id.btn_Cancel);
        btnSave=mview.findViewById(R.id.btn_Save);

        AlertDialog dialog=mydialog.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category1=catEdit.getText().toString().trim();

                Cat1 c=new Cat1(category1,post_key);


                mCategoryDatabase.child(post_key).setValue(c);

                dialog.dismiss();
            }
        });

        btnCnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategoryDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public  void  addData()
    {
        AlertDialog.Builder mydailog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.add_category,null);
        mydailog.setView(myview);
        AlertDialog dialog=mydailog.create();

        EditText catAdd=myview.findViewById(R.id.cat_add);
        Button btnCancel=myview.findViewById(R.id.btn_Cancel);
        Button btnSave=myview.findViewById(R.id.btn_Save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=catAdd.getText().toString().trim();
                if(TextUtils.isEmpty(s))
                {
                    catAdd.setError("Required");
                    return;
                }

                String id=mCategoryDatabase.push().getKey();

                Cat1 c=new Cat1(s,id);
                mCategoryDatabase.child(id).setValue(c);

                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Floating Button Animation



}