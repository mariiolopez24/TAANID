
package com.example.tfg_junio_java;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CloudinaryManagerInstrumentedTest {

    @Test
    public void testInitDoesNotCrash() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CloudinaryManager.init(context);
    }
}
