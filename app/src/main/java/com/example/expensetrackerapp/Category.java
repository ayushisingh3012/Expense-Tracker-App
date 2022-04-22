package com.example.expensetrackerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.expensetrackerapp.Model.Cat1;
import com.example.expensetrackerapp.Model.Data;
import com.example.expensetrackerapp.placeholder.PlaceholderContent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
 * A fragment representing a list of Items.
 */
public class Category extends Fragment
{

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
    // TODO: Customize parameter argument names
    // private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    // private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    //public Category() {}

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    /*public static Category newInstance(int columnCount) {
        Category fragment = new Category();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }*/

    /*@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        }
        */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mCategoryDatabase= FirebaseDatabase.getInstance().getReference().child("Category").child(uid);

        recyclerView=view.findViewById(R.id.cat_list);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        cat=view.findViewById(R.id.fb_cat);


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


        FirebaseRecyclerAdapter<Cat1, Category.MyViewHolder> adapter=new FirebaseRecyclerAdapter<Cat1, Category.MyViewHolder>(
                Cat1.class,
                R.layout.fragment_category,
                Category.MyViewHolder.class,
                mCategoryDatabase
        )

        {

            @Override
            protected void populateViewHolder(Category.MyViewHolder viewHolder, Cat1 model, int position) {
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
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setCat(String category){
            TextView mType=mView.findViewById(R.id.item_number);
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
