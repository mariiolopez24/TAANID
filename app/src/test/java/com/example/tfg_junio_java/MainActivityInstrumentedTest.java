
package com.example.tfg_junio_java;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Test
    public void testActivityViewsAreDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.userName)).check(matches(isDisplayed()));
            onView(withId(R.id.password)).check(matches(isDisplayed()));
            onView(withId(R.id.iniciarSesion)).check(matches(isDisplayed()));
            onView(withId(R.id.botonRegistro)).check(matches(isDisplayed()));
            onView(withId(R.id.SesionInvitado)).check(matches(isDisplayed()));
        }
    }
}
