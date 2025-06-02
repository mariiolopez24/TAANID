
package com.example.tfg_junio_java;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SubirPeliculaActivityTest {

    @Rule
    public ActivityScenarioRule<SubirPeliculaActivity> activityRule =
            new ActivityScenarioRule<>(SubirPeliculaActivity.class);

    @Test
    public void testCamposVaciosMuestraToast() {
        ActivityScenario<SubirPeliculaActivity> scenario = activityRule.getScenario();

        scenario.onActivity(activity -> {
            onView(withId(R.id.btnSubirPelicula)).perform(click());

            onView(withText(R.string.completarImagen))
                    .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        });
    }
}
