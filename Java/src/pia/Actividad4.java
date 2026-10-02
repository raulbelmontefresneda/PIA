package pia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad 4: El Lector de Datasets (.ARFF Manual)
 * Objetivo: Leer un archivo .arff sin librerías externas y analizar su estructura.
 */
public class Actividad4 {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: Falta la ruta del archivo.");
            System.out.println("Uso: java pia.Actividad4 <ruta_al_archivo_diabetes.arff>");
            System.exit(1);
        }

        String rutaArchivo = args[0];
        List<String> atributos = new ArrayList<>();
        List<String> instancias = new ArrayList<>();
        String nombreRelacion = "";
        boolean seccionData = false;

        // Try-with-resources: Cierra el archivo automáticamente (Equivalente al "with open" de Python)
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                // Ignorar líneas vacías o comentarios
                if (linea.isEmpty() || linea.startsWith("%")) {
                    continue;
                }

                // Procesar la cabecera del archivo ARFF
                if (!seccionData) {
                    String lineaMinuscula = linea.toLowerCase();
                    if (lineaMinuscula.startsWith("@relation")) {
                        nombreRelacion = linea.substring(9).trim();
                    } else if (lineaMinuscula.startsWith("@attribute")) {
                        // Guardar la definición del atributo
                        atributos.add(linea.substring(10).trim());
                    } else if (lineaMinuscula.startsWith("@data")) {
                        seccionData = true;
                    }
                } else {
                    // Si ya pasamos @data, todo lo demás son filas/instancias
                    instancias.add(linea);
                }
            }

            // Mostrar el informe por consola
            System.out.println("==================================================");
            System.out.println("REPORT DE DATASET .ARFF DETECTADO");
            System.out.println("==================================================");
            System.out.println("Relación (Dataset): " + nombreRelacion);
            System.out.println("Número Total de Columnas (Atributos): " + atributos.size());
            System.out.println("Número Total de Registros (Instancias): " + instancias.size());
            System.out.println("\n--- Lista de Atributos Detectados ---");
            for (int i = 0; i < atributos.size(); i++) {
                System.out.printf(" [%d] %s%n", i, atributos.get(i));
            }

            System.out.println("\n--- Muestra de las 3 primeras instancias ---");
            int muestras = Math.min(3, instancias.size());
            for (int i = 0; i < muestras; i++) {
                System.out.println(" Fila " + (i + 1) + ": " + instancias.get(i));
            }
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo: " + e.getMessage());
            System.exit(1);
        }
    }
}
