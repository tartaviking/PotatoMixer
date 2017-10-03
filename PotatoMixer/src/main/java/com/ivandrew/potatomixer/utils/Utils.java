/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivandrew.potatomixer.utils;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Андрей
 */
public class Utils {

    public static String toHumanReadbleTime(Double millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis.longValue()),
                TimeUnit.MILLISECONDS.toSeconds(millis.longValue())
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis.longValue()))
        );
    }
}
