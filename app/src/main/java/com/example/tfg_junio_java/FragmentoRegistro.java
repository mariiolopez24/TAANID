package com.example.tfg_junio_java;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoRegistro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoRegistro extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BaseDatosHelper dbHelper;

    public FragmentoRegistro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoRegistro.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoRegistro newInstance(String param1, String param2) {
        FragmentoRegistro fragment = new FragmentoRegistro();
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

        View v =inflater.inflate(R.layout.fragment_fragmento_registro, container, false);


        TextView usuario = v.findViewById(R.id.txtUsuario);
        TextView contrasenia = v.findViewById(R.id.txtContraseña);
        TextView nombre = v.findViewById(R.id.txtNombre);
        TextView fechaNacimiento = v.findViewById(R.id.txtFechaNacimiento);
        Button botonAvatar = v.findViewById(R.id.botonAvatar);
        Button botonRegistrarse = v.findViewById(R.id.botonRegistrarse);

        dbHelper = new BaseDatosHelper(getContext());

        botonAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                FragmentoAvatares fragmentoAvatares = new FragmentoAvatares();
                ft.replace(android.R.id.content,fragmentoAvatares).commit();

            }
        });

        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userdb = usuario.getText().toString();
                String passdb = contrasenia.getText().toString();
                String namedb = nombre.getText().toString();
                String btddb = fechaNacimiento.getText().toString();


                if (checkUser(userdb)){ Toast.makeText(getContext(),"El usuario ya está registrado",Toast.LENGTH_SHORT).show(); return; }

                insertBBD(userdb, passdb, btddb, namedb);

                Toast.makeText(getContext(),"El usuario se ha registrado correctamente",Toast.LENGTH_SHORT).show();

                Intent j = new Intent(getActivity(), MainActivity.class);
                startActivity(j);


            }
        });




        return v;









    }

    public boolean checkUser(String user){
        Cursor cursor = this.dbHelper.getReadableDatabase().query(
                EstructuraBDD.tabla_usuarios,
                EstructuraBDD.projection,
                EstructuraBDD.column_usuario + "=?",
                new String[]{user},
                null,
                null,
                EstructuraBDD.sortOrderAsc
        );

        return cursor.getCount() > 0;
    }

    public void insertBBD(String usuario, String contrasenia, String fechaNac, String nombre){
        ContentValues values = new ContentValues();
        values.put(EstructuraBDD.column_usuario, usuario);
        values.put(EstructuraBDD.column_contraseña, contrasenia);
        values.put(EstructuraBDD.column_nombre,nombre);
        values.put(EstructuraBDD.column_fechaNac, fechaNac);

        this.dbHelper.getWritableDatabase().insert(
                EstructuraBDD.tabla_usuarios,
                null,
                values);
    }


}