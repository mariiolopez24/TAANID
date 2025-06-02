
package com.example.tfg_junio_java;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListaInstrumentedTest {

    @Test
    public void testFragmentLaunch() {
        FragmentScenario<Lista> scenario = FragmentScenario.launchInContainer(Lista.class);

        scenario.onFragment(fragment -> {
            View view = fragment.getView();
            assertNotNull(view);

            assertNotNull(view.findViewById(R.id.recyclerPelis));
            assertNotNull(view.findViewById(R.id.search_input));
            assertNotNull(view.findViewById(R.id.btnFiltrarFavoritos));
        });
    }
}
