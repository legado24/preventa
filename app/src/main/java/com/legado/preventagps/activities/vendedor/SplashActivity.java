package com.legado.preventagps.activities.vendedor;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.supervisor.InicioSupervisorActivity;
import com.legado.preventagps.api.ApiGpsInka;
import com.legado.preventagps.enums.PERFILESUSUARIO;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.json.JsonUrl;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.view.kbv.KenBurnsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=3000;
    public static final String SPLASH_SCREEN_OPTION_1 = "Option 1";
    public static final String SPLASH_SCREEN_OPTION_2 = "Option 2";
    public static final String SPLASH_SCREEN_OPTION_3 = "Option 3";
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.welcome_text)
    TextView etWelcome;
    @BindView(R.id.ken_burns_images)
    KenBurnsView  ken_burns_images;

    SessionUsuario sessionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
     //FirebaseMessaging.getInstance().subscribeToTopic("NEWSS");
         sessionUsuario=new SessionUsuario(this);
        ken_burns_images.setImageResource(R.drawable.fondodemoda);

       // Toast.makeText(this,"datos notific"+getIntent().hasExtra("pushnotification"),Toast.LENGTH_LONG).show();
     if(getIntent().hasExtra("pushnotification")){
            Toast.makeText(this,"datos notific"+getIntent().getExtras().get("body"),Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(SplashActivity.this, InicioActivity.class);
//            intent.putExtra("key",getIntent().getExtras().get("key").toString());
//            startActivity(intent);,
//            finish();
       }


       // checkMyPermissionLocation();
        setAnimation(SPLASH_SCREEN_OPTION_3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionUsuario = new SessionUsuario(SplashActivity.this);
                if (sessionUsuario.getSession()) {

                //  if(sessionUsuario.getPaqueteUsuario().getCodPerfil().equals(PERFILESUSUARIO.VENDEDOR.getClave()))  {
                      Intent intent = new Intent(SplashActivity.this, InicioActivity.class);
                      if(getIntent().hasExtra("pushnotification")){
                          intent.putExtra("pushnotification","true");
                          intent.putExtra("title",getIntent().getExtras().get("title").toString());
                          intent.putExtra("body",getIntent().getExtras().get("body").toString());
                      }

                      startActivity(intent);
                      finish();
//                  }else if(sessionUsuario.getPaqueteUsuario().getCodPerfil().equals(PERFILESUSUARIO.COORDINADOR.getClave())||sessionUsuario.getPaqueteUsuario().getCodPerfil().equals(PERFILESUSUARIO.JEFEDEVENTAS.getClave())){
//                      Intent intent = new Intent(SplashActivity.this, InicioSupervisorActivity.class);
//                      startActivity(intent);
//                      finish();
//                  }


                } else {
                         Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                         startActivity(intent);
                        finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void setAnimation(String category) {
        if (category.equals(SPLASH_SCREEN_OPTION_1)) {
            animation1();
        } else if (category.equals(SPLASH_SCREEN_OPTION_2)) {
            animation2();
        } else if (category.equals(SPLASH_SCREEN_OPTION_3)) {
            animation2();
            animation3();
        }
    }


    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(logo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(logo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(logo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        logo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        logo.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(etWelcome, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }






}
