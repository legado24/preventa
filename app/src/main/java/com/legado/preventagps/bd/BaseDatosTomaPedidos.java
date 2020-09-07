package com.legado.preventagps.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;

/**
 * Created by __Adrian__ on 3/5/2017.
 */

public class BaseDatosTomaPedidos extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE_DATOS = "preventagps.db";
    private static final int VERSION_ACTUAL = 6;
    private final Context contexto;

    interface Tablas {

        String CLIENTE = "cliente";
        String MONTOS_VENDEDOR = "montos_vendedor";
        String CABECERA_MONTOS_VENDEDOR = "cabecera_montos";
        String DATOS_METRICAS = "datos_metricas";
        String DATOS_CLIENTE = "datos_cliente";

        //offline
        String DATOS_USUARIO = "datos_usuario";
        String CONDICIONES_PAGO = "condiciones_pago";

        String ARTICULO="articulo";
        String SUGERIDO_LOCAL="sugerido_local";

        String CABECERA_PEDIDO_LOCAL="cabecera_pedido_local";
        String DETALLE_PEDIDO_LOCAL="detalle_pedido_local";
        String NO_PEDIDO_LOCAL="no_pedido_local";
        String FOCUS_LOCAL="focus_local";



    }

    public BaseDatosTomaPedidos(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }


//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//        if (!db.isReadOnly()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                db.setForeignKeyConstraintsEnabled(true);
//            } else {
//                db.execSQL("PRAGMA foreign_keys=ON");
//            }
//        }
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {



            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT   NULL ," +
                            "%s DATE   NULL ," +
                            "%s TEXT   NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT   NULL," +
                            "%s TEXT   NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT   NULL," +
                            "%s TEXT  NULL," +
                            "%s INTEGER  NULL," +
                            "%s INTEGER   NULL," +
                            "%s INTEGER   NULL," +
                            "%s INTEGER   NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s DOUBLE   NULL," +
                            "%s DOUBLE   NULL," +
                            "%s DOUBLE   NULL," +
                            "%s TEXT   NULL," +
                            "%s DATE   NULL ," +
                            "%s DOUBLE   NULL," +
                            "%s INTEGER   NULL," +
                            "%s TEXT   NULL," +

                            "%s TEXT  NULL," +
                            "%s TEXT   NULL," +
                            "%s TEXT  NULL," +
                            "%s INTEGER  NULL" +
                            ")",
                    Tablas.CLIENTE,
                    ContratoPreventa.ColumnasCliente.CODIGO,
                    ContratoPreventa.ColumnasCliente.FECHA,
                    ContratoPreventa.ColumnasCliente.DESC_CLIENTE,
                    ContratoPreventa.ColumnasCliente.COORDENADAS,
                    ContratoPreventa.ColumnasCliente.CODRUTA,
                    ContratoPreventa.ColumnasCliente.DESCRUTA,
                    ContratoPreventa.ColumnasCliente.DIRECCION,
                    ContratoPreventa.ColumnasCliente.COD_LOCAL,
                    ContratoPreventa.ColumnasCliente.COD_LISTA,
                    ContratoPreventa.ColumnasCliente.COD_EMPRESA,
                    ContratoPreventa.ColumnasCliente.FLAG_DESPACHADO,
                    ContratoPreventa.ColumnasCliente.FLAG_IGNORESEC,
                    ContratoPreventa.ColumnasCliente.ESTADO_JORNADA,
                    ContratoPreventa.ColumnasCliente.ESTADO_GEOSIS,
                    ContratoPreventa.ColumnasCliente.ESTADO_CLIENTE,
                    ContratoPreventa.ColumnasCliente.TIPO_NEGOCIO,
                    ContratoPreventa.ColumnasCliente.DESC_LISTA,
                    ContratoPreventa.ColumnasCliente.LIMITE_CREDITO,
                    ContratoPreventa.ColumnasCliente.DEUDA_VENCIDA,
                    ContratoPreventa.ColumnasCliente.DEUDA_NOVENCIDA,
                    ContratoPreventa.ColumnasCliente.ANTIGUEDAD,
                    ContratoPreventa.ColumnasCliente.CUMPLEAÑOS,
                    ContratoPreventa.ColumnasCliente.VELOCIDAD_PAGO,
                    ContratoPreventa.ColumnasCliente.CANT_PEDIDOS,
                    ContratoPreventa.ColumnasCliente.NEW_TELEFONO,

                    ContratoPreventa.ColumnasCliente.NOMBRES,
                    ContratoPreventa.ColumnasCliente.APELLIDOS,
                    ContratoPreventa.ColumnasCliente.NEW_DIRECCION,
                    ContratoPreventa.ColumnasCliente.IS_AUDITADO



            ));


            db.execSQL(String.format("CREATE TABLE %s (%s DOUBLE  NOT NULL )",
                    Tablas.MONTOS_VENDEDOR,
                    ContratoPreventa.ColumnasMontosVendedor.MONTO));


            db.execSQL(String.format("CREATE TABLE %s (%s TEXT  NOT NULL)",
                    Tablas.CABECERA_MONTOS_VENDEDOR,
                    ContratoPreventa.ColumnasCabeceraMontosVendedor.CABECERA));




            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s DOUBLE  NOT NULL," +
                            "%s DOUBLE  NOT NULL," +
                            "%s TEXT  NOT NULL)",
                    Tablas.DATOS_CLIENTE,
                    ContratoPreventa.ColumnasDatosCliente.COD_CLIENTE,
                    ContratoPreventa.ColumnasDatosCliente.COD_LOCAL,
                    ContratoPreventa.ColumnasDatosCliente.TIPO_NEGOCIO,
                    ContratoPreventa.ColumnasDatosCliente.COD_LISTA,
                    ContratoPreventa.ColumnasDatosCliente.DESC_LISTA,
                    ContratoPreventa.ColumnasDatosCliente.LIMITE_CREDITO,
                    ContratoPreventa.ColumnasDatosCliente.DEUDA_VENCIDA,
                    ContratoPreventa.ColumnasDatosCliente.DEUDA_NOVENCIDA,
                    ContratoPreventa.ColumnasDatosCliente.ANTIGUEDAD,
                    ContratoPreventa.ColumnasDatosCliente.CUMPLEAÑOS,
                    ContratoPreventa.ColumnasDatosCliente.VELOCIDAD_PAGO,
                    ContratoPreventa.ColumnasDatosCliente.CANT_PEDIDOS

            ));


//offline
            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL," +
                            "%s INTEGER  NOT NULL," +
                            "%s INTEGER  NOT NULL)",
                    Tablas.DATOS_USUARIO,
                    ContratoPreventa.ColumnasDatosUsuario.COD_EMPRESA,
                    ContratoPreventa.ColumnasDatosUsuario.COD_MESA,
                    ContratoPreventa.ColumnasDatosUsuario.DESC_MESA,
                    ContratoPreventa.ColumnasDatosUsuario.TIPO_CODIGO,
                    ContratoPreventa.ColumnasDatosUsuario.COD_LOCALIDAD,
                    ContratoPreventa.ColumnasDatosUsuario.DESC_LOCALIDAD,
                    ContratoPreventa.ColumnasDatosUsuario.COD_CANAL,
                    ContratoPreventa.ColumnasDatosUsuario.DESC_CANAL,
                    ContratoPreventa.ColumnasDatosUsuario.COD_VENDEDOR,

                    ContratoPreventa.ColumnasDatosUsuario.COD_ALMACEN,
                    ContratoPreventa.ColumnasDatosUsuario.DESC_ALMACEN,


                    ContratoPreventa.ColumnasDatosUsuario.COD_SEDE,
                    ContratoPreventa.ColumnasDatosUsuario.PERMITE_CREDITO,
                    ContratoPreventa.ColumnasDatosUsuario.PERMITE_CAMPANIA

            ));

            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s INTEGER  NOT NULL,"+
                            "%s TEXT  NOT NULL)",
                    Tablas.CONDICIONES_PAGO,
                    ContratoPreventa.ColumnasCondiciones.COD_CONDICION,
                    ContratoPreventa.ColumnasCondiciones.DESC_CONDICION,
                    ContratoPreventa.ColumnasCondiciones.TIPO_CONDICION,
                    ContratoPreventa.ColumnasCondiciones.COD_LISTA


            ));



            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s DOUBLE  NOT NULL,"+
                            "%s DOUBLE  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s TEXT  NOT NULL,"+
                            "%s DOUBLE  NOT NULL)",
                    Tablas.ARTICULO,
                    ContratoPreventa.ColumnasArticulos.COD_ITEM,
                    ContratoPreventa.ColumnasArticulos.DESC_ITEM,
                    ContratoPreventa.ColumnasArticulos.UM_ITEM,
                    ContratoPreventa.ColumnasArticulos.COD_LISTA,
                    ContratoPreventa.ColumnasArticulos.PRECIO_BASE,
                    ContratoPreventa.ColumnasArticulos.SALDO,
                    ContratoPreventa.ColumnasArticulos.COD_ALMACEN,
                    ContratoPreventa.ColumnasArticulos.COND_PAGO,
                    ContratoPreventa.ColumnasArticulos.PRECIO_SUGERIDO

            ));




            db.execSQL(String.format("CREATE TABLE %s ( " +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "%s TEXT  NULL ," +
                            "%s TEXT  NULL," +
//                            "%s TEXT  NULL," +
                            "%s TEXT NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s DOUBLE  NULL," +
                            "%s CURRENT_TIMESTAMP  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL" +
                            ")",
                    Tablas.CABECERA_PEDIDO_LOCAL,
                    BaseColumns._ID,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.ID,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOEMPRESA,
                     ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLOCALIDAD,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCLOCALIDAD,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODALMACEN,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCALMACEN,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOCLIENTE,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCCLIENTE,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCONDICIONPAGO,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLISTAPRECIOS,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODDIRECCION,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.MONTOVENTA,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODSEDE,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODMESA,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCANAL,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.COORDENADAS,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODRUTA,
                    ContratoPreventa.ColumnasCabeceraPedidoLocal.CODVENDEDOR
            ));


            db.execSQL(String.format("CREATE TABLE %s ( " +
                            "%s TEXT NOT NULL ," +
                            "%s TEXT  NULL," +
                            "%s TEXT  NULL," +
                            " %s TEXT  NULL," +
                            "%s TEXT  NULL, " +
                            "%s DOUBLE NULL," +
                            "%s DOUBLE NULL," +
                            "%s INTEGER  NULL, " +
                            "FOREIGN KEY (%s) " +
                            "REFERENCES %s (%s))",
                    Tablas.DETALLE_PEDIDO_LOCAL,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOARTICULO,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.DESCITEM,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.UM,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOLISTA,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOBASE,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOSUGERIDO,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.CANTIDAD,
                    ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO,
                    Tablas.CABECERA_PEDIDO_LOCAL, ContratoPreventa.ColumnasCabeceraPedidoLocal.ID));




            db.execSQL(String.format("CREATE TABLE %s ( " +
               "%s TEXT NOT NULL ," +
                "%s TEXT  NULL," +
                 "%s TEXT NULL," +
                 "%s TEXT  NULL," +
                 "%s TEXT  NULL," +
                 "%s TEXT  NULL," +
                 "%s TEXT  NULL," +
                  "%s TEXT  NULL," +
                   "%s TEXT  NULL," +
                    "%s CURRENT_TIMESTAMP  NULL," +
                     "%s TEXT  NULL," +
                    "%s TEXT  NULL)",
                    Tablas.NO_PEDIDO_LOCAL,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.ID,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.CODIGOEMPRESA,
                     ContratoPreventa.ColumnasTablaNoPedidoLocal.USUARIOXRAY,
                     ContratoPreventa.ColumnasTablaNoPedidoLocal.CODCLIENTE,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCCLIENTE,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.CODRUTA,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.MOTIVONOPEDIDO,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.CODDIRECCION,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCDIRDESPACHO,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.FECHAREGISTRO,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL,
                    ContratoPreventa.ColumnasTablaNoPedidoLocal.COORDENADAS
            ));


            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s DATE  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s DOUBLE  NOT NULL," +
                            "%s DOUBLE  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s INTEGER  NOT NULL)",
                    Tablas.DATOS_METRICAS,
                    ContratoPreventa.ColumnasDatosMetricas.VENDEDOR,
                    ContratoPreventa.ColumnasDatosMetricas.FECHA,
                    ContratoPreventa.ColumnasDatosMetricas.PERIODO,
                    ContratoPreventa.ColumnasDatosMetricas.TICKET_DIARIO,
                    ContratoPreventa.ColumnasDatosMetricas.TICKET_MENSUAL,
                    ContratoPreventa.ColumnasDatosMetricas.RUTASHOY,
                    ContratoPreventa.ColumnasDatosMetricas.PERMITE_OFFLINE

            ));




            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                            "%s TEXT  NOT NULL," +
                            "%s INTEGER  NOT NULL," +
                            "%s INTEGER  NOT NULL)" ,
                    Tablas.SUGERIDO_LOCAL,
                    ContratoPreventa.ColumnasSugeridoLocal.CODCLIENTE,
                    ContratoPreventa.ColumnasSugeridoLocal.COD_ITEM,
                    ContratoPreventa.ColumnasSugeridoLocal.CANT_SUGERIDA,
                    ContratoPreventa.ColumnasSugeridoLocal.NRO_VECES

            ));

            db.execSQL(String.format("CREATE TABLE %s (" +
                            "%s TEXT  NOT NULL," +
                             "%s INTEGER  NOT NULL)" ,
                    Tablas.FOCUS_LOCAL,
                    ContratoPreventa.ColumnasFocusLocal.COD_ITEM,
                    ContratoPreventa.ColumnasFocusLocal.CANT_FOCUS

            ));





//
//            ));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CLIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.MONTOS_VENDEDOR);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CABECERA_MONTOS_VENDEDOR);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.DATOS_METRICAS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.DATOS_CLIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.DATOS_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CONDICIONES_PAGO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.ARTICULO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.SUGERIDO_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CABECERA_PEDIDO_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.DETALLE_PEDIDO_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.NO_PEDIDO_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.FOCUS_LOCAL);





        onCreate(db);
    }

}


