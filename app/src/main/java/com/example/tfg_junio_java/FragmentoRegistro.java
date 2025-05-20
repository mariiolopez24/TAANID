package com.example.tfg_junio_java;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.pm.PackageManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentoRegistro extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private FirebaseAuth mAuth;

    private EditText usuario, contrasenia, nombre, fechaNacimiento;
    private CheckBox checkboxAdmin;
    private Button botonAvatar, botonRegistrarse;

    public FragmentoRegistro() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragmento_registro, container, false);

        usuario = v.findViewById(R.id.txtUsuario);
        contrasenia = v.findViewById(R.id.txtContraseña);
        nombre = v.findViewById(R.id.txtNombre);
        fechaNacimiento = v.findViewById(R.id.txtFechaNacimiento);
        checkboxAdmin = v.findViewById(R.id.checkboxAdmin);
        botonAvatar = v.findViewById(R.id.botonAvatar);
        botonRegistrarse = v.findViewById(R.id.botonRegistrarse);

        botonAvatar.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            } else {
                abrirSelectorImagen();
            }
        });

        botonRegistrarse.setOnClickListener(view -> registrarUsuario());

        return v;
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona un avatar"), PICK_IMAGE_REQUEST);
    }

    private void registrarUsuario() {
        String email = usuario.getText().toString().trim();
        String password = contrasenia.getText().toString().trim();
        String nombreUsuario = nombre.getText().toString().trim();
        String fechaNac = fechaNacimiento.getText().toString().trim();
        boolean esAdmin = checkboxAdmin.isChecked();

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "El campo de correo electrónico es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(getContext(), "El campo de contraseña es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        Map<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("admin", esAdmin);

                        if (!nombreUsuario.isEmpty()) {
                            datosUsuario.put("Nombre", nombreUsuario);
                        }

                        if (!fechaNac.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date fecha = sdf.parse(fechaNac);
                                datosUsuario.put("FechaNacimiento", new Timestamp(fecha));
                            } catch (ParseException e) {
                                Toast.makeText(getContext(), "Formato de fecha inválido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (imageUri != null) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + uid + ".jpg");
                            storageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        datosUsuario.put("Avatar", uri.toString());
                                        guardarEnFirestore(uid, datosUsuario);
                                    }))
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            guardarEnFirestore(uid, datosUsuario);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void guardarEnFirestore(String uid, Map<String, Object> datosUsuario) {
        FirebaseFirestore.getInstance().collection("DatosUsuario")
                .document(uid)
                .set(datosUsuario)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(getContext(), "Avatar seleccionado", Toast.LENGTH_SHORT).show();
        }
    }
}
