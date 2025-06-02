
package com.example.tfg_junio_java;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EditarPerfilInstrumentedTest {

    @Before
    public void setUp() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            auth.signInAnonymously();
        }
    }

    @Test
    public void testActivityLaunch() {
        try (ActivityScenario<EditarPerfilActivity> scenario = ActivityScenario.launch(EditarPerfilActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.editNombre));
                assertNotNull(activity.findViewById(R.id.editFechaNacimiento));
                assertNotNull(activity.findViewById(R.id.imageAvatar));
                assertNotNull(activity.findViewById(R.id.btnGuardarCambios));
            });
        }
    }
}
