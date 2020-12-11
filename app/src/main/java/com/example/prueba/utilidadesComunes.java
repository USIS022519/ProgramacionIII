package com.example.prueba;

/**
 * @author 682 Luis Enrique Hernandez
 * @author 687 Juan Ernesto Perez
 * @author 688 Jose Mario Catelino
 * @author 689 Jose Mario Villega
 */

/**
 * @class utilidadesComunes me sirve para la ip del servidor y generar un randomId
 */
public class utilidadesComunes {
    static String url_consulta = "http://192.168.1.7:5984/db_agenda/_design/agenda/_view/mi-agenda";
    static String url_mto = "http://192.168.1.7:5984/db_agenda/";

    /**
     * @function generateUniqueId sirve para generar un identidicador unico
     * @return string id
     */
    public String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }
}
