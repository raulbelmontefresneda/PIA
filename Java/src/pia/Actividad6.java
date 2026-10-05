package pia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad 6: Filtrado de Casos en Paralelo (Hilos Nativos)
 * Objetivo: Particionar un dataset en memoria y procesar el filtrado de filas
 * concurrentemente utilizando la clase nativa Thread de Java.
 */
public class Actividad6 {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Error: Argumentos insuficientes.");
            System.out.println("Uso: java pia.Actividad6 <archivo_origen> <indice_columna> <valor_buscado> <num_hilos>");
            System.exit(1);
        }

        String origen = args[0];
        int indiceColumna = Integer.parseInt(args[1]);
        String valorBuscado = args[2];
        int numHilos = Integer.parseInt(args[3]);

        List<String> filasData = new ArrayList<>();

        // 1. Cargar la sección @data en memoria
        try (BufferedReader br = new BufferedReader(new FileReader(origen))) {
            String linea;
            boolean seccionData = false;

            while ((linea = br.readLine()) != null) {
                String lineaTrim = linea.trim();

                if (lineaTrim.isEmpty() || lineaTrim.startsWith("%")) {
                    continue;
                }

                if (!seccionData) {
                    if (lineaTrim.toLowerCase().startsWith("@data")) {
                        seccionData = true;
                    }
                } else {
                    filasData.add(lineaTrim);
                }
            }
        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Error: El índice de la columna y el número de hilos deben ser enteros.");
            System.exit(1);
        }

        System.out.println("==================================================");
        System.out.println("INICIANDO FILTRADO EN PARALELO (HILOS NATIVOS)");
        System.out.println("==================================================");
        System.out.println("Filas totales a analizar: " + filasData.size());
        System.out.println("Número de hilos nativos: " + numHilos);
        System.out.println("Criterio: Columna [" + indiceColumna + "] == '" + valorBuscado + "'");
        System.out.println("--------------------------------------------------");

        long tiempoInicio = System.currentTimeMillis();

        // Lista hilo-segura (thread-safe) para acumular las coincidencias desde múltiples hilos simultáneamente
        List<String> resultadosFiltrados = Collections.synchronizedList(new ArrayList<>());
        List<Thread> hilos = new ArrayList<>();

        int totalFilas = filasData.size();
        int tamanoBloque = (int) Math.ceil((double) totalFilas / numHilos);

        // 2. Particionado de datos y creación de hilos nativos
        for (int i = 0; i < numHilos; i++) {
            final int inicio = i * tamanoBloque;
            final int fin = Math.min(inicio + tamanoBloque, totalFilas);

            if (inicio >= totalFilas) {
                break;
            }

            // Trabajo asignado a cada hilo (Runnable via Lambda)
            Thread hilo = new Thread(() -> {
                List<String> coincidenciasLocales = new ArrayList<>();

                for (int j = inicio; j < fin; j++) {
                    String fila = filasData.get(j);
                    String[] valores = fila.split(",");

                    if (indiceColumna < valores.length) {
                        if (valores[indiceColumna].trim().equalsIgnoreCase(valorBuscado)) {
                            coincidenciasLocales.add(fila);
                        }
                    }
                }
                // Agregamos las coincidencias locales a la lista global sincronizada
                resultadosFiltrados.addAll(coincidenciasLocales);
            });

            hilos.add(hilo);
            hilo.start(); // Inicia la ejecución del hilo en paralelo
        }

        // 3. Sincronización: Esperar a que todos los hilos finalicen (join)
        for (Thread hilo : hilos) {
            try {
                hilo.join(); // Bloquea el hilo principal hasta que termine la ejecución del subproceso
            } catch (InterruptedException e) {
                System.err.println("Hilo interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        long tiempoFin = System.currentTimeMillis();

        // 4. Informe de resultados
        System.out.println("¡Procesamiento paralelo finalizado!");
        System.out.println("Coincidencias encontradas: " + resultadosFiltrados.size());
        System.out.println("Tiempo total de ejecución: " + (tiempoFin - tiempoInicio) + " ms");
        System.out.println("\n--- Muestra de las 3 primeras coincidencias ---");
        int muestras = Math.min(3, resultadosFiltrados.size());
        for (int i = 0; i < muestras; i++) {
            System.out.println(" Fila filtrada " + (i + 1) + ": " + resultadosFiltrados.get(i));
        }
        System.out.println("==================================================");
    }
}
