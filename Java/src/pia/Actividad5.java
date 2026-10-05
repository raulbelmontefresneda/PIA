package pia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad 5: El Limpiador de Columnas (Discarding Columns)
 * Objetivo: Filtrar columnas por índice y exportar un nuevo dataset limpio.
 */
public class Actividad5 {

    public static void main(String[] args) {
        
        // Configuración de parámetros locales
        String origen = "diabetes.arff";
        String destino = "diabetes_limpio.arff";
        int columnaAEliminar = 0; // Elimina la primera columna (p.ej. 'preg' en diabetes.arff)

        System.out.printf("Iniciando purga de '%s'. Eliminando columna en índice [%d]...%n", origen, columnaAEliminar);

        System.out.printf("Iniciando purga. Eliminando columna en índice [%d]...%n", columnaAEliminar);

        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino))) {

            String linea;
            boolean seccionData = false;
            int contadorAtributos = 0;

            while ((linea = br.readLine()) != null) {
                String lineaTrim = linea.trim();

                // Mantener intactos comentarios y líneas vacías
                if (lineaTrim.isEmpty() || lineaTrim.startsWith("%")) {
                    bw.write(linea);
                    bw.newLine();
                    continue;
                }

                String lineaMinuscula = lineaTrim.toLowerCase();

                if (!seccionData) {
                    if (lineaMinuscula.startsWith("@attribute")) {
                        // Si es el atributo que queremos borrar, nos lo saltamos e incrementamos el índice
                        if (contadorAtributos == columnaAEliminar) {
                            System.out.println("-> Removiendo cabecera: " + lineaTrim);
                            contadorAtributos++;
                            continue;
                        }
                        contadorAtributos++;
                    } else if (lineaMinuscula.startsWith("@data")) {
                        seccionData = true;
                    }
                    // Escribir cabeceras que no se eliminan
                    bw.write(linea);
                    bw.newLine();
                } else {
                    // Procesar las filas de la sección @data
                    String[] valores = lineaTrim.split(",");

                    if (columnaAEliminar >= valores.length) {
                        System.err.println("Error: El índice indicado supera las columnas reales de los datos.");
                        System.exit(1);
                    }

                    // Reconstruir la fila usando una lista dinámica (excluyendo el índice seleccionado)
                    List<String> valoresFiltrados = new ArrayList<>();
                    for (int i = 0; i < valores.length; i++) {
                        if (i != columnaAEliminar) {
                            valoresFiltrados.add(valores[i]);
                        }
                    }

                    // Unir de nuevo por comas (Equivalente al ",".join(lista) de Python)
                    String nuevaLinea = String.join(",", valoresFiltrados);
                    bw.write(nuevaLinea);
                    bw.newLine();
                }
            }

            System.out.println("¡Proceso completado con éxito! Archivo guardado en: " + destino);

        } catch (IOException e) {
            System.err.println("Error en la manipulación de archivos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: El índice de la columna debe ser un número entero válido.");
        }
    }
}
