#!/bin/bash

# ==============================================================================
# Actividad 3: JIT vs AOT (GraalVM)
# Script de automatización para Ubuntu 22.04 LTS (Gestión vía SDKMAN!)
# Objetivo: Instalar GraalVM para Java 17, compilar AOT y generar un binario nativo.
# ==============================================================================

# Detener el script inmediatamente si ocurre algún error
set -e

echo "=== 1. Actualizando paquetes y dependencias nativas del sistema ==="
sudo apt-get update && sudo apt-get install -y curl build-essential zlib1g-dev unzip


# MIRAR : https://www.graalvm.org/downloads/#
echo -e "\n=== 2. Configurando SDKMAN! e instalando GraalVM 17 ==="
# Instalamos SDKMAN! si no existe en el sistema
if [ ! -d "$HOME/.sdkman" ]; then
    echo "Instalando SDKMAN! desde el repositorio oficial..."
    curl -s "https://sdkman.io" | bash
    # Inicializar SDKMAN! en la sesión actual de la terminal
    source "$HOME/.sdkman/bin/sdkman-init.sh"
else
    echo "SDKMAN! ya está instalado. Inicializando..."
    source "$HOME/.sdkman/bin/sdkman-init.sh"
fi

# Instalar la versión estable de GraalVM basada en Java 17
# Nota: La etiqueta habitual en SDKMAN! para la comunitaria de Java 17 es '17.0.x-graalce'
# Forzamos la instalación de la versión LTS 17 de GraalVM CE
echo "Instalando GraalVM CE (Java 17)..."
sdk install java 17.0.9-graalce || echo "GraalVM 17 ya está instalada o disponible."
sdk use java 17.0.9-graalce

# Validar que la terminal está usando el compilador correcto
echo "Verificando versión activa de Java en el entorno:"
java -version

echo -e "\n=== 3. Preparando el entorno de compilación ==="
# Comprobamos la estructura de paquetes (se asume que existe la carpeta ./pia/)
if [ ! -f "pia/Actividad1.java" ]; then
    echo "Error: No se encuentra 'pia/Actividad1.java' en el directorio actual."
    exit 1
fi

echo "Compilando código fuente a Bytecode estándar (.class)..."
javac pia/Actividad1.java

echo -e "\n=== 4. Generando el Binario Nativo (Compilación AOT) ==="
# GraalVM 17 incluye la herramienta native-image directamente en el PATH gestionado por SDKMAN!
native-image --no-fallback -cp . pia.Actividad1 act1_nativo

echo -e "\n=== 5. ¡Éxito! Comparativa y Prueba de Ejecución ==="
echo "-------------------------------------------------------"
echo "Tamaño del bytecode (.class): $(du -sh pia/Actividad1.class | cut -f1)"
echo "Tamaño del binario nativo:    $(du -sh act1_nativo | cut -f1)"
echo "-------------------------------------------------------"

echo "Ejecutando el binario nativo generado de forma directa:"
./act1_nativo DevOps_SDKMAN 3
