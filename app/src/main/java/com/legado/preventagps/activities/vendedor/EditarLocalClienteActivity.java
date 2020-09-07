package com.legado.preventagps.activities.vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.legado.preventagps.R;
import com.legado.preventagps.enums.CLIENTEENUM;

public class EditarLocalClienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_local_cliente);
        Bundle bd=getIntent().getExtras();
        String codCliente=bd.getString("codCliente");
       String codLocal= bd.getString("codLocal");

        Log.wtf("CodCliente",codCliente);
        Log.wtf("codLocal",codLocal);
    }
}