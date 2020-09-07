package com.legado.preventagps.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiRetrofitMoreLong;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.util.SessionUsuario;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

//import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanificadorFragment extends Fragment implements OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
//    @BindView(R.id.reciclador)
//    RecyclerView reciclador;
//    @BindView(R.id.list_empty)
//    TextView txtEmpty;
//    private RecyclerView.LayoutManager lManager;
//    @BindView(R.id.swiperefresh)
//    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
    private SessionUsuario sessionUsuario;
//    PlanificadorRecyclerAdapter planificadorRecyclerAdapter;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.btnGenerarReporte)
    Button btnGenerarReporte;

//    @BindView(R.id.txtProgressPercent)
//    TextView txtProgressPercent;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    DownloadPdfFileTask downloadPdfFileTask;
    private static final int PERMISSION_REQUEST_CODE = 1;
     File destinationFile;
   private final static int REQUEST_CODE = 42;
    public static PlanificadorFragment newInstance() {
        PlanificadorFragment fragment = new PlanificadorFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planificador, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(getContext());
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("GENERANDO PLANIFICADOR..");
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission();
//        if(checkPermission()) {
         generarReporte();
//        }else {
//
//            requestPermissions(permissions,REQUEST_CODE);
//            //requestPermission();
//        }

        setHasOptionsMenu(true);

        btnGenerarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                  showDialog();
                }else {
                    requestPermission();
                }            }
        });
        return rootView;
    }

    public void showDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea volver a consultar el Reporte actualizado?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cargarPlanificador();

                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    public void cargarPlanificador(){
    progressDialog.show();
    Map<String, String> dataConsulta = new HashMap<>();
    dataConsulta.put("codEmpresa","");
    dataConsulta.put("codSede","");
    dataConsulta.put("codMesa","");
    dataConsulta.put("codCanal","");
    dataConsulta.put("usuario",sessionUsuario.getUsuario());
    dataConsulta.put("fecha","");
    dataConsulta.put("codProveedor","");

    Call<ResponseBody> call =   ApiRetrofitMoreLong.getInstance(sessionUsuario.getUrlPreventa()).getPlanificadorService().generarPlanificador(dataConsulta);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if(response.code()==401){
                Toast.makeText(
                        getActivity(), "SE EXPIRÓ EL TIEMPO DE LA TOMA DE PEDIDOS PARA SU USUARIO ,POR FAVOR COMUNIQUESE CON SU COORDINADOR.",
                        Toast.LENGTH_LONG).show();
                //  progressDialog.dismiss();
            }else if(response.code()==403) {
                Toast.makeText(getActivity(), "USUARIO INACTIVO, COMUNIQUESE CON CON SU COORDINADOR.",
                        Toast.LENGTH_LONG).show();
                //   progressDialog.dismiss();
            }else if(response.code()==404){
                    Toast.makeText(getActivity(), "EL SUPERVISOR AUN NO HA CARGADO SU PLANIFICADOR!!",
                            Toast.LENGTH_LONG).show();
                      progressDialog.dismiss();

            }else {
                System.out.println(response.body());
                downloadPdfFileTask = new DownloadPdfFileTask();
                downloadPdfFileTask.execute(response.body());
//                pdfView.fromUri(Uri)
//                pdfView.fromFile(File)
//                pdfView.fromBytes(byte[])
//                pdfView.fromStream(InputStream)
//                pdfView.fromAsset(String)
                progressDialog.dismiss();
//
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            Snackbar.make(getView(), "Problemas de conexión  " , Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cargarPlanificador();

                        }
                    }).show();

        }
    });
}

    public void generarReporte(){
        destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Planificador.pdf");
            if(destinationFile==null){
                cargarPlanificador();
            }else{
                pdfView.fromFile(destinationFile).enableDoubletap(true)

             .load();

                pdfView.setMinZoom(3);
                pdfView.setMaxZoom(3);
                pdfView.setMidZoom(3);
                //pdfView.setCameraDistance(8);
                // pdfView.setMaxZoom(10);
                pdfView.resetZoom();


            }
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }



    private class DownloadPdfFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            try {
                saveToDisk(urls[0], "Planificador.pdf");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100)
                Toast.makeText(getContext(), "Planificador generado Correctamente!!", Toast.LENGTH_SHORT).show();


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                //progressBar.setProgress(currentProgress);

//                txtProgressPercent.setText("Progress " + currentProgress + "%");

            }

            if (progress[0].first == -1) {
                Toast.makeText(getContext(), "Error al generar el planificador", Toast.LENGTH_SHORT).show();
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {
//            pdfView.fromFile(destinationFile)
//                    .enableSwipe(true) /* allows to block changing pages using swipe*/
//                    .swipeHorizontal(false)

//                    .load();

            pdfView.fromFile(destinationFile).enableDoubletap(true)


                    .load();

    pdfView.setMinZoom(3);
        pdfView.setMaxZoom(3);
            pdfView.setMidZoom(3);
            //pdfView.setCameraDistance(8);
           // pdfView.setMaxZoom(10);
            pdfView.resetZoom();

        }
    }

    private void saveToDisk(ResponseBody body, String filename) throws IOException {
        try {

               destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
//                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadPdfFileTask.doProgress(pairs);
                    Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d(TAG, destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadPdfFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadPdfFileTask.doProgress(pairs);
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 101) {
                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
              //  cargarPlanificador();
                generarReporte();
            } else {
                permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, REQUEST_CODE);
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
private boolean checkPermission(){
    int result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (result == PackageManager.PERMISSION_GRANTED){
        return true;
    } else {
       // checkPermission();
        return false;
    }
}

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }


}
