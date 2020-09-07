package com.legado.preventagps.bd;

import java.util.UUID;

/**
 * Clase que establece los nombres a usar en la base de datos
 */
public class ContratoPreventa {

    interface ColumnasCliente {
        String CODIGO = "cod_cliente";
        String FECHA="fecha";
        String DESC_CLIENTE = "desc_cliente";
        String COORDENADAS = "coordenadas";
        String CODRUTA = "cod_ruta";
        String DESCRUTA = "desc_ruta";
        String DIRECCION = "direccion";
        String COD_LOCAL="cod_local";
        String COD_LISTA="cod_lista";
        String COD_EMPRESA="cod_empresa";
        String FLAG_DESPACHADO="flag_despachado";
        String FLAG_IGNORESEC="flag_ignore_sec";
        String ESTADO_JORNADA="estado_jornada";
        String ESTADO_GEOSIS="estado_geosis";
        String ESTADO_CLIENTE="estado_cliente";
        String NEW_TELEFONO="new_telefono";

        String TIPO_NEGOCIO = "tipo_negocio";
        String DESC_LISTA = "desc_lista";
        String LIMITE_CREDITO = "limite_credito";
        String DEUDA_VENCIDA = "deuda_vencida";
        String DEUDA_NOVENCIDA = "deuda_novencida";
        String ANTIGUEDAD = "antiguedad";
        String CUMPLEAÑOS = "cumpleanios";
        String VELOCIDAD_PAGO="velocidad_pago";
        String CANT_PEDIDOS="cantidad_pedidos";

        String NOMBRES="nombres";
        String APELLIDOS="apellidos";
        String NEW_DIRECCION="new_direccion";
        String IS_AUDITADO="is_auditado";
    }

    interface ColumnasDatosCliente {
        String COD_CLIENTE = "cod_cliente";
        String COD_LOCAL="cod_local";
        String TIPO_NEGOCIO = "tipo_negocio";
        String COD_LISTA = "cod_lista";
        String DESC_LISTA = "desc_lista";
        String LIMITE_CREDITO = "limite_credito";
        String DEUDA_VENCIDA = "deuda_vencida";
        String DEUDA_NOVENCIDA = "deuda_novencida";
        String ANTIGUEDAD = "antiguedad";
        String CUMPLEAÑOS = "cumpleanios";
        String VELOCIDAD_PAGO="velocidad_pago";
        String CANT_PEDIDOS="cantidad_pedidos";

    }

    interface ColumnasMontosVendedor {
        String MONTO = "monto_valor";
        String TIPO = "tipo_valor";
    }
    interface ColumnasCabeceraMontosVendedor {
        String CABECERA = "cabecera_nombre";

    }

    interface ColumnasDatosMetricas {
        String VENDEDOR = "desc_vendedor";
        String FECHA = "fecha_metrica";
        String PERIODO = "periodo_metrica";
        String TICKET_DIARIO = "ticket_diario";
        String TICKET_MENSUAL = "ticket_mensual";
        String RUTASHOY = "rutas_hoy";
        String PERMITE_OFFLINE="permite_offline";

    }
//OFFLINE

    interface ColumnasDatosUsuario {
        String COD_EMPRESA = "cod_empresa";
        String COD_MESA="cod_mesa";
        String DESC_MESA = "desc_mesa";
        String TIPO_CODIGO = "tipo_codigo";
        String COD_LOCALIDAD = "cod_localidad";
        String DESC_LOCALIDAD = "desc_localidad";
        String COD_CANAL = "cod_canal";
        String DESC_CANAL = "desc_canal";
        String COD_VENDEDOR = "cod_vendedor";

        String COD_ALMACEN = "cod_almacen";
        String DESC_ALMACEN = "desc_almacen";

        String COD_SEDE = "cod_sede";
        String PERMITE_CREDITO="permite_credito";
        String PERMITE_CAMPANIA="permite_campania";

    }
    interface ColumnasCondiciones {
        String COD_CONDICION = "cod_condicion";
        String DESC_CONDICION = "desc_condicion";
        String TIPO_CONDICION = "tipo_condicion";
        String COD_LISTA = "cod_lista";
    }

    interface ColumnasArticulos {
        String COD_ITEM = "cod_item";
        String DESC_ITEM = "desc_item";
        String UM_ITEM = "um_item";
        String COD_LISTA = "cod_lista";
        String PRECIO_BASE = "precio_base";
        String SALDO = "saldo";
        String COD_ALMACEN = "cod_almacen";
        String COND_PAGO = "cond_pago";
        String PRECIO_SUGERIDO = "precio_sugerido";
    }

    interface ColumnasCabeceraPedidoLocal{
        String ID = "id_cabecera_pedidolocal";
        String CODIGOEMPRESA= "cod_empresa";
        String CODLOCALIDAD = "cod_localidad";
        String DESCLOCALIDAD="desc_localidad";
        String CODALMACEN="cod_almacen";
        String DESCALMACEN="desc_almacen";
        String USUARIOXRAY="usuario_xray";
        String CODIGOCLIENTE="cod_cliente";
        String DESCCLIENTE="desc_cliente";
        String CODCONDICIONPAGO="cod_condicionpago";
        String CODLISTAPRECIOS="cod_listaprecios";
        String CODDIRECCION="cod_direccion";
        String MONTOVENTA="monto_venta";
        String FECHAREGISTRO="fecha_registro";
        String STATUSPEDIDOLOCAL="status_pedido";
        String CODSEDE = "cod_sede";
        String CODMESA = "cod_mesa";
        String CODCANAL = "cod_canal";
        String COORDENADAS = "coordenadas";
        String CODRUTA = "cod_ruta";
        String CODVENDEDOR="cod_vendedor";
    }
    interface ColumnasDetallesPedidoLocal
    {
        String CODCABECERAPEDIDO= "cod_cabecerapedido";
        String CODIGOARTICULO = "cod_articulo";
        String CODIGOLISTA= "cod_lista";
        String DESCITEM = "desc_item";
        String PRECIOBASE="precio_base";
        String PRECIOSUGERIDO="precio_sugerido";
        String UM = "um";
        String CANTIDAD= "cantidad";
    }




    interface ColumnasTablaNoPedidoLocal{
        String ID = "id_motivo_nopedidolocal";
        String CODIGOEMPRESA= "cod_empresa";
         String USUARIOXRAY="usuario_xray";
         String CODCLIENTE = "cod_cliente";
        String DESCCLIENTE = "desc_cliente";
        String CODRUTA="cod_ruta";
        String MOTIVONOPEDIDO="desc_motivo_nopedido";
        String CODDIRECCION = "cod_local";
        String DESCDIRDESPACHO = "desc_direccion";
        String FECHAREGISTRO="fecha_registro";
        String STATUSNOPEDIDOLOCAL="status_nopedido";
        String COORDENADAS = "coordenadas";
    }


    interface ColumnasSugeridoLocal{
        String CODCLIENTE = "cod_cliente";
        String COD_ITEM = "cod_item";
        String CANT_SUGERIDA="cant_sugerida";
        String NRO_VECES="nro_veces";
    }

    interface ColumnasFocusLocal{
        String COD_ITEM = "cod_item";
        String CANT_FOCUS="cant_sugerida";
     }

    interface ColumnasTablaMaestra{
        String NOMBRE = "nombre_tabla";
     }

    //    public static class TablaMaestras implements ColumnasTablaMaestra {
//        public static String generarIdTablaMaestra() {
//            return "ID-" + UUID.randomUUID().toString();
//        }
//    }
//
    public static class CabeceraPedidoMovil implements ColumnasCabeceraPedidoLocal {
        public static String generarIdCabeceraPedidoMovil() {
            return "ID-" + UUID.randomUUID().toString();
        }
    }

    public static class IdNoPedidoMovil implements ColumnasTablaNoPedidoLocal {
        public static String generarIdNoPedidoMovil() {
            return "ID-" + UUID.randomUUID().toString();
        }
    }

//
//    public static class IdNoPedidoMovil implements ColumnasTablaNoPedidoLocal {
//        public static String generarIdNoPedidoMovil() {
//            return "ID-" + UUID.randomUUID().toString();
//        }
//    }

    private ContratoPreventa() {
    }

}
