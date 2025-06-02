
package com.example.tfg_junio_java;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class FragmentActivityInstrumentedTest {

    @Test
    public void testFragmentAddition() {
        try (ActivityScenario<FragmentActivity> scenario = ActivityScenario.launch(FragmentActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            });
        }
    }
}
