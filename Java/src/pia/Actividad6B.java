package pia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad 6B: Filtrado de Casos con Hilos Virtuales (Java 21+)
 * Ejecutable directo sin argumentos de consola.
 */
public class Actividad6B {

    public static void main(String[] args) {
        // Configuración de parámetros locales
        String origen = "diabetes.arff";
        int indiceColumna = 8;
        String valorBuscado = "tested_positive";
        int numHilosVirtuales = 50;            // Se pueden invocar decenas o cientos sin impacto de RAM

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
        System.out.println("INICIANDO FILTRADO CON VIRTUAL THREADS (JAVA 21+)");
        System.out.println("==================================================");
        System.out.println("Filas totales a analizar: " + filasData.size());
        System.out.println("Número de Virtual Threads invocados: " + numHilosVirtuales);
        System.out.println("Criterio: Columna [" + indiceColumna + "] == '" + valorBuscado + "'");
        System.out.println("--------------------------------------------------");

        long tiempoInicio = System.currentTimeMillis();

        List<String> resultadosFiltrados = Collections.synchronizedList(new ArrayList<>());
        List<Thread> hilosVirtuales = new ArrayList<>();

        int totalFilas = filasData.size();
        int tamanoBloque = (int) Math.ceil((double) totalFilas / numHilosVirtuales);

        for (int i = 0; i < numHilosVirtuales; i++) {
            final int inicio = i * tamanoBloque;
            final int fin = Math.min(inicio + tamanoBloque, totalFilas);

            if (inicio >= totalFilas) break;

            Thread hiloVirtual = Thread.ofVirtual()
                    .name("vt-filter-worker-", i)
                    .start(() -> {
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

            hilosVirtuales.add(hiloVirtual);
        }

        for (Thread hilo : hilosVirtuales) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long tiempoFin = System.currentTimeMillis();

        System.out.println("¡Procesamiento con Virtual Threads finalizado!");
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
