package com.example.tfg_junio_java;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoAvatares#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoAvatares extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Avatar avatar;


    public FragmentoAvatares() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoAvatares.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoAvatares newInstance(String param1, String param2) {
        FragmentoAvatares fragment = new FragmentoAvatares();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_fragmento_avatares, container, false);

        ImageButton avatar1 = v.findViewById(R.id.avatar1);
        ImageButton avatar2 = v.findViewById(R.id.avatar2);
        ImageButton avatar3 = v.findViewById(R.id.avatar3);
        ImageButton avatar4 = v.findViewById(R.id.avatar4);
        ImageButton avatar5 = v.findViewById(R.id.avatar5);
        ImageButton avatar6 = v.findViewById(R.id.avatar6);

        avatar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.avatar_profile_vector);
                int foto =  avatar.getFoto();
                insertAvatar(foto);

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content,fragmentoRegistro).commit();




            }
        });
        avatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.descarga);
                int foto =  avatar.getFoto();
                insertAvatar(foto);



                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content,fragmentoRegistro).commit();

            }
        });
        avatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.avatar_profile_vector_png_photo);
                int foto =  avatar.getFoto();
                insertAvatar(foto);



            }
        });
        avatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.descarga__1_);
                int foto =  avatar.getFoto();
                insertAvatar(foto);


                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content,fragmentoRegistro).commit();
            }
        });
        avatar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.user_avatar_profile_no_background);
                int foto =  avatar.getFoto();
                insertAvatar(foto);


                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content,fragmentoRegistro).commit();
            }
        });
        avatar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar = new Avatar(R.drawable.avatar_profile_vector_png_photos);
                int foto =  avatar.getFoto();
                insertAvatar(foto);


                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content,fragmentoRegistro).commit();
            }
        });
        return v;
    }

    public void insertAvatar(int fotoAvatar){
        ContentValues values = new ContentValues();
        values.put(EstructuraBDD.column_avatar,fotoAvatar );

    }
}



