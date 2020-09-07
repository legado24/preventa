package com.legado.preventagps.util;

import android.text.format.Time;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ERICK on 20/02/2018.
 */

public class Metodos {


    public static String parsingEmpty(String cadena) throws Exception {
        try {
            if (cadena==null) {
                return null;
            }
            return cadena.trim().equals("")?null:cadena.trim();
        } catch (Exception e) {
            throw e;
        }
    }

    public static String parsingEmptyUppercase(String cadena) throws Exception {
        try {
            if (cadena==null) {
                return null;
            }
            return cadena.trim().equals("")?null:cadena.trim().toUpperCase();
        } catch (Exception e) {
            throw e;
        }
    }



    public static String ObtenerAnioActual() throws Exception {
        String anio = "";
        try {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            anio = String.valueOf(today.year);
            return anio;
        } catch (Exception e) {
            throw e;
        }
    }

    public static boolean VerificarNumeroDecimal(String cadena) {
        Boolean esnumero = true;
        try{
            Double.parseDouble(cadena);
        }catch(Exception e){
            esnumero = false;
        }
        return esnumero;
    }

    public static BigDecimal redondearDosDecimales(BigDecimal monto){
        BigDecimal as=monto.setScale(3, RoundingMode.HALF_UP);
        return as.setScale(2, RoundingMode.HALF_UP);
    }



    public static String generarCorrelativo() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
//        String dateString = simpleDateFormat.format(new Date());
        return  UUID.randomUUID().toString();
    }
}
