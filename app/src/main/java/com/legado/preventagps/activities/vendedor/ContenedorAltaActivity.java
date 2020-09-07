package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;

import com.legado.preventagps.R;
import com.legado.preventagps.fragments.AltaFragment;
import com.legado.preventagps.fragments.AltaFragmentv2;
import com.legado.preventagps.fragments.AuditarClienteFragmentv2;
import com.legado.preventagps.util.SessionUsuario;

public  class ContenedorAltaActivity extends BaseActivity {

    private SessionUsuario sessionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionUsuario=new SessionUsuario(getApplicationContext());
        sessionUsuario.guardarPaqueteAlta(null);
        setContentView(R.layout.activity_contenedor_alta);
        setupToolbar();
        Bundle args=getIntent().getExtras();
        if(args==null){
            AltaFragmentv2 fragment = (AltaFragmentv2) getSupportFragmentManager().findFragmentById(R.id.contenedorFragmentAlta);
            if (fragment == null) {
                fragment = new AltaFragmentv2();

                fragment.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.contenedorFragmentAlta, fragment)
                        .commit();
            }
        }else {

            AuditarClienteFragmentv2 fragment = (AuditarClienteFragmentv2) getSupportFragmentManager().findFragmentById(R.id.contenedorFragmentAlta);
            if (fragment == null) {
                fragment = new AuditarClienteFragmentv2(args.getString("codCliente"));

                fragment.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.contenedorFragmentAlta, fragment)
                        .commit();
            }
        }
        CardView menu1= (CardView) findViewById(R.id.menu2);
        menu1.setCardBackgroundColor(Color.parseColor("#FF9800"));

    }
    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alta_cliente, menu);
        return true;
    }
    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_logout:
                logout(sessionUsuario);
                return true;



        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

        moveTaskToBack(false);
    }
    @Override
    protected void goToNavDrawerItem(int item) {
        switch (item) {
            case R.id.menu0:
                Intent intent0 = new Intent(this, InicioActivity.class);
                startActivity(intent0);
                //finish();
                break;
            case R.id.menu1:
                Intent intent1 = new Intent(this, MenuPreventaActivity.class);
                startActivity(intent1);
                // finish();
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(this, ContenedorAltaActivity.class);
                startActivity(intent2);
                //finish();
                break;
            case R.id.menu3:
                Intent intent3 = new Intent(this, MenuCobranzaActivity.class);
                startActivity(intent3);
                // finish();
                break;
            case R.id.menu4:
                Intent intent4 = new Intent(this, MenuBandejasActivity.class);
                startActivity(intent4);
                // finish();
                break;



        }
    }

}
