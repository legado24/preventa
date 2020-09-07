package com.legado.preventagps.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Location setting and permission check
 */

public final class PermissionUtils {
    public static final int REQUEST_CODE = 400;
    /**
     * Runtime Permission Check
     */
    public static void requestPermission( Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                                      Manifest.permission.ACCESS_FINE_LOCATION},
                                      REQUEST_CODE);
    }

    public static void requestPermissionImei( Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE);
    }

    public static void requestPermissionStorageWrite( Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE);
    }

    public static void requestPermissionStorageRead( Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE);
    }

    /**
     * Common methods to determine if a location permission is granted
     */
    public static boolean isPermissionGranted(String[] grantPermissions,
                                              int[] grantResults){
        int permissionSize = grantPermissions.length;
        for (int i = 0; i < permissionSize; i++) {
            if (Manifest.permission.ACCESS_FINE_LOCATION.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }
    /**
     * Dialog extension for permissions
     */
    public static class LocationSettingDialog extends DialogFragment{
        /**
         * Create a new instance of this dialog and click on the 'ok' button to
         * selectively advance the call activity.
         */
        SessionUsuario sessionUsuario;
        public static LocationSettingDialog newInstance() {
            return new LocationSettingDialog();
        }



        @NonNull
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState) {
            sessionUsuario=new SessionUsuario(getActivity());

            return new AlertDialog.Builder(getContext())
                    .setMessage("Necesitas configurar la ubicación de tu dispositivo.")
                    .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sessionUsuario.guardarBandSettings(true);
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(getActivity(),"ACTIVE GPS Y SELECCIONE MODO ALTA PRECISION PORFAVOR",Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                    }).create();
        }
    }
}
