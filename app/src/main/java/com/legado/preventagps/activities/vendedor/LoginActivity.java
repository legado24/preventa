package com.legado.preventagps.activities.vendedor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.supervisor.InicioSupervisorActivity;
import com.legado.preventagps.api.ApiDni;
import com.legado.preventagps.api.ApiGpsInka;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.PERFILESUSUARIO;
import com.legado.preventagps.json.JsonDni;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.json.JsonUrl;
import com.legado.preventagps.modelo.PaqueteUsuario;
import com.legado.preventagps.util.Metodos;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;
import com.pd.chocobar.ChocoBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.txtUsername)
    TextInputEditText txtUsername;
    @BindView(R.id.txtPassword)
    TextInputEditText txtPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.float_label_username)
    TextInputLayout float_label_username;
    @BindView(R.id.float_label_password)
    TextInputLayout float_label_password;
    @BindView(R.id.logo_gps)
    ImageView logo_gps;
    @BindView(R.id.login_progress)
    View login_progress;
    @BindView(R.id.login_form)
    View login_form;

    @BindView(R.id.linkManual)
    TextView linkManual;

    String mac = null;
    private SessionUsuario sessionUsuario;
private String imei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sessionUsuario = new SessionUsuario(getApplicationContext());
        linkManual.setMovementMethod(LinkMovementMethod.getInstance());
        guardarUrlPreventa();
        consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
         toUpper();
        clickBtnLogin();
        validarLogin();
        obtenerMac();
    }

    private void toUpper() {
        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtUsername.setText(s);
                }
                txtUsername.setSelection(txtUsername.getText().length());
            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtPassword.setText(s);
                }
                txtPassword.setSelection(txtPassword.getText().length());
            }
        });
    }
    static final Integer PHONESTATS = 0x1;
    private void clickBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                if(imei!=null){
                    login();
                }else{

                    consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
               }

            }
        });
    }
    private void obtenerMac() {
        try {
            mac = Metodos.parsingEmptyUppercase(UtilAndroid.getMacAddress());
            if (mac == null) {
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);
            }
            while (mac == null) {
                mac = Metodos.parsingEmptyUppercase(UtilAndroid.getMacAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void validarLogin() {
        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                 } else {
                    validarCampos();
                }
            }
        });

        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validarCampos();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validarCampos();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showLoginError(String error) {
        ChocoBar.builder().setBackgroundColor( getResources().getColor(R.color.colorError)).setTextSize(12)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(error)
                .setActionText("Cerrar")
                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.amber900))
                .setActionTextSize(12)
                .setMaxLines(4)
                .centerText()
                .setActionTextSize(12)
                .setActionTextTypefaceStyle(Typeface.BOLD)
                .setIcon(R.drawable.ic_error_outline_white_48dp)
                .setActivity(this)
                .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();
    }

    private void showProgress(boolean show) {
        login_progress.setVisibility(show ? View.VISIBLE : View.GONE);
        int visibility = show ? View.GONE : View.VISIBLE;
        logo_gps.setVisibility(visibility);
        login_form.setVisibility(visibility);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void login() {
        float_label_username.setError(null);
        float_label_password.setError(null);
        final String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(password)) {
            float_label_password.setError(getString(R.string.error_field_password_required));
            focusView = float_label_password;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            float_label_password.setError(getString(R.string.error_invalid_password));
            focusView = float_label_password;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            float_label_username.setError(getString(R.string.error_field_username_required));
            focusView = float_label_username;
            cancel = true;
        } else if (!isUserValid(username)) {
            float_label_username.setError(getString(R.string.error_invalid_username));
            focusView = float_label_username;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {
            try {
                showProgress(true);
                Map<String, String> dataConsulta = new HashMap<>();
                dataConsulta.put("usuario", username);
                dataConsulta.put("password", password);
                dataConsulta.put("mac", mac == null ? "0000" : mac);
                dataConsulta.put("imei", mac == null ? "0000" : imei);
                Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getLoginService().login(dataConsulta);
                loginCall.enqueue(new Callback<JsonRespuesta>() {
                    @Override
                    public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                        if (!response.isSuccessful()) {
                            String error = "Ha ocurrido un error. Contacte al administrador";
                            if (response.errorBody()
                                    .contentType()
                                    .subtype()
                                    .equals("json")) {
                            } else {
                                Log.d("LoginActivity", response.message().toString());
                            }
                            showLoginError("Servicio caído!! " + response.message().toString());
                            showProgress(false);
                            // showLoginError(error);
                            return;
                        }

                        if (response.body().getEstado() == 1) {
                            ArrayList objLogin = (ArrayList) response.body().getItem();
                            PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
                            paqueteUsuario.setUsuario(objLogin.get(0).toString());
                            paqueteUsuario.setCodPerfil(objLogin.get(1).toString());
                            paqueteUsuario.setCodCanal(objLogin.get(5).toString());
                            paqueteUsuario.setDescCanal(objLogin.get(6).toString());
                            paqueteUsuario.setCodEmpresa(objLogin.get(7).toString());
                            paqueteUsuario.setIgnoreGps(new Double(objLogin.get(8).toString()).intValue());
                            sessionUsuario.guardarPaqueteUsuario(paqueteUsuario);
                            sessionUsuario.IniciarSession(true, username, mac);
                            sessionUsuario.guardarUsuario(username);

//
//                        FirebaseInstanceId.getInstance().getInstanceId()
//                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                                        if (!task.isSuccessful()) {
//                                            Log.w("", "getInstanceId failed", task.getException());
//                                            return;
//                                        }
//
//                                        // Get new Instance ID token
//                                        String token = task.getResult().getToken();
//
//                                       // String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//                                        paqueteUsuario.setToken(token);
//                                        paqueteUsuario.setMac(mac);
//                                        // paqueteUsuario.setImei(obtenerIMEI());
//                                        registrarToken(paqueteUsuario);
//
//                                        if( paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.VENDEDOR.getClave())){
//                                            showVendedorInicioActivity();
//                                        }else if(paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.COORDINADOR.getClave())||paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.JEFEDEVENTAS.getClave())){
//                                            showSupervisorInicioActivity();
//                                        }
//
//                                    }
//                                });

                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            paqueteUsuario.setToken(refreshedToken);
                            paqueteUsuario.setMac(mac);
                            // paqueteUsuario.setImei(obtenerIMEI());
                            registrarToken(paqueteUsuario);

                            //if( paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.VENDEDOR.getClave())){
                            showVendedorInicioActivity();
//                        }else if(paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.COORDINADOR.getClave())||paqueteUsuario.getCodPerfil().equals(PERFILESUSUARIO.JEFEDEVENTAS.getClave())){
//                            showSupervisorInicioActivity();
//                        }

                        } else {
                            showLoginError(response.body().getMensaje());
                            showProgress(false);
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                        showProgress(false);
                        showLoginError(t.getMessage());
                    }
                });
            }catch(Exception ex){
                showLoginError(ex.getMessage());
                showProgress(false);
            }

            }
    }

    private void registrarToken(PaqueteUsuario paqueteUsuario) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getLoginService().registrarToken(paqueteUsuario);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {

                } else {


                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {

            }
        });
    }
    private boolean isUserValid(String username) {
        return username.length() <= 8;
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }
    public void validarCampos() {
        float_label_username.setError(null);
        float_label_password.setError(null);
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            float_label_password.setError(getString(R.string.error_field_password_required));
        } else if (!isPasswordValid(password)) {
            float_label_password.setError(getString(R.string.error_invalid_password));
        }
        if (TextUtils.isEmpty(username)) {
            float_label_username.setError(getString(R.string.error_field_username_required));
        } else if (!isUserValid(username)) {
            float_label_username.setError(getString(R.string.error_invalid_username));
        }
    }

    private void showVendedorInicioActivity() {
        startActivity(new Intent(this, InicioActivity.class));
        finish();
    }

    private void showSupervisorInicioActivity() {
        startActivity(new Intent(this, InicioSupervisorActivity.class));
        finish();
    }

    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = obtenerIMEI();
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // Validamos si el usuario acepta el permiso para que la aplicación acceda a los datos internos del equipo, si no denegamos el acceso
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    imei = obtenerIMEI();

                } else {

                    Toast.makeText(LoginActivity.this, "Has negado el permiso a la aplicación", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private String obtenerIMEI() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //return telephonyManager.getImei();
            return  Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        else {
            return telephonyManager.getDeviceId();
        }

    }

    public   String getImei(Context c) {
        TelephonyManager telephonyManager = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                checkMyPermissionImei();
                return null;
            }
        }
        return telephonyManager.getDeviceId();
    }



    private void checkMyPermissionImei() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermissionImei(this);
        }
    }

    public void guardarUrlPreventa(){
        sessionUsuario.guardarUrlPreventa(null);
        Map<String, String> dataConsulta = new HashMap<>();
        Call<JsonUrl> call =   ApiGpsInka.getInstance().getUrlsService().getUrl();
        call.enqueue(new Callback<JsonUrl>() {
            @Override
            public void onResponse(Call<JsonUrl> call, Response<JsonUrl> response) {
                if(response.body()!=null){
                    sessionUsuario.guardarUrlPreventa(response.body().getDatos().getURL_PREVENTA());
                }else{
                 //  sessionUsuario.guardarUrlPreventa("http://190.223.55.172:8080/PREVENTA_PROD2_1_1/preventaGps/");
                   sessionUsuario.guardarUrlPreventa("http://6d932aa3bc20.ngrok.io/preventaGps/");
                    //sessionUsuario.guardarUrlPreventa("http://190.223.55.172:8080/PREVENTA_TEST/preventaGps/");
                }


            }

            @Override
            public void onFailure(Call<JsonUrl> call, Throwable t) {
             //  sessionUsuario.guardarUrlPreventa("http://190.223.55.172:8080/PREVENTA_PROD2_1_1/preventaGps/");
                sessionUsuario.guardarUrlPreventa("http://6d932aa3bc20.ngrok.io/preventaGps/");
               // sessionUsuario.guardarUrlPreventa("http://190.223.55.172:8080/PREVENTA_TEST/preventaGps/");
            }
        });
    }



    public void getDni(){

        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("token","1ciChKr2cdGhpJAf6LZEDoVgRaota66xZliijyMnLDMX5XJtqWZHYFgqigyX");
        dataConsulta.put("dni","43491434");
        Call<JsonDni> call =   ApiDni.getInstance().getUrlsService().getDni(dataConsulta);
        call.enqueue(new Callback<JsonDni>() {
            @Override
            public void onResponse(Call<JsonDni> call, Response<JsonDni> response) {
                if(response.body()!=null){

                    System.out.println(response);
                }


            }

            @Override
            public void onFailure(Call<JsonDni> call, Throwable t) {
                System.out.println(t.getCause());
            }
        });
    }
}
