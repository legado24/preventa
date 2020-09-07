package com.legado.preventagps.util;

import android.content.Context;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;

import com.legado.preventagps.dialogs.MyDialogCalculando;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SRIVASTAVA on 1/9/2016.
 */
/*The instance of this class is called by "MainActivty",to get the time taken reach the destination from Google Distance Matrix API in background.
  This class contains interface "Geo" to call the function setDouble(String) defined in "MainActivity.class" to display the result.*/
public class GeoTask extends AsyncTask<String, Void, String> {
    MyDialogCalculando dialogProgress;
    Context mContext;
    Double duration;
    Geo geo1;
   // View view;
//constructor is used to get the context.
    public GeoTask(Context mContext,MyDialogCalculando dialogProgress) {
        this.mContext = mContext;
        this.dialogProgress=dialogProgress;
        geo1= (Geo) mContext;
    }
//This function is executed before before "doInBackground(String...params)" is executed to dispaly the progress dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        Activity activity = (Activity) mContext;
//        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager().b;
//        dialogSecuencia = new MyDialogProgress();
//        dialogSecuencia.show(ft, "dialog");
    }
//This function is executed after the execution of "doInBackground(String...params)" to dismiss the dispalyed progress dialog and call "setDouble(Double)" defined in "MainActivity.java"
    @Override
    protected void onPostExecute(String aDouble) {
        super.onPostExecute(aDouble);
        if(aDouble!=null)
        {
         geo1.setDouble(aDouble);
//            pd.dismiss();
        } else {

//            Snackbar.make(dialogProgress.getView(), "UNA COORDENADA ESTA MAL REFERENCIADA", Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialogProgress.dismiss();
////                    Toast.makeText(mContext, "Error4!Please Try Again wiht proper values", Toast.LENGTH_SHORT).show();
//
//                }
//            }).show();
        }


     }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url=new URL(params[0]);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode=con.getResponseCode();
            if(statuscode==HttpURLConnection.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=br.readLine();
                while(line!=null)
                {
                    sb.append(line);
                    line=br.readLine();
                }
//                String json=sb.toString();
//                Log.d("JSON",json);
//                JSONObject root=new JSONObject(json);
//                JSONArray array_rows=root.getJSONArray("rows");
//                Log.d("JSON","array_rows:"+array_rows);
//                JSONObject object_rows=array_rows.getJSONObject(0);
//                Log.d("JSON","object_rows:"+object_rows);
//                JSONArray array_elements=object_rows.getJSONArray("elements");
//                Log.d("JSON","array_elements:"+array_elements);
//                JSONObject  object_elements=array_elements.getJSONObject(0);
//                Log.d("JSON","object_elements:"+object_elements);
//                JSONObject object_duration=object_elements.getJSONObject("duration");
//                JSONObject object_distance=object_elements.getJSONObject("distance");
//
//                Log.d("JSON","object_duration:"+object_duration);
//                return object_duration.getString("value")+","+object_distance.getString("value");



                String jsonOutput = sb.toString();
                Log.d("JSON",jsonOutput);
                JSONObject jsonObject = new JSONObject(jsonOutput);
//routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
//Grab the first route
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legsArray = route.getJSONArray("legs");
                JSONObject legs = legsArray.getJSONObject(0);
                JSONObject distance = legs.getJSONObject("distance");
                JSONObject duration = legs.getJSONObject("duration");
               return distance.getString("value")+","+duration.getString("value");
            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Snackbar.make(dialogProgress.getView(), "UNA COORDENADA ESTA MAL REFERENCIADA "+params[1], Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogProgress.dismiss();
//                    Toast.makeText(mContext, "Error4!Please Try Again wiht proper values", Toast.LENGTH_SHORT).show();

                }
            }).show();
        }


        return null;
    }
    public interface Geo{
        public void setDouble(String min);
//        public Cliente clienteCerca(String min);

    }

}

