package com.legado.preventagps.util;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 17/5/2017.
 */

public class UtilRepository {
    public static String[] listarMotivoNoPedido(){

        String[] motivos=new String[11];
        motivos[0]="01-SIN DINERO";
        motivos[1]="02-PTO DE VENTA CERRADO DIA";
        motivos[2]="03-BAJA ROTACION / STOCK";
        motivos[3]="04-PTO DE VENTA CERRADO DEFINITIVO";
        motivos[4]="05-ACCIONES DE LA COMPETENCIA";
        motivos[5]="06-COMPRA MAYORISTA";
        motivos[6]="07-QUIERE DESCUENTO";
        motivos[7]="08-QUIERE CREDITO";
        motivos[8]="09-SIN ENVASES / CAJAS";
        motivos[9]="10-OTROS / MOTIVOS";
        motivos[10]="11-REGRESAR DESPUES";

        return motivos;

    }


}
