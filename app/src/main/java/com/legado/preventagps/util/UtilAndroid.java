package com.legado.preventagps.util;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogProgress;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by __Adrian__ on 13/03/2019.
 */

public class UtilAndroid {

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String par = Integer.toHexString(b & 0xFF) + ":";
                    if (par.length() < 3) {
                        par = "0" + par; // pad with leading zero if needed
                    }
                    res1.append(par);
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return "";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String fechaDevice(String formato){
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        // Formateamos la fecha del dia.
        String resultado = sdf.format(new Date());
            return resultado;
    }



   public static void showProgressDialog(AppCompatActivity activity) {
       FragmentTransaction ft= activity.getSupportFragmentManager().beginTransaction();
       MyDialogProgress newFragment = new MyDialogProgress();
       newFragment.show(ft,"dialog");
   }

    public static String ConvertirArrayinString(List<String> listado){
        String codigos = "";
        for(int i=0;i<listado.size();i++){
            if(i==(listado.size()-1)){
                codigos+=listado.get(i);
            }else{
                codigos+=listado.get(i)+",";
            }
        }
        return codigos;
    }

//    public static void hideProgressDialog(AppCompatActivity activity) {
//        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
//        if(prev == null) {
//            //There is no active fragment with tag "dialog"
//        }else {
//            if (prev.getActivity() != activity) { //additional check
//                //There is a fragment with tag "dialog", but it is not active (shown) which means it was found on device's back stack.
//            }else{
//               prev.getFragmentManager().get
//
//            }
//            //There is an active fragment with tag "dialog"
//        }
//    }

    public static boolean estaInstaladaAplicacion(String nombrePaquete, Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(nombrePaquete, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

//    public static boolean servicioEjecutandose(String nombreServicio, Context context) {
//
//        PackageManager pm = context.getPackageManager();
//        try {
//            pm.getPackageInfo(nombreServicio, PackageManager.GET_SERVICES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }

    private static boolean mResult;

    public static boolean mensajeConfirmacion(Context context, String title, String message) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //PARA CAMBIAR EL COLOR DEL MENSAJE DEL DIALOG
        SpannableString messageM = new SpannableString(message);
        messageM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
        alert.setMessage(messageM);
        //PARA CAMBIAR EL COLOR  DEL TITULO DEL DIALOG
        SpannableString titleM = new SpannableString(title);
        titleM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
        alert.setTitle(titleM);
        alert.setIcon(R.drawable.ic_help_yellow_100_24dp);
        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {

        }

        return mResult;
    }

    public static boolean mensajeInformacion(Context context, String title, String message) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //PARA CAMBIAR EL COLOR DEL MENSAJE DEL DIALOG
        SpannableString messageM = new SpannableString(message);
        messageM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
        alert.setMessage(messageM);
        //PARA CAMBIAR EL COLOR  DEL TITULO DEL DIALOG
        SpannableString titleM = new SpannableString(title);
        titleM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
        alert.setTitle(titleM);

        alert.setIcon(R.drawable.ic_info_blue_400_24dp);
        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {

        }

        return mResult;
    }

    public static void toNegrita(TextView textView){
        SpannableString content = new SpannableString(textView.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public static boolean mensajeError(Context context, String title, String message) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);


        //PARA CAMBIAR EL COLOR DEL MENSAJE DEL DIALOG
        SpannableString messageM = new SpannableString(message);
        messageM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
        alert.setMessage(messageM);
        //PARA CAMBIAR EL COLOR  DEL TITULO DEL DIALOG
        SpannableString titleM = new SpannableString(title);
        titleM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
        alert.setTitle(titleM);

        alert.setIcon(R.drawable.ic_error_red_500_24dp);
        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {

        }

        return mResult;
    }

    public static boolean mensajeExito(Context context, String title, String message) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //PARA CAMBIAR EL COLOR DEL MENSAJE DEL DIALOG
        SpannableString messageM = new SpannableString(message);
        messageM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
        alert.setMessage(messageM);
        //PARA CAMBIAR EL COLOR  DEL TITULO DEL DIALOG
        SpannableString titleM = new SpannableString(title);
        titleM.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
        alert.setTitle(titleM);
        alert.setIcon(R.drawable.ic_success);
        alert.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {

        }

        return mResult;
    }




    public static boolean isNetDisponible(Context c) {

        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public  static Integer isOffline() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable==true?0:1;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 2;
    }



    public static List<String> getListaTablas(){
        List<String> tablas=new ArrayList<>();
        tablas.add("CLIENTE ");
        tablas.add("MONTOS_VENDEDOR");
        tablas.add("CABECERA_MONTOS_VENDEDOR");
        tablas.add("DATOS_METRICAS");
        tablas.add("DATOS_CLIENTE");
        tablas.add("DATOS_USUARIO");
        tablas.add("CONDICIONES_PAGO");
        tablas.add("ARTICULO");
        tablas.add("SUGERIDO_LOCAL");
        tablas.add("CABECERA_PEDIDO_LOCAL");
        tablas.add("DETALLE_PEDIDO_LOCAL");
        tablas.add("NO_PEDIDO_LOCAL");
        tablas.add("FOCUS_LOCAL");
        tablas.add("TABLA_MAESTRA");


        return  tablas;

    }


}
