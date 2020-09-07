package com.legado.preventagps.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.legado.preventagps.enums.STATUSSINCRONIZACION;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.NoPedido;
import com.legado.preventagps.modelo.PreVenta;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class OperacionesBaseDatos {
    private static BaseDatosTomaPedidos baseDatos;
    private static OperacionesBaseDatos instancia = new OperacionesBaseDatos();
    private static Context context;

    private OperacionesBaseDatos() {
    }
    public SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
    public static OperacionesBaseDatos obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new BaseDatosTomaPedidos(contexto);
            context=contexto;

        }
        return instancia;
    }

    //BLOQUE CLIENTES

    public Cursor obtenerMaestroClientesByDia(String fecha){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String selectQuery = "SELECT distinct * FROM   "+BaseDatosTomaPedidos.Tablas.CLIENTE;
        selectQuery=selectQuery+" where strftime('%Y-%m-%d' ,"+ContratoPreventa.ColumnasCliente.FECHA+")";
        selectQuery = selectQuery + " = '" + fecha + "'" ;
        return db.rawQuery(selectQuery,null);
    }

    public List<Cliente> listarClientes(String fecha){
        try {
            Cursor c = obtenerMaestroClientesByDia(fecha);
            List<Cliente> lista = new ArrayList<>();
            while (c.moveToNext()) {
                Cliente cliente = new Cliente();
                cliente.setCodCliente(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.CODIGO)));
                cliente.setFecha(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.FECHA)));
                cliente.setDescCliente(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DESC_CLIENTE)));
                cliente.setCodEmpresa(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.COD_EMPRESA)));
                cliente.setCodLista(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.COD_LISTA)));
                cliente.setCodLocal(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.COD_LOCAL)));
                cliente.setCodRuta(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.CODRUTA)));
                cliente.setDescRuta(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DESCRUTA)));
                cliente.setDireccion(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DIRECCION)));
                cliente.setCoordenadas(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.COORDENADAS)));
                cliente.setEstadoJornada(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.ESTADO_JORNADA)));
                cliente.setEstadoGeoSis(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.ESTADO_GEOSIS)));
                cliente.setStatus(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.ESTADO_CLIENTE)));
                cliente.setNewTelefono(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.NEW_TELEFONO)));
                cliente.setNombres(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.NOMBRES)));
                cliente.setApellidos(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.APELLIDOS)));
                cliente.setNewDireccion(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.NEW_DIRECCION)));
                cliente.setIsAuditado(c.getInt(c.getColumnIndex(ContratoPreventa.ColumnasCliente.IS_AUDITADO)));



                //OFFLINE
                cliente.setFechaCumple(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.CUMPLEAÑOS)));
                cliente.setTipoNegocio(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.TIPO_NEGOCIO)));
                cliente.setAntiguedad(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.ANTIGUEDAD)));
                cliente.setVelocidadPago(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasCliente.VELOCIDAD_PAGO)));
                cliente.setDeudaVencida(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DEUDA_VENCIDA)));
                cliente.setDeudaNoVencida(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DEUDA_NOVENCIDA)));
                cliente.setCantidadPedidos(c.getInt(c.getColumnIndex(ContratoPreventa.ColumnasCliente.CANT_PEDIDOS)));
                cliente.setDescListaPrecios(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.DESC_LISTA)));
                cliente.setLimiteCredito(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasCliente.LIMITE_CREDITO)));
                lista.add(cliente);
            }
            return lista;
        } catch(Exception ex){
            Log.wtf("ERRRO SQLITE CONSULTA CLIENTES",ex.getMessage());
            return null;
        }

    }

    public String insertarCliente(Cliente cliente) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasCliente.CODIGO, cliente.getCodCliente());
            valores.put(ContratoPreventa.ColumnasCliente.FECHA, cliente.getFecha());
            valores.put(ContratoPreventa.ColumnasCliente.DESC_CLIENTE,cliente.getDescCliente());
            valores.put(ContratoPreventa.ColumnasCliente.COORDENADAS, cliente.getCoordenadas());
            valores.put(ContratoPreventa.ColumnasCliente.CODRUTA, cliente.getCodRuta());
            valores.put(ContratoPreventa.ColumnasCliente.DESCRUTA, cliente.getDescRuta());
            valores.put(ContratoPreventa.ColumnasCliente.DIRECCION, cliente.getDireccion());
            valores.put(ContratoPreventa.ColumnasCliente.COD_LOCAL, cliente.getCodLocal());
            valores.put(ContratoPreventa.ColumnasCliente.COD_LISTA, cliente.getCodLista());
            valores.put(ContratoPreventa.ColumnasCliente.COD_EMPRESA, cliente.getCodEmpresa());
            valores.put(ContratoPreventa.ColumnasCliente.FLAG_DESPACHADO, cliente.getFlagDespachado());
            valores.put(ContratoPreventa.ColumnasCliente.FLAG_IGNORESEC, cliente.getFlagIgnoreSec());
            valores.put(ContratoPreventa.ColumnasCliente.ESTADO_JORNADA, cliente.getEstadoJornada());
            valores.put(ContratoPreventa.ColumnasCliente.ESTADO_GEOSIS, cliente.getEstadoGeoSis());
            valores.put(ContratoPreventa.ColumnasCliente.ESTADO_CLIENTE, cliente.getStatus());
            valores.put(ContratoPreventa.ColumnasCliente.NEW_TELEFONO, cliente.getNewTelefono());


            valores.put(ContratoPreventa.ColumnasCliente.TIPO_NEGOCIO, cliente.getTipoNegocio());
            valores.put(ContratoPreventa.ColumnasCliente.DESC_LISTA, cliente.getDescListaPrecios());
            valores.put(ContratoPreventa.ColumnasCliente.LIMITE_CREDITO, cliente.getLimiteCredito());
            valores.put(ContratoPreventa.ColumnasCliente.DEUDA_VENCIDA, cliente.getDeudaVencida());
            valores.put(ContratoPreventa.ColumnasCliente.DEUDA_NOVENCIDA, cliente.getDeudaNoVencida());
            valores.put(ContratoPreventa.ColumnasCliente.ANTIGUEDAD, cliente.getAntiguedad());
            valores.put(ContratoPreventa.ColumnasCliente.CUMPLEAÑOS, cliente.getFechaCumple());
            valores.put(ContratoPreventa.ColumnasCliente.VELOCIDAD_PAGO, cliente.getVelocidadPago());
            valores.put(ContratoPreventa.ColumnasCliente.CANT_PEDIDOS, cliente.getCantidadPedidos());

            valores.put(ContratoPreventa.ColumnasCliente.NOMBRES, cliente.getNombres());
            valores.put(ContratoPreventa.ColumnasCliente.APELLIDOS, cliente.getApellidos());
            valores.put(ContratoPreventa.ColumnasCliente.NEW_DIRECCION, cliente.getNewDireccion());
            valores.put(ContratoPreventa.ColumnasCliente.IS_AUDITADO, cliente.getIsAuditado());



            // Insertar cabecera
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.CLIENTE, null, valores);
            return cliente.getCodCliente();
        }catch(Exception ex){

            return ex.getMessage();
        }
    }

    public void updateClienteEstadoJornada(String newEstadoJornada,String codCliente,String codLocal){
        String updateQuery = "UPDATE %s SET %s=? where  %s=? AND  %s=? ";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CLIENTE,ContratoPreventa.ColumnasCliente.ESTADO_JORNADA,ContratoPreventa.ColumnasCliente.CODIGO,ContratoPreventa.ColumnasCliente.COD_LOCAL);
        String[] selectionArgs = {newEstadoJornada,codCliente,codLocal};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void updateAfterRegistrarCoordenadas(String newCoordenadas,String newEstadoGeosis,String codCliente,String codLocal){
        String updateQuery = "UPDATE %s SET %s=?,%s=? where  %s=? AND  %s=? ";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CLIENTE,ContratoPreventa.ColumnasCliente.COORDENADAS,ContratoPreventa.ColumnasCliente.ESTADO_GEOSIS,ContratoPreventa.ColumnasCliente.CODIGO,ContratoPreventa.ColumnasCliente.COD_LOCAL);
        String[] selectionArgs = {newCoordenadas,newEstadoGeosis,codCliente,codLocal};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }


    public void updateAfterRegistrarTelefono(String telefono,String codCliente){
        String updateQuery = "UPDATE %s SET %s=? where  %s=?";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CLIENTE,ContratoPreventa.ColumnasCliente.NEW_TELEFONO,ContratoPreventa.ColumnasCliente.CODIGO);
        String[] selectionArgs = {telefono,codCliente};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }


    public void updateAuditarCliente(String codCliente,String codLocal){
        String updateQuery = "UPDATE %s SET %s=1 where  %s=? and  %s=?";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CLIENTE,ContratoPreventa.ColumnasCliente.IS_AUDITADO,ContratoPreventa.ColumnasCliente.CODIGO,ContratoPreventa.ColumnasCliente.COD_LOCAL);
        String[] selectionArgs = {codCliente,codLocal};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteTablaClientes(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.CLIENTE);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }

    //BLOQUE METRICAS
    public String insertarMontos(Double monto) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasMontosVendedor.MONTO, monto);
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.MONTOS_VENDEDOR, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }
    public String insertarCabeceraMontos(String cabecera) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasCabeceraMontosVendedor.CABECERA, cabecera);
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.CABECERA_MONTOS_VENDEDOR, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }
    public Cursor obtenerCabeceraMontos() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT distinct * FROM %s ", BaseDatosTomaPedidos.Tablas.CABECERA_MONTOS_VENDEDOR);
        return db.rawQuery(sql, null);

    }
    public List<String> listarCabeceraMontos(){
        Cursor c=obtenerCabeceraMontos();
        List<String> lista=new ArrayList<>();
        while(c.moveToNext()){
            lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraMontosVendedor.CABECERA)));
        }
        return lista;
    }
    public Cursor obtenerMetricasByFecha(String fecha) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String selectQuery = "SELECT distinct periodo_metrica FROM   "+BaseDatosTomaPedidos.Tablas.DATOS_METRICAS;
       // selectQuery=selectQuery+" where strftime('%Y-%m-%d' ,"+ContratoPreventa.ColumnasDatosMetricas.FECHA+")";
        selectQuery=selectQuery+" where "+ContratoPreventa.ColumnasDatosMetricas.FECHA;//
        selectQuery = selectQuery + " = '" + fecha + "'" ;
        return db.rawQuery(selectQuery,null);

    }
    public List<String> listarMetricasByFecha(String fecha){
        Cursor c=obtenerMetricasByFecha(fecha);
        List<String> lista=new ArrayList<>();
        while(c.moveToNext()){
            lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.PERIODO)));
        }
        return lista;
    }
    public Cursor obtenerValoresMontos() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT  * FROM %s ", BaseDatosTomaPedidos.Tablas.MONTOS_VENDEDOR);
        return db.rawQuery(sql, null);

    }
    public List<Double> listarValoresMontos(){
        Cursor c=obtenerValoresMontos();
        List<Double> lista=new ArrayList<>();
        while(c.moveToNext()){
            lista.add(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasMontosVendedor.MONTO)));
        }
        return lista;
    }
    public String insertarDatosMetricas(String vendedor,String fecha,String periodo,Double ticketDiario,Double ticketMensual,String rutasHoy,Integer permiteOffline) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasDatosMetricas.VENDEDOR, vendedor);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.FECHA, fecha);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.PERIODO, periodo);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.TICKET_DIARIO, ticketDiario);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.TICKET_MENSUAL, ticketMensual);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.RUTASHOY, rutasHoy);
            valores.put(ContratoPreventa.ColumnasDatosMetricas.PERMITE_OFFLINE, permiteOffline);
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.DATOS_METRICAS, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }
    public Cursor obtenerDatosMetricas() {
        try{
            SQLiteDatabase db = baseDatos.getReadableDatabase();
            //String nuevofiltro= limpiarAcentos();
            String sql = String.format("SELECT distinct * FROM %s ", BaseDatosTomaPedidos.Tablas.DATOS_METRICAS);
            return db.rawQuery(sql, null);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }




    }
    public List<String> listarDatosMetricas(){
        Cursor c=obtenerDatosMetricas();
        List<String> lista=new ArrayList<>();
        try{
            while(c.moveToNext()){
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.VENDEDOR)));
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.FECHA)));
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.PERIODO)));
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.TICKET_DIARIO)));
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.TICKET_MENSUAL)));
                lista.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.RUTASHOY)));
            }
            return lista;
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }


    }
    public void deleteTablaMontosVendedor(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.MONTOS_VENDEDOR);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }
    public void deleteTablaCabeceraMontosVendedor(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.CABECERA_MONTOS_VENDEDOR);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }
    public void deleteTablaDatosMetricas(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.DATOS_METRICAS);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }
    public Cursor obtenerFechaCliente() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT distinct FECHA FROM %s ", BaseDatosTomaPedidos.Tablas.CLIENTE);
        return db.rawQuery(sql, null);

    }
    public Cursor obtenerFechaMetricas() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT distinct fecha_metrica FROM %s ", BaseDatosTomaPedidos.Tablas.DATOS_METRICAS);
        return db.rawQuery(sql, null);

    }
    public Cursor obtenerPermiteCredito() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT DISTINCT permite_credito  FROM %s ", BaseDatosTomaPedidos.Tablas.DATOS_USUARIO);
        return db.rawQuery(sql, null);

    }
    public String getPermiteCredito(){
        Cursor c=obtenerPermiteCredito();
        String  permiteCredito=new String();
        while(c.moveToNext()){
            permiteCredito = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.PERMITE_CREDITO));
        }
        return permiteCredito;

    }
    public String getFechaClientes(){

        Cursor c=obtenerFechaCliente();
        String  fecha=new String();
        while(c.moveToNext()){
            fecha = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCliente.FECHA));
        }
        return fecha;

    }

    public Cursor cursorPermiteOffline() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT DISTINCT %s  FROM %s ",ContratoPreventa.ColumnasDatosMetricas.PERMITE_OFFLINE, BaseDatosTomaPedidos.Tablas.DATOS_METRICAS);
        return db.rawQuery(sql, null);

    }
    public Integer getPermiteOffline(){
        Cursor c=cursorPermiteOffline();
        Integer  permiteOffline=new Integer("0");
        while(c.moveToNext()){
            permiteOffline = c.getInt(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.PERMITE_OFFLINE));
        }
        return permiteOffline;

    }

    public String getFechaMetricas(){
        Cursor c=obtenerFechaMetricas();
        String  fecha=new String();
        while(c.moveToNext()){
            fecha = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosMetricas.FECHA));
        }
        return fecha;
    }


    public long getCountPedidosPendientes(String fecha,String usuario){
       // try {
            SQLiteDatabase db = baseDatos.getReadableDatabase();
            return DatabaseUtils.queryNumEntries(db, "cabecera_pedido_local",
                    "status_pedido=? and strftime('%d-%m-%Y',fecha_registro)=?  and usuario_xray=?", new String[]{"P", fecha, usuario});
//        }catch(Exception ex){
//            Toast.makeText(context,ex.getMessage() , Toast.LENGTH_LONG).show();
//            return  0;
//        }

    }

    public long getCountNoPedidosPendientes(String fecha,String usuario){
        SQLiteDatabase db =  baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, "no_pedido_local",
                "status_nopedido=? and strftime('%d-%m-%Y',fecha_registro)=?  and usuario_xray=?", new String[] {"P",fecha,usuario});

    }


    public long getCountClienteNuevo(String codClienteNew,String  codLocal){
        SQLiteDatabase db =  baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, "cliente",
                "cod_cliente=?  and cod_local=?", new String[] {codClienteNew,codLocal});

    }


    public  String limpiarAcentos(String cadena) {
        String limpio =null;
        if (cadena !=null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        }
        return limpio;
    }

//OFFLINE

    public String insertarDatosUsuario(ArrayList datos) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_EMPRESA, datos.get(0).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_MESA, datos.get(1).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.DESC_MESA,datos.get(2).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.TIPO_CODIGO, datos.get(3).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_LOCALIDAD, datos.get(4).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.DESC_LOCALIDAD, datos.get(5).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_CANAL, datos.get(6).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.DESC_CANAL, datos.get(7).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_VENDEDOR, datos.get(8).toString());

            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_ALMACEN, datos.get(9).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.DESC_ALMACEN, datos.get(10).toString());

            valores.put(ContratoPreventa.ColumnasDatosUsuario.COD_SEDE,datos.get(11).toString());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.PERMITE_CREDITO, new Double(datos.get(12).toString()).intValue());
            valores.put(ContratoPreventa.ColumnasDatosUsuario.PERMITE_CAMPANIA, new Double(datos.get(13).toString()).intValue());


            // Insertar cabecera
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.DATOS_USUARIO, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }


    public String insertarCondicionesPago(ArrayList condiciones) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasCondiciones.COD_CONDICION, condiciones.get(0).toString());
            valores.put(ContratoPreventa.ColumnasCondiciones.DESC_CONDICION,  condiciones.get(1).toString());
            valores.put(ContratoPreventa.ColumnasCondiciones.TIPO_CONDICION,new Double(condiciones.get(2).toString()).intValue());
            valores.put(ContratoPreventa.ColumnasCondiciones.COD_LISTA,condiciones.get(3).toString());
            // Insertar cabecera
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.CONDICIONES_PAGO, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }

    public String insertarArticulos(Articulo articulo) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasArticulos.COD_ITEM, articulo.getCodItem());
            valores.put(ContratoPreventa.ColumnasArticulos.DESC_ITEM, articulo.getDescItem());
            valores.put(ContratoPreventa.ColumnasArticulos.UM_ITEM,articulo.getUm());
            valores.put(ContratoPreventa.ColumnasArticulos.COD_LISTA,articulo.getCodLista());
            valores.put(ContratoPreventa.ColumnasArticulos.PRECIO_BASE,articulo.getPrecioBase().doubleValue());
            valores.put(ContratoPreventa.ColumnasArticulos.SALDO,articulo.getSaldo().doubleValue());
            valores.put(ContratoPreventa.ColumnasArticulos.COD_ALMACEN,articulo.getCodAlmacen());
            valores.put(ContratoPreventa.ColumnasArticulos.COND_PAGO,articulo.getCondicionPago());
            valores.put(ContratoPreventa.ColumnasArticulos.PRECIO_SUGERIDO,articulo.getPrecioSugerido().doubleValue());
            // Insertar cabecera
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.ARTICULO, null, valores);
            return "OK";
        }catch(Exception ex){

            return ex.getMessage();
        }
    }

    public Cursor obtenerLocalidades() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("select distinct cod_empresa,cod_mesa,desc_mesa,tipo_codigo,desc_localidad,cod_canal,desc_canal,cod_vendedor,cod_localidad,cod_sede,\n" +
                "case when tipo_codigo=\"R\" THEN \"REGULAR\" WHEN tipo_codigo=\"A\" THEN   \"ARROZ\" ELSE \"CAMPAÑA\" END DESCTIPO\n" +
                "from %s ;\n", BaseDatosTomaPedidos.Tablas.DATOS_USUARIO);
        return db.rawQuery(sql, null);
    }

    public  ArrayList  listarLocalidades(){
        Cursor c=obtenerLocalidades();
        ArrayList  lista=new ArrayList<>();
        while(c.moveToNext()){
            ArrayList  obj=new ArrayList();
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_EMPRESA)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_MESA)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.DESC_MESA)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.TIPO_CODIGO)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.DESC_LOCALIDAD)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_CANAL)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.DESC_CANAL)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_VENDEDOR)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_LOCALIDAD)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_SEDE)));
            lista.add(obj);

        }
        return lista;
    }

    public Cursor obtenerAlmacenesByLocalidad(String codLocalidad) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("select  cod_almacen,desc_almacen from %s WHERE %s=?", BaseDatosTomaPedidos.Tablas.DATOS_USUARIO,ContratoPreventa.ColumnasDatosUsuario.COD_LOCALIDAD);
        String[] selectionArgs = {codLocalidad};
        return db.rawQuery(sql, selectionArgs);
    }

    public  ArrayList  listarAlmacenesByLocalidad(String codLocalidad){
        Cursor c=obtenerAlmacenesByLocalidad(codLocalidad);
        ArrayList  lista=new ArrayList<>();
        while(c.moveToNext()){
            ArrayList  obj=new ArrayList();
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.COD_ALMACEN)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDatosUsuario.DESC_ALMACEN)));
            lista.add(obj);

        }
        return lista;
    }


    public Cursor obtenerCondicionesDePago(String codLista,Integer tieneDeudaVencida) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String permiteCredito=getPermiteCredito();
        if(tieneDeudaVencida.equals(new Integer(1))){
                permiteCredito="0";
        }
        String sql="";
        if(permiteCredito.equals("1")){
            sql = String.format("select cod_condicion,desc_condicion  from %s where  %s=?", BaseDatosTomaPedidos.Tablas.CONDICIONES_PAGO,ContratoPreventa.ColumnasCondiciones.COD_LISTA);
        }else{
            sql = String.format("select cod_condicion,desc_condicion  from %s where  %s=? AND %s IN ('2','3')", BaseDatosTomaPedidos.Tablas.CONDICIONES_PAGO,ContratoPreventa.ColumnasCondiciones.COD_LISTA,ContratoPreventa.ColumnasCondiciones.TIPO_CONDICION);

        }
        String[] selectionArgs = {codLista};
        return db.rawQuery(sql, selectionArgs);
    }

    public  ArrayList  listarCondicionesPago(String codLista,Integer deuda){
        Cursor c=obtenerCondicionesDePago(codLista,deuda);
        ArrayList  lista=new ArrayList<>();
        while(c.moveToNext()){
            ArrayList  obj=new ArrayList();
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCondiciones.COD_CONDICION)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCondiciones.DESC_CONDICION)));

            lista.add(obj);

        }
        return lista;
    }


    public Cursor obtenerListaArticulos(String filtro,String codLista,String codAlmacen,String condicionPago) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String nuevofiltro= limpiarAcentos(filtro);
        String sql = String.format("SELECT * FROM %s WHERE %s||%s like ? AND %s=? and %s=? and %s=? limit 20", BaseDatosTomaPedidos.Tablas.ARTICULO, ContratoPreventa.ColumnasArticulos.COD_ITEM,ContratoPreventa.ColumnasArticulos.DESC_ITEM,
                ContratoPreventa.ColumnasArticulos.COD_LISTA,ContratoPreventa.ColumnasArticulos.COD_ALMACEN, ContratoPreventa.ColumnasArticulos.COND_PAGO);
        String[] selectionArgs = {"%"+nuevofiltro + "%",codLista,codAlmacen,condicionPago};
        return db.rawQuery(sql, selectionArgs);
    }
    public List<Articulo> listaArticulo(String filtro,String codLista,String codAlmacen,String condicionPago){
        Cursor c=obtenerListaArticulos(filtro,codLista,codAlmacen,condicionPago);
        List<Articulo> lista=new ArrayList<>();
        while(c.moveToNext()){
            String codigo = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COD_ITEM));

            String descripcion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.DESC_ITEM));
            String um = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.UM_ITEM));
            String listaPrecio=  c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COD_LISTA));
            Double precioBase=  c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_BASE));
            String saldo=  c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.SALDO));
            String almacen=  c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COD_ALMACEN));
            String condPago=  c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COND_PAGO));
            Double precioSugerido=  c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_SUGERIDO));
            Articulo articulo=new Articulo(codigo,descripcion,um,listaPrecio,new BigDecimal(precioBase,new MathContext(15, RoundingMode.FLOOR)),new BigDecimal(saldo),almacen);
            articulo.setCondicionPago(condPago);
            articulo.setPrecioSugerido(new BigDecimal(precioSugerido, new MathContext(15, RoundingMode.FLOOR)));
            lista.add(articulo);
        }
        return lista;
    }


    public String insertarPedidoLocal(PreVenta preventa) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            //String idCabeceraPedidoLocal = ContratoPreventa.CabeceraPedidoMovil.generarIdCabeceraPedidoMovil();
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.ID, preventa.getUid());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOEMPRESA, preventa.getCodEmpresa());
//            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCEMPRESA,  preventa.getDescEmpresa());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLOCALIDAD, preventa.getCodLocalidad());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCLOCALIDAD, preventa.getDescLocalidad());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODALMACEN, preventa.getCodAlmacen());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCALMACEN,preventa.getDescAlmacen());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY, preventa.getUsuarioRegistro());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOCLIENTE, preventa.getCodCliente());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCCLIENTE, preventa.getDescCliente());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCONDICIONPAGO, preventa.getCodCondicion());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLISTAPRECIOS,preventa.getCodLista());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODDIRECCION, preventa.getCodLocal());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.MONTOVENTA, preventa.getMontoVenta().doubleValue());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) );
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL, STATUSSINCRONIZACION.PENDIENTE.getCod());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODSEDE,preventa.getCodSede() );
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODMESA, preventa.getCodMesa());
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCANAL, preventa.getCodCanal() );
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.COORDENADAS, preventa.getCoordenadas() );

            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODRUTA, preventa.getCodRuta() );
            valores.put(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODVENDEDOR, preventa.getCodVendedor() );
//            for (int i = 0; i < preventa.getDetalles().size(); i++) {
//                insertarDetallePedidoLocal( preventa.getDetalles().get(i),preventa.getUid());
//            }
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL, null, valores);
            return preventa.getCodEmpresa();
        }catch(SQLException ex){
            return ex.getMessage();
        }
    }


    public String insertarDetallePedidoLocal(CarritoCompras detallePedido, String codigoCabeceraPedido) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO, codigoCabeceraPedido);
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOARTICULO, detallePedido.getArticulo().getCodItem());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.DESCITEM,  detallePedido.getArticulo().getDescItem());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.UM, detallePedido.getArticulo().getUm());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOLISTA,  detallePedido.getArticulo().getCodLista());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOBASE, detallePedido.getArticulo().getPrecioBase().doubleValue());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOSUGERIDO, detallePedido.getArticulo().getPrecioSugerido().doubleValue());
            valores.put(ContratoPreventa.ColumnasDetallesPedidoLocal.CANTIDAD, detallePedido.getCantidad());
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.DETALLE_PEDIDO_LOCAL, null, valores);

            return detallePedido.getArticulo().getCodItem();
        }catch(SQLException ex){

            return ex.getMessage();
        }
    }

    public String insertarNoPedidoLocal(NoPedido noPedidoLocal) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            String idPedidoNoLocal = ContratoPreventa.IdNoPedidoMovil.generarIdNoPedidoMovil();
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.ID, idPedidoNoLocal);
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODIGOEMPRESA, noPedidoLocal.getCodEmpresa());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODDIRECCION, noPedidoLocal.getCodLocal());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODCLIENTE,noPedidoLocal.getCodCliente());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCCLIENTE, noPedidoLocal.getDescCliente());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCDIRDESPACHO, noPedidoLocal.getDireccion());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.USUARIOXRAY, noPedidoLocal.getUsuario());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODRUTA, noPedidoLocal.getCodRuta());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.MOTIVONOPEDIDO, noPedidoLocal.getDescMotivo());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL, noPedidoLocal.getStatusNoPedido());
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.FECHAREGISTRO, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) );
            valores.put(ContratoPreventa.ColumnasTablaNoPedidoLocal.COORDENADAS, noPedidoLocal.getCoordenadasNoPedido());
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.NO_PEDIDO_LOCAL, null, valores);
            return noPedidoLocal.getCodCliente();
        }catch(SQLException ex){

            return ex.getMessage();
        }
    }


    public String insertarSugeridoLocal(ArrayList sugerido) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
             ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasSugeridoLocal.CODCLIENTE, sugerido.get(0).toString());
            valores.put(ContratoPreventa.ColumnasSugeridoLocal.COD_ITEM,  sugerido.get(1).toString());
            valores.put(ContratoPreventa.ColumnasSugeridoLocal.CANT_SUGERIDA,  new Double(sugerido.get(2).toString()).intValue());//sugerido.get(2).toString()
            valores.put(ContratoPreventa.ColumnasSugeridoLocal.NRO_VECES, new Double(sugerido.get(4).toString()).intValue());//sugerido.get(4).toString()
           db.insertOrThrow(BaseDatosTomaPedidos.Tablas.SUGERIDO_LOCAL, null, valores);
            return "OK";
        }catch(SQLException ex){

            return ex.getMessage();
        }
    }


    public String insertarFocusLocal(ArrayList focus) {
        try {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(ContratoPreventa.ColumnasFocusLocal.COD_ITEM, focus.get(0).toString());
            valores.put(ContratoPreventa.ColumnasFocusLocal.CANT_FOCUS,  new Double(focus.get(1).toString()).intValue());//focus.get(2).toString()
            db.insertOrThrow(BaseDatosTomaPedidos.Tablas.FOCUS_LOCAL, null, valores);
            return "OK";
        }catch(SQLException ex){

            return ex.getMessage();
        }
    }



    public Cursor listarCursorPedidosLocales(String fecha,String statusFiltro,String usuario){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String selectQuery = "SELECT distinct * FROM   "+BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL;
        selectQuery=selectQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO+")";
        if(statusFiltro==null) {
            selectQuery = selectQuery + " = '" + fecha + "'  and " + ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"','"+ STATUSSINCRONIZACION.SINCRONIZADO.getCod()+"')";
        }else if(statusFiltro.equals(STATUSSINCRONIZACION.PENDIENTE.getCod())){
            selectQuery = selectQuery + " = '" + fecha + "'  and " + ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"')";
        }
        selectQuery=selectQuery+" and "+ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY+"='"+usuario+"'";
        return db.rawQuery(selectQuery,null);
    }

    public Cursor listarCursorPedidosLocalesAnteriores(String fecha,String statusFiltro,String usuario){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String selectQuery = "SELECT distinct * FROM   "+BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL;
        selectQuery=selectQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO+")";
        if(statusFiltro==null) {
            selectQuery = selectQuery + " < '" + fecha + "'  and " + ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"','"+ STATUSSINCRONIZACION.SINCRONIZADO.getCod()+"')";
        }else if(statusFiltro.equals(STATUSSINCRONIZACION.PENDIENTE.getCod())){
            selectQuery = selectQuery + " < '" + fecha + "'  and " + ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"')";
        }
        selectQuery=selectQuery+" and "+ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY+"='"+usuario+"'";
        return db.rawQuery(selectQuery,null);
    }

    public Cursor listarCursorDetallesPedidosLocales(String idCabecera){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT distinct * FROM %s where %s=?", BaseDatosTomaPedidos.Tablas.DETALLE_PEDIDO_LOCAL,ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO);
        String[] selectionArgs = {idCabecera};
        return db.rawQuery(sql, selectionArgs);
    }
    public List<CarritoCompras> listaDetallesPedidosLocales(String idCabecera){
        Cursor c=listarCursorDetallesPedidosLocales(idCabecera);
        List<CarritoCompras> lista=new ArrayList<>();
        while(c.moveToNext()){
            String nroPedido=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO));
            String codProducto= c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOARTICULO));
            String descProducto = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.DESCITEM));
            Double precioString = c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOBASE));
            Double precioSugeridoString = c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.PRECIOSUGERIDO));

            String umString = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.UM));
            String codListaPrecio = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.CODIGOLISTA));

            String cantidadString = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasDetallesPedidoLocal.CANTIDAD));
            CarritoCompras  detallePedido=new CarritoCompras();

            Articulo articulo=new Articulo();
            articulo.setCodItem(codProducto);
            articulo.setDescItem(descProducto);
            articulo.setPrecioBase(new BigDecimal(precioString,new MathContext(15, RoundingMode.FLOOR)));
            articulo.setPrecioSugerido(new BigDecimal(precioSugeridoString,new MathContext(15, RoundingMode.FLOOR)));
            articulo.setCodLista(codListaPrecio);
            articulo.setUm(umString);
            detallePedido.setArticulo(articulo);
            detallePedido.setCantidad(new Integer(cantidadString));
            lista.add(detallePedido);

        }
        return lista;
    }

    public List<PreVenta> listarPedidosLocales(String filtro,String statusFiltro,String usuario){
        Cursor c=listarCursorPedidosLocales(filtro,statusFiltro,usuario);
        List<PreVenta> lista=new ArrayList<>();
        while(c.moveToNext()){
            String nroPedido=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.ID));
            String codCliente= c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOCLIENTE));
            String descCliente = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCCLIENTE));
            String montoString = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.MONTOVENTA));
            String codCondicion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCONDICIONPAGO));
            String codDireccion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODDIRECCION));
            String codAlmacen = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODALMACEN));
            String codLocalidad = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLOCALIDAD));
            String codEmpresa = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOEMPRESA));
            String codListaPrecios = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLISTAPRECIOS));
            String descLocalidad = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCLOCALIDAD));
            String descAlmacen = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCALMACEN));
            String usuarioXray = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY));
            String fechaRegistro=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO));
            String status=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL));
            String codSede=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODSEDE));
            String codMesa=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODMESA));
            String codCanal=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCANAL));
            String codRuta=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODRUTA));
            String codVendedor=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODVENDEDOR));


            PreVenta preVenta=new PreVenta();
            preVenta.setUid(nroPedido);
            preVenta.setCodCliente(codCliente);
            preVenta.setDescCliente(descCliente);
            preVenta.setMontoVenta(new BigDecimal(montoString));
            preVenta.setCodCondicion(codCondicion);
            preVenta.setCodLocal(codDireccion);
            preVenta.setCodAlmacen(codAlmacen);
            preVenta.setCodLocalidad(codLocalidad);
            preVenta.setCodEmpresa(codEmpresa);
            preVenta.setCodLista(codListaPrecios);
            preVenta.setDescLocalidad(descLocalidad);
            preVenta.setFechaRegistro(fechaRegistro);
            preVenta.setDescAlmacen(descAlmacen);
            preVenta.setUsuarioRegistro(usuarioXray);
            preVenta.setCodSede(codSede);
            preVenta.setCodMesa(codMesa);
            preVenta.setCodCanal(codCanal);
            preVenta.setStatus(status);
            preVenta.setCodRuta(codRuta);
            preVenta.setCodVendedor(codVendedor);
            preVenta.setDetalles(listaDetallesPedidosLocales(preVenta.getUid()));
//
//
//
            lista.add(preVenta);
        }
        return lista;
    }
    public List<PreVenta> listarPedidosLocalesAnteriores(String filtro,String statusFiltro,String usuario){
        Cursor c=listarCursorPedidosLocalesAnteriores(filtro,statusFiltro,usuario);
        List<PreVenta> lista=new ArrayList<>();
        while(c.moveToNext()){
            String nroPedido=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.ID));
            String codCliente= c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOCLIENTE));
            String descCliente = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCCLIENTE));
            String montoString = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.MONTOVENTA));
            String codCondicion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCONDICIONPAGO));
            String codDireccion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODDIRECCION));
            String codAlmacen = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODALMACEN));
            String codLocalidad = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLOCALIDAD));
            String codEmpresa = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODIGOEMPRESA));
            String codListaPrecios = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODLISTAPRECIOS));
            String descLocalidad = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCLOCALIDAD));
            String descAlmacen = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.DESCALMACEN));
            String usuarioXray = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.USUARIOXRAY));
            String fechaRegistro=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO));
            String status=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL));
            String codSede=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODSEDE));
            String codMesa=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODMESA));
            String codCanal=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODCANAL));
            String codRuta=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODRUTA));
            String codVendedor=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasCabeceraPedidoLocal.CODVENDEDOR));


            PreVenta preVenta=new PreVenta();
            preVenta.setUid(nroPedido);
            preVenta.setCodCliente(codCliente);
            preVenta.setDescCliente(descCliente);
            preVenta.setMontoVenta(new BigDecimal(montoString));
            preVenta.setCodCondicion(codCondicion);
            preVenta.setCodLocal(codDireccion);
            preVenta.setCodAlmacen(codAlmacen);
            preVenta.setCodLocalidad(codLocalidad);
            preVenta.setCodEmpresa(codEmpresa);
            preVenta.setCodLista(codListaPrecios);
            preVenta.setDescLocalidad(descLocalidad);
            preVenta.setFechaRegistro(fechaRegistro);
            preVenta.setDescAlmacen(descAlmacen);
            preVenta.setUsuarioRegistro(usuarioXray);
            preVenta.setCodSede(codSede);
            preVenta.setCodMesa(codMesa);
            preVenta.setCodCanal(codCanal);
            preVenta.setStatus(status);
            preVenta.setCodRuta(codRuta);
            preVenta.setCodVendedor(codVendedor);
            preVenta.setDetalles(listaDetallesPedidosLocales(preVenta.getUid()));
//
//
//
            lista.add(preVenta);
        }
        return lista;
    }

    public Cursor cursorNoPedidosLocales(String fecha,String statusFiltro,String usuario){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String selectQuery = "SELECT distinct * FROM   "+BaseDatosTomaPedidos.Tablas.NO_PEDIDO_LOCAL;
        selectQuery=selectQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasTablaNoPedidoLocal.FECHAREGISTRO+")";
        if(statusFiltro==null) {
            selectQuery = selectQuery + " = '" + fecha + "'  and " + ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"','"+ STATUSSINCRONIZACION.SINCRONIZADO.getCod()+"')";
        }else if(statusFiltro.equals(STATUSSINCRONIZACION.PENDIENTE.getCod())){
            selectQuery = selectQuery + " = '" + fecha + "'  and " + ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL + " IN ('"+ STATUSSINCRONIZACION.PENDIENTE.getCod()+"')";
        }
        selectQuery=selectQuery+" and "+ContratoPreventa.ColumnasTablaNoPedidoLocal.USUARIOXRAY+"='"+usuario+"'";
        return db.rawQuery(selectQuery,null);
    }


    public List<NoPedido> listarNoPedidosLocales(String fecha,String statusFiltro,String usuario){
        Cursor c=cursorNoPedidosLocales(fecha,statusFiltro,usuario);
        List<NoPedido> lista=new ArrayList<>();
        while(c.moveToNext()){
            String nroPedido=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.ID));
            String codCliente= c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODCLIENTE));
            String descCliente = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCCLIENTE));
            String codDireccion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODDIRECCION));
            String descDireccion = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.DESCDIRDESPACHO));
            String codEmpresa = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODIGOEMPRESA));
            String usuarioXray = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.USUARIOXRAY));
            String fechaRegistro=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.FECHAREGISTRO));
            String status=c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL));
            String codRuta = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.CODRUTA));
            String descMotivo = c.getString(c.getColumnIndex(ContratoPreventa.ColumnasTablaNoPedidoLocal.MOTIVONOPEDIDO));

            NoPedido noPedido=new NoPedido();
            noPedido.setIdNoPedidoLocal(nroPedido);
            noPedido.setCodCliente(codCliente);
            noPedido.setStatusNoPedido(status);
            noPedido.setDescMotivo(descMotivo);
            noPedido.setCodEmpresa(codEmpresa);
            noPedido.setCodLocal(codDireccion);
            noPedido.setUsuario(usuarioXray);
            noPedido.setDireccion(descDireccion);
            noPedido.setFechaTransaccion(fechaRegistro);
            noPedido.setCodRuta(codRuta);
            noPedido.setDescCliente(descCliente);
            lista.add(noPedido);

        }
        return lista;

    }

    public Cursor obtenerSugeridosByCliente(String codCliente) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("select a.cod_item,a.desc_item,a.cant_sugerida from \n" +
                "(select  distinct sl.cod_item,art.desc_item, sl.cant_sugerida,sl.nro_veces\n" +
                "FROM %s SL \n" +
                " INNER JOIN %s ART ON SL.cod_item=ART.cod_item\n" +
                "where SL.cod_cliente=? " +
                "order by 4 desc\n" +
                ")a limit 3\n", BaseDatosTomaPedidos.Tablas.SUGERIDO_LOCAL,BaseDatosTomaPedidos.Tablas.ARTICULO);
        String[] selectionArgs = {codCliente};
        return db.rawQuery(sql, selectionArgs);
    }


    public Cursor obtenerFocus() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format(" select  distinct fl.cod_item,art.desc_item, fl.cant_sugerida \n" +
                " FROM %s FL INNER JOIN %s ART ON fL.cod_item=ART.cod_item ", BaseDatosTomaPedidos.Tablas.FOCUS_LOCAL,BaseDatosTomaPedidos.Tablas.ARTICULO);
         return db.rawQuery(sql, null);
    }

    public Cursor getSugeridos(String codCliente,String codItems,String codLista,String condPago){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("select art.cod_item,art.desc_item,art.um_item,art.precio_base,art.saldo,art.precio_sugerido\n" +
                "        from %s art\n" +
                "        INNER JOIN %s sl on art.cod_item=sl.cod_item and sl.cod_cliente=?" +
                "        where art.cod_item in " +"('"+ codItems.replace(",","','")+"')"+
                "        and art.cod_lista=? and art.cond_pago=?", BaseDatosTomaPedidos.Tablas.ARTICULO,BaseDatosTomaPedidos.Tablas.SUGERIDO_LOCAL);
        String[] selectionArgs = {codCliente,codLista,condPago};
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor getFocus(String codItems,String codLista,String condPago){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("select art.cod_item,art.desc_item,art.um_item,art.precio_base,art.saldo,art.precio_sugerido\n" +
                "        from %s art\n" +
                "        INNER JOIN %s fl on art.cod_item=fl.cod_item " +
                "        where art.cod_item in " +"('"+ codItems.replace(",","','")+"')"+
                "        and art.cod_lista=? and art.cond_pago=?", BaseDatosTomaPedidos.Tablas.ARTICULO,BaseDatosTomaPedidos.Tablas.FOCUS_LOCAL);
        String[] selectionArgs = {codLista,condPago};
        return db.rawQuery(sql, selectionArgs);


    }


    public   List<Articulo>  getSugeridosArticulos(String codCliente,String codItems,String codLista,String condPago){
        Cursor c=getSugeridos(codCliente,codItems,codLista,condPago);
        List<Articulo> lista=new ArrayList<Articulo>();
        while(c.moveToNext()){
            Articulo art=new Articulo();
            art.setCodItem(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COD_ITEM)));
            art.setDescItem(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.DESC_ITEM)));
            art.setUm(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.UM_ITEM)));
            art.setPrecioBase(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_BASE)),new MathContext(15, RoundingMode.FLOOR)));
            art.setPrecioSugerido(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_SUGERIDO)),new MathContext(15, RoundingMode.FLOOR)));
            art.setSaldo(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.SALDO)),new MathContext(15, RoundingMode.FLOOR)));
            art.setTipo("S");
            lista.add(art);

        }
        return lista;
    }

    public   List<Articulo>  getFocusArticulos(String codItems,String codLista,String condPago){
        Cursor c=getFocus(codItems,codLista,condPago);
        List<Articulo> lista=new ArrayList<Articulo>();
        while(c.moveToNext()){
            Articulo art=new Articulo();
            art.setCodItem(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.COD_ITEM)));
            art.setDescItem(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.DESC_ITEM)));
            art.setUm(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.UM_ITEM)));
            art.setPrecioBase(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_BASE)),new MathContext(15, RoundingMode.FLOOR)));
            art.setPrecioSugerido(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.PRECIO_SUGERIDO)),new MathContext(15, RoundingMode.FLOOR)));
            art.setSaldo(new BigDecimal(c.getDouble(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.SALDO)),new MathContext(15, RoundingMode.FLOOR)));
           art.setTipo("F");
            lista.add(art);

        }
        return lista;
    }




    public ArrayList listarSugeridosByCliente(String codCliente){
        Cursor c=obtenerSugeridosByCliente(codCliente);
       ArrayList lista=new ArrayList<>();
        while(c.moveToNext()){
            ArrayList obj=new ArrayList();
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasSugeridoLocal.COD_ITEM)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.DESC_ITEM)));
            obj.add(c.getInt(c.getColumnIndex(ContratoPreventa.ColumnasSugeridoLocal.CANT_SUGERIDA)));
            obj.add("S");
            lista.add(obj);

        }
        return lista;

    }

    public ArrayList listarFocus(){
        Cursor c=obtenerFocus();
        ArrayList lista=new ArrayList<>();
        while(c.moveToNext()){
            ArrayList obj=new ArrayList();
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasFocusLocal.COD_ITEM)));
            obj.add(c.getString(c.getColumnIndex(ContratoPreventa.ColumnasArticulos.DESC_ITEM)));
            obj.add(c.getInt(c.getColumnIndex(ContratoPreventa.ColumnasFocusLocal.CANT_FOCUS)));
            obj.add("F");
            lista.add(obj);

        }
        return lista;

    }



    public long contarArticulos(){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, BaseDatosTomaPedidos.Tablas.ARTICULO);
    }

    public long contarDatosUsuario(){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, BaseDatosTomaPedidos.Tablas.DATOS_USUARIO);
    }
    public long contarDatosCliente(){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, BaseDatosTomaPedidos.Tablas.DATOS_CLIENTE);
    }
    public long contarCondicionesPago(){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, BaseDatosTomaPedidos.Tablas.CONDICIONES_PAGO);
    }

    public void updateStatusPedidoLocal(String newEstadoPedido,String nroPedidoLocal){
        String updateQuery = "UPDATE %s SET %s=? where  %s=?";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL,ContratoPreventa.ColumnasCabeceraPedidoLocal.STATUSPEDIDOLOCAL,ContratoPreventa.ColumnasCabeceraPedidoLocal.ID);
        String[] selectionArgs = {newEstadoPedido,nroPedidoLocal};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }
    public void updateStatusNoPedidoLocal(String newEstadoPedido,String nroPedidoLocal){
        String updateQuery = "UPDATE %s SET %s=? where  %s=?";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.NO_PEDIDO_LOCAL,ContratoPreventa.ColumnasTablaNoPedidoLocal.STATUSNOPEDIDOLOCAL,ContratoPreventa.ColumnasTablaNoPedidoLocal.ID);
        String[] selectionArgs = {newEstadoPedido,nroPedidoLocal};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void updateClienteOldClienteNew(Cliente clienteNew){
        String updateQuery = "UPDATE %s SET %s=? , %s=? , %s=? ,%s=? ,%s=?  ,%s=? where  %s=? and %s=?";
        String sql = String.format(updateQuery, BaseDatosTomaPedidos.Tablas.CLIENTE,ContratoPreventa.ColumnasCliente.COD_LISTA,ContratoPreventa.ColumnasCliente.ESTADO_CLIENTE,ContratoPreventa.ColumnasCliente.DIRECCION
        ,ContratoPreventa.ColumnasCliente.DEUDA_VENCIDA,ContratoPreventa.ColumnasCliente.DEUDA_NOVENCIDA,ContratoPreventa.ColumnasCliente.DESC_LISTA,ContratoPreventa.ColumnasCliente.CODIGO,ContratoPreventa.ColumnasCliente.COD_LOCAL);
        String[] selectionArgs = {clienteNew.getCodLista(),clienteNew.getStatus(),clienteNew.getDireccion(),clienteNew.getDeudaVencida().toString(),clienteNew.getDeudaNoVencida().toString(),clienteNew.getDescListaPrecios(),clienteNew.getCodCliente(),clienteNew.getCodLocal()};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteDatosUsuario(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.DATOS_USUARIO);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }
    public void deleteDatosCondiciones(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.CONDICIONES_PAGO);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteArticulos(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.ARTICULO);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteSugerido(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.SUGERIDO_LOCAL);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteFocus(){
        String deleteQuery = "DELETE FROM %s ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.FOCUS_LOCAL);
        Cursor cursor = getDb().rawQuery(sql, null);
        DatabaseUtils.dumpCursor(cursor);
    }


    public void deletePedidoLocalById(String id){
        String deleteQuery = "DELETE FROM %s where %s=? ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL, ContratoPreventa.ColumnasCabeceraPedidoLocal.ID);
        String[] selectionArgs = {id};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteNoPedidoLocalById(String id){
        String deleteQuery = "DELETE FROM %s where %s=? ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.NO_PEDIDO_LOCAL, ContratoPreventa.ColumnasTablaNoPedidoLocal.ID);
        String[] selectionArgs = {id};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }


    public void deleteDetallePedidoLocalById(String id){
        String deleteQuery = "DELETE FROM %s where %s=? ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.DETALLE_PEDIDO_LOCAL, ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO);
        String[] selectionArgs = {id};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }

    public void deleteCliente(Cliente clienteOld){
        String deleteQuery = "DELETE FROM %s where %s=? and %s=? ";
        String sql = String.format(deleteQuery, BaseDatosTomaPedidos.Tablas.CLIENTE, ContratoPreventa.ColumnasCliente.CODIGO,ContratoPreventa.ColumnasCliente.COD_LOCAL);
        String[] selectionArgs = {clienteOld.getCodCliente(),clienteOld.getCodLocal()};
        Cursor cursor = getDb().rawQuery(sql, selectionArgs);
        DatabaseUtils.dumpCursor(cursor);
    }



    public void deleteNoPedidosLocalesAnteriores(String fechaDevice){
            String deleteQuery = "DELETE FROM "+BaseDatosTomaPedidos.Tablas.NO_PEDIDO_LOCAL;
            deleteQuery=deleteQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasTablaNoPedidoLocal.FECHAREGISTRO+")";
            deleteQuery = deleteQuery + " < '" + fechaDevice + "' ";
            Cursor cursor = getDb().rawQuery(deleteQuery, null);
            DatabaseUtils.dumpCursor(cursor);
        }


//    public void deleteDetallesPedidosLocalesAnteriores(String codCab){
//        String deleteQuery = "DELETE FROM "+BaseDatosTomaPedidos.Tablas.DETALLE_PEDIDO_LOCAL;
//        deleteQuery=deleteQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasDetallesPedidoLocal.CODCABECERAPEDIDO+")";
//        deleteQuery = deleteQuery + " = '" + codCab + "' ";
//
//        Cursor cursor = getDb().rawQuery(deleteQuery, null);
//        DatabaseUtils.dumpCursor(cursor);
//    }

    public void deletePedidosLocalesAnteriores(String fechaDevice){
        String deleteQuery = "DELETE FROM "+BaseDatosTomaPedidos.Tablas.CABECERA_PEDIDO_LOCAL;
        deleteQuery=deleteQuery+" where strftime('%d-%m-%Y' ,"+ContratoPreventa.ColumnasCabeceraPedidoLocal.FECHAREGISTRO+")";
        deleteQuery = deleteQuery + " < '" + fechaDevice + "' ";

        Cursor cursor = getDb().rawQuery(deleteQuery, null);
        DatabaseUtils.dumpCursor(cursor);
    }



    public long contarTablas(){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db,"SQLITE_MASTER");
    }

}