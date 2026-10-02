package pia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Actividad 2: Tipado Fuerte y Colecciones Estructuradas
 * Conceptos: Primitivos vs Objetos, Arrays fijos, ArrayList y bucles for-each.
 */
public class Actividad2 {

    public static void main(String[] args) {
        System.out.println("--- Demostración de Tipado y Colecciones --- \n");

        // 1. TIPADO FUERTE (El compilador no te dejará reasignar tipos sobre la marcha)
        int puertoServidor = 8080;                  // Primitivo para enteros
        double porcentajeCpu = 87.5;               // Primitivo para decimales
        boolean esProduccion = true;                // Primitivo booleano
        String nombreServidor = "srv-prod-01";      // Objeto (Inmutable)

        System.out.printf("Servidor: %s | Puerto: %d | CPU: %.2f%% | Prod: %b%n",
                nombreServidor, puertoServidor, porcentajeCpu, esProduccion);

        // 2. ARRAYS DE TAMAÑO FIJO (Estructura clásica y estática en memoria)
        String[] ipsFijas = new String[3];
        ipsFijas[0] = "192.168.1.10";
        ipsFijas[1] = "192.168.1.11";
        ipsFijas[2] = "192.168.1.12";
        // ipsFijas[3] = "10.0.0.1"; // <- Esto lanzará un ArrayIndexOutOfBoundsException en ejecución

        System.out.println("\nListando IPs de la red (Array Fijo):");
        // Bucle for-each (Muy intuitivo para alumnos de Python)
        for (String ip : ipsFijas) {
            System.out.println(" -> Nodo detectado: " + ip);
        }

        // 3. COLECCIONES DINÁMICAS (ArrayList: El verdadero equivalente a la lista de Python)
        // Usamos la interfaz genérica List limitando el tipo estrictamente a <String>
        List<String> logsFiltrados = new ArrayList<>();

        // Añadir elementos dinámicamente (.append() en Python, .add() en Java)
        logsFiltrados.add("404 Not Found en /api/v1/users");
        logsFiltrados.add("500 Internal Server Error en /checkout");
        logsFiltrados.add("200 OK en /index.html");

        System.out.println("\nProcesando logs capturados (ArrayList Dinámico):");
        // Iteración de la lista dinámica
        for (String log : logsFiltrados) {
            if (log.contains("500") || log.contains("404")) {
                System.out.println("[ALERTA CRÍTICA] " + log);
            } else {
                System.out.println("[INFO] " + log);
            }
        }
    }
}
