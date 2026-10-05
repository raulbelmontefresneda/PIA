package pia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad 6: Filtrado de Casos en Paralelo (Hilos Nativos)
 * Ejecutable directo sin argumentos de consola.
 */
public class Actividad6 {

    public static void main(String[] args) {
        // Configuración de parámetros locales
        String origen = "diabetes.arff";
        int indiceColumna = 8;                 // Columna 'class' en diabetes.arff
        String valorBuscado = "tested_positive";
        int numHilos = 4;                      // Número de hilos nativos a spawnear

        List<String> filasData = new ArrayList<>();

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
            return;
        }

        System.out.println("==================================================");
        System.out.println("INICIANDO FILTRADO EN PARALELO (HILOS NATIVOS)");
        System.out.println("==================================================");
        System.out.println("Filas totales a analizar: " + filasData.size());
        System.out.println("Número de hilos nativos: " + numHilos);
        System.out.println("Criterio: Columna [" + indiceColumna + "] == '" + valorBuscado + "'");
        System.out.println("--------------------------------------------------");

        long tiempoInicio = System.currentTimeMillis();

        List<String> resultadosFiltrados = Collections.synchronizedList(new ArrayList<>());
        List<Thread> hilos = new ArrayList<>();

        int totalFilas = filasData.size();
        int tamanoBloque = (int) Math.ceil((double) totalFilas / numHilos);

        for (int i = 0; i < numHilos; i++) {
            final int inicio = i * tamanoBloque;
            final int fin = Math.min(inicio + tamanoBloque, totalFilas);

            if (inicio >= totalFilas) break;

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
                resultadosFiltrados.addAll(coincidenciasLocales);
            });

            hilos.add(hilo);
            hilo.start();
        }

        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long tiempoFin = System.currentTimeMillis();

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
