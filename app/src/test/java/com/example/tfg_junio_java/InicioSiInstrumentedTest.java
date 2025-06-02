
package com.example.tfg_junio_java;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class InicioSiInstrumentedTest {

    @Test
    public void testActivityLaunch() {
        try (ActivityScenario<InicioSi> scenario = ActivityScenario.launch(InicioSi.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.btnSubirPeliculaAdmin));
                assertNotNull(activity.findViewById(R.id.avatarToolbar));
            });
        }
    }
}
