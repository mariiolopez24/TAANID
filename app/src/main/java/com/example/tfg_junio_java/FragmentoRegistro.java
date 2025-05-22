package com.example.tfg_junio_java;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentoRegistro extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private Uri imageUri;
    private FirebaseAuth mAuth;

    private EditText usuario, contrasenia, nombre, fechaNacimiento;
    private CheckBox checkboxAdmin;
    private Button botonAvatar, botonRegistrarse;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public FragmentoRegistro() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Toast.makeText(getContext(), "Avatar seleccionado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragmento_registro, container, false);

        usuario = v.findViewById(R.id.txtUsuario);
        contrasenia = v.findViewById(R.id.txtContraseña);
        nombre = v.findViewById(R.id.txtNombre);
        fechaNacimiento = v.findViewById(R.id.txtFechaNacimiento);
        checkboxAdmin = v.findViewById(R.id.checkboxAdmin);
        botonAvatar = v.findViewById(R.id.botonAvatar);
        botonRegistrarse = v.findViewById(R.id.botonRegistrarse);

        CloudinaryManager.init(requireContext());

        botonAvatar.setOnClickListener(view -> verificarPermisosYSeleccionarImagen());
        botonRegistrarse.setOnClickListener(view -> registrarUsuario());

        return v;
    }

    private void verificarPermisosYSeleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                abrirSelectorImagen();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                abrirSelectorImagen();
            }
        }
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Selecciona un avatar"));
    }

    private void registrarUsuario() {
        String email = usuario.getText().toString().trim();
        String password = contrasenia.getText().toString().trim();
        String nombreUsuario = nombre.getText().toString().trim();
        String fechaNac = fechaNacimiento.getText().toString().trim();
        boolean esAdmin = checkboxAdmin.isChecked();

        if (email.isEmpty() || password.isEmpty() || nombreUsuario.isEmpty()) {
            Toast.makeText(getContext(), "Correo, nombre y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailEnUso = !task.getResult().getSignInMethods().isEmpty();
                        if (emailEnUso) {
                            Toast.makeText(getContext(), "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(registroTask -> {
                                        if (registroTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String uid = user.getUid();

                                            Map<String, Object> datosUsuario = new HashMap<>();
                                            datosUsuario.put("admin", esAdmin);

                                            if (!nombreUsuario.isEmpty()) {
                                                datosUsuario.put("Nombre", nombreUsuario);
                                            }

                                            if (!fechaNac.isEmpty()) {
                                                try {
                                                    Date fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNac);
                                                    datosUsuario.put("FechaNacimiento", new Timestamp(fecha));
                                                } catch (ParseException e) {
                                                    Toast.makeText(getContext(), "Formato de fecha inválido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }

                                            if (imageUri != null) {
                                                MediaManager.get().upload(imageUri)
                                                        .callback(new UploadCallback() {
                                                            @Override
                                                            public void onSuccess(String requestId, Map resultData) {
                                                                String imageUrl = resultData.get("secure_url").toString();
                                                                datosUsuario.put("Avatar", imageUrl);
                                                                guardarEnFirestore(uid, datosUsuario);
                                                            }

                                                            @Override public void onError(String requestId, ErrorInfo error) {
                                                                Toast.makeText(getContext(), "Error al subir avatar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override public void onStart(String requestId) {}
                                                            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                                                            @Override public void onReschedule(String requestId, ErrorInfo error) {}
                                                        })
                                                        .dispatch();
                                            } else {
                                                guardarEnFirestore(uid, datosUsuario);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Error al registrar: " + registroTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al verificar el correo: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirSelectorImagen();
            } else {
                Toast.makeText(getContext(), "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
