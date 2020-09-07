package com.legado.preventagps.firebase;

//import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by __Adrian__ on 26/7/2017.
 */

public class MyFirebaseInstanceIDService  {
////extends FirebaseInstanceIdService
//    private static final String TAG = "MyFirebaseIIDService";
//
//    @Override
//    public void onTokenRefresh() {
//
//        super.onTokenRefresh();
//        //Getting registration token
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        //Displaying token on logcat
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        //SessionUsuario sessionUsuario=new SessionUsuario(getApplicationContext());
////        PaqueteUsuario paqueteUsuario=new PaqueteUsuario();
////        paqueteUsuario.setUsuario("ADRIAN");
////        paqueteUsuario.setToken(refreshedToken);
////        Gson gson = new Gson();
////        String paqueteUsuarioJson = gson.toJson(paqueteUsuario);
////        Log.d("paqueteUsuario",paqueteUsuarioJson);
//
//
//        //registrarToken(paqueteUsuario);
//
//
//
//    }
//
//    private void registrarToken(PaqueteUsuario paqueteUsuario) {
//        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance().getLoginService().registrarToken(paqueteUsuario);
//        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
//            @Override
//            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
//                if (response.body().getEstado() == -1) {
//
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
//
//            }
//        });
//    }
}