package pia;

/**
 * Actividad 1: Del Script al Bytecode (Hola Mundo)
 * Conceptos: Estructura de clases, método main y paso de argumentos.
 */
public class Actividad1 {

    /* En Java, el punto de entrada siempre requiere una clase
       y este método 'main' exacto. */

    public static void main(String[] args) {

        // Validación de argumentos (equivalente a $# en Bash o len(sys.argv) en Python)
        if (args.length < 2) {
            System.out.println("Error: Faltan argumentos.");
            System.out.println("Uso: java pia.Actividad1 <nombre_usuario> <repeticiones>");
            System.exit(1); // Salida con código de error
        }

        // Asignación de variables con tipado estricto
        String usuario = args[0];

        try {
            /* Java es fuertemente tipado... Necesita:
               Conversión explícita de tipos (Casting / Parsing)  */
            int repeticiones = Integer.parseInt(args[1]);

            System.out.println("Hola, " + usuario + ". Ejecutando bucle de control...");

            // Bucle indexado estándar (Estilo C / Bash)
            for (int i = 1; i <= repeticiones; i++) {
                System.out.printf("[Hilo Main] Iteración %d de %d para %s%n", i, repeticiones, usuario);
            }

        } catch (NumberFormatException e) {
            // Captura de excepciones obligatoria si el parseo falla
            System.err.println("Error: El segundo argumento debe ser un número entero válido.");
            System.exit(1);
        }
    }
}
