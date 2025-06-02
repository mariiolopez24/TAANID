package com.example.tfg_junio_java;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cloudinary.android.MediaManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MyAppInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
        CloudinaryManager.init(appContext);
    }

    @Test
    public void testCloudinaryManagerInitialization() {
        assertNotNull("MediaManager no fue inicializado correctamente", MediaManager.get());
    }
}
