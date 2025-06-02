package com.example.tfg_junio_java;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class FragmentoRegistroInstrumentedTest {

    @Test
    public void testFragmentLaunch() {
        FragmentScenario<FragmentoRegistro> scenario = FragmentScenario.launchInContainer(FragmentoRegistro.class);

        scenario.onFragment(fragment -> {
            View view = fragment.getView();
            assertNotNull(view);

            assertNotNull(view.findViewById(R.id.txtUsuario));
            assertNotNull(view.findViewById(R.id.txtContraseña));
            assertNotNull(view.findViewById(R.id.txtNombre));
            assertNotNull(view.findViewById(R.id.txtFechaNacimiento));
            assertNotNull(view.findViewById(R.id.checkboxAdmin));
            assertNotNull(view.findViewById(R.id.botonAvatar));
            assertNotNull(view.findViewById(R.id.botonRegistrarse));
        });
    }
}
