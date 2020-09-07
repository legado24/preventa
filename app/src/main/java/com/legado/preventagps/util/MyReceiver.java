package com.legado.preventagps.util;



import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.core.content.FileProvider;

import com.legado.preventagps.BuildConfig;

import java.io.File;

public class MyReceiver extends BroadcastReceiver {

    DownloadManager my_DownloadManager;
    long tamaño;
    IntentFilter my_IntentFilter;

    private Context my_context;
    private Activity my_activity;

    public MyReceiver(Activity activity_) {
        this.my_context=activity_;
        this.my_activity=activity_;

        my_IntentFilter=new IntentFilter();
        my_IntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("Evento_Action",intent.getAction());

        String action=intent.getAction();

        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
            DownloadManager.Query query=new DownloadManager.Query();
            query.setFilterById(tamaño);

            Cursor cursor=my_DownloadManager.query(query);

            if(cursor.moveToFirst()){
                int columnIndex=cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if(DownloadManager.STATUS_SUCCESSFUL==cursor.getInt(columnIndex)){
                    String uriString=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    File file=new File(uriString.replace("file:",""));

                    Intent pantallaInstall=new Intent(Intent.ACTION_VIEW);
                    pantallaInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri=null;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri= FileProvider.getUriForFile(context, "com.legado.preventagps.provider", file);
                    }else{
                        uri= Uri.parse(uriString);

                    }

                      pantallaInstall.setDataAndType(uri,"application/vnd.android.package-archive");

                    pantallaInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    my_activity.startActivity(pantallaInstall);

                    Log.e("MsjDescarga","Se descargo sin problemas");

                }
            }



        }

    }

    public void descargar(String url){
        // String url="https://firebasestorage.googleapis.com/v0/b/descargar-1fffc.appspot.com/o/Peregrine.apk?alt=media&token=e7b741b4-68ec-43f5-9359-5d66f1d67492";
        DownloadManager.Request my_Request;

        my_DownloadManager=(DownloadManager) my_context.getSystemService(Context.DOWNLOAD_SERVICE);

        my_Request=new DownloadManager.Request(Uri.parse(url));
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        String name= URLUtil.guessFileName(url,null,fileExtension);

       /* //crear la carpeta
        File miFile=new File(Environment.getExternalStorageDirectory(),"apk");
        boolean isCreada=miFile.exists();

        if(isCreada==false){
            isCreada=miFile.mkdirs();
        }


        my_Request.setDestinationInExternalPublicDir("/apk",name);

        String h=my_Request.setDestinationInExternalPublicDir("/apk",name).toString();

        Log.e("ruta_apk",h);

        Log.e("Descargar","Ok");*/
       /* String destination = my_activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
        destination = destination + name;
        Uri uri = Uri.parse("file://" + destination);
        File file = new File(destination);
        if (file.exists()) {
            file.delete();
        }
        my_Request.setMimeType("application/vnd.android.package-archive");
        my_Request.setDestinationUri(uri);

        tamaño=my_DownloadManager.enqueue(my_Request);*/
        //crear la carpeta
        File miFile=new File(Environment.getExternalStorageDirectory(),"apk");
        boolean isCreada=miFile.exists();

        if(isCreada==false){
            isCreada=miFile.mkdirs();
        }
        my_Request.setDestinationInExternalPublicDir("/apk",name);

        String h=my_Request.setDestinationInExternalPublicDir("/apk",name).toString();

        Log.e("ruta_apk",h);

        Log.e("Descargar","Ok");

        tamaño=my_DownloadManager.enqueue(my_Request);






    }

    public void registrar(MyReceiver oMyReceiver){
        my_context.registerReceiver(oMyReceiver,my_IntentFilter);
    }

    public  void borrarRegistro(MyReceiver oMyReceiver){
        my_context.unregisterReceiver(oMyReceiver);
    }


}
