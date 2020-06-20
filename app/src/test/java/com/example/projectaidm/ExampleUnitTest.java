package com.example.projectaidm;

import android.widget.Spinner;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void calculeaza(){
        Conturi euro = new Conturi();
        euro.setValuta("EUR");
        euro.setValuta("1000");
        euro.setCoefc(4.6);
        euro.setCoefv(4.8);

        HomeActivity er = new HomeActivity();
        //er.getConvertedsum(String spinner1, String spinner2 );
        //assertEquals();
    }
}