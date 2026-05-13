# 🚦 Sistema de Semáforos Inteligentes con Greenfoot + ActiveMQ

**Simulación de una intersección urbana con semáforos adaptativos basados en sensores y mensajería en tiempo real.**

## 🎯 Descripción Rápida

Un sistema completo de control de tráfico en Greenfoot donde:
- ✅ **4 vías** con vehículos que se mueven inteligentemente
- ✅ **Sensores** que detectan vehículos y publican a ActiveMQ
- ✅ **Controlador central** que orquesta cambios de semáforos
- ✅ **Mensajería en tiempo real** usando ActiveMQ
- ✅ **Lógica adaptativa** que prioriza carriles congestionados

## 📂 Estructura de Proyecto

```
ILI140_T2_SEMAFOROS/
├── traffic-sim/                    # 🎮 Simulación Greenfoot
│   ├── MyWorld.java               # Intersección (4 carriles + 4 semáforos)
│   ├── Car.java                   # Vehículos inteligentes
│   ├── Sensor.java                # Detectores que publican a ActiveMQ
│   ├── TrafficLight.java          # Semáforos (RED/YELLOW/GREEN)
│   ├── TrafficController.java     # ⭐ NUEVO - Controlador central
│   ├── project.greenfoot          # Configuración del proyecto
│   └── images/                    # Recursos visuales
│
├── apache-activemq-5.19.6-bin/   # 📨 Broker de mensajes
│   └── apache-activemq-5.19.6/
│       ├── bin/
│       │   ├── activemq.bat      # 🚀 Iniciar servidor
│       │   └── ...
│       └── conf/
│           └── activemq.xml      # Configuración
│
├── libraries/                     # 📚 Dependencias JAR
│   ├── activemq-all-5.19.6.jar
│   ├── log4j-*.jar
│   ├── slf4j-*.jar
│   └── ...
│
├── README.md                      # ← ESTÁS AQUÍ
├── SETUP.md                       # ⚙️ Configuración completa
├── ACTIVEMQ_GUIDE.md             # 📨 Guía ActiveMQ
├── ARCHITECTURE.md               # 🏗️ Diagramas y flujos
├── TESTING.md                    # ✅ Pruebas y verificación
└── EXAMPLES.md                   # 💡 Ejemplos y personalizaciones
```

## 🚀 Inicio Rápido (5 minutos)

### Paso 1️⃣: Iniciar ActiveMQ

```powershell
cd "C:\Users\cacer\dev\ILI140\ILI140_T2_SEMAFOROS\apache-activemq-5.19.6-bin\apache-activemq-5.19.6\bin"
.\activemq.bat
```

Espera hasta ver: `Listening for connections at: tcp://localhost:61616`

### Paso 2️⃣: Compilar en Greenfoot

1. Abre Greenfoot
2. Abre proyecto: `traffic-sim/`
3. Presiona **Ctrl+K** (Compile)

### Paso 3️⃣: Ejecutar Simulación

Presiona **Ctrl+R** (Run) y observa:
- 🚗 Vehículos azules en los 4 carriles
- 🚦 Semáforos que cambian de color
- 📊 Logs en la consola

**¡Listo! El sistema está funcionando.**

## 📊 Cómo Funciona

```
Sensor detecta vehículos
    ↓
Publica JSON a ActiveMQ
    ↓
TrafficController escucha
    ↓
Analiza datos y decide qué carril
    ↓
Cambia semáforos
    ↓
Car responde al cambio
    ↓
Avanza o se detiene
```

### Ejemplo de Flujo Real

```
[SENSOR-NORTH] Publicado: {"lane":"NORTH","cars":3,"timestamp":...}
[CONTROLLER] Recibido: NORTH -> 3 vehículos
[CONTROLLER] VERDE asignado a: NORTH (3 vehículos)
[TRAFFIC-NORTH] Estado: GREEN (50f)
[TRAFFIC-SOUTH] Estado: RED
[TRAFFIC-EAST] Estado: RED
[TRAFFIC-WEST] Estado: RED

→ Los 3 vehículos en NORTH avanzan
→ Otros carriles se detienen
```

## 📚 Documentación Detallada

| Documento | Contenido |
|-----------|-----------|
| **[SETUP.md](SETUP.md)** | Instalación completa, configuración de librerías, parámetros |
| **[ACTIVEMQ_GUIDE.md](ACTIVEMQ_GUIDE.md)** | Cómo usar ActiveMQ, dashboard, troubleshooting |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Diagramas de componentes, flujos de datos, ciclos |
| **[TESTING.md](TESTING.md)** | 10 pruebas de verificación con pasos y resultados esperados |
| **[EXAMPLES.md](EXAMPLES.md)** | Ejemplos de código: personalizar velocidad, algoritmos, etc. |

## 🎮 Componentes Principales

### **MyWorld** - La Intersección
- Crea el mapa 800x600
- Posiciona 4 semáforos (N, S, E, O)
- Crea sensores y vehículos
- Llama al controlador cada frame

### **Car** - Vehículos Inteligentes
- Se mueven en línea recta (no giran)
- Responden a semáforos
- Detectan vehículos adelante
- Se detienen en rojo/amarillo

### **Sensor** - Detectores de Tráfico
- Detectan vehículos en rango de 50px
- Publican JSON cada 10 frames
- Envían a `traffic.sensor.{LANE}`

### **TrafficLight** - Semáforos
- 3 estados: RED, YELLOW, GREEN
- Transiciones automáticas
- Visual: caja con círculos de color

### **TrafficController** ⭐ - Orquestador
- **Escucha** sensores en ActiveMQ
- **Analiza** cantidad de vehículos
- **Decide** qué carril obtiene verde
- **Coordina** cambios de semáforos

## ⚡ Características Principales

✅ **Sistema de mensajería real** - ActiveMQ, no simulado  
✅ **Comunicación asincrónica** - Sensores → Broker → Controlador  
✅ **Algoritmo adaptativo** - Prioriza carriles con más tráfico  
✅ **Lógica robusta** - Manejo de vehículos múltiples  
✅ **Visualización clara** - Semáforos y vehículos en tiempo real  
✅ **Logs detallados** - Seguimiento completo en consola  

## 🔧 Requisitos

- **Java** 8 o superior
- **Greenfoot** 3.x
- **ActiveMQ** 5.19.6 (incluido)
- **6 librerías JAR** (incluidas en `/libraries/`)
- **~200 MB de espacio** en disco

## 📋 Checklist de Configuración

- [ ] ActiveMQ descargado en `/apache-activemq-5.19.6-bin/`
- [ ] Greenfoot 3.x instalado
- [ ] JAR añadidas a Project Properties
- [ ] `activemq.bat` ejecutable
- [ ] Puerto 61616 disponible
- [ ] Java version correcta (`java -version`)

## 🐛 Troubleshooting Rápido

| Problema | Solución |
|----------|----------|
| "Port 61616 already in use" | Cambiar puerto en activemq.xml |
| "Cannot find symbol" | Verificar que todas las clases estén compiladas |
| "Librerias no encontradas" | Añadir JAR en Project Properties → Libraries |
| "Sensores no publican" | Verificar que vehículos estén dentro de 50px |
| "Semáforos no cambian" | Revisar consola, verificar processTraffic() |

Ver [TESTING.md](TESTING.md) para pruebas completas.

## 💡 Ejemplos de Personalización

### Agregar más vehículos
Modifica loops en `MyWorld.java`:
```java
for (int i = 0; i < 10; i++) {  // En lugar de 3
    addObject(new Car("NORTH"), CENTER_X, 50 + i*60);
}
```

### Cambiar velocidad
En `Car.java`:
```java
private int speed = 3;  // En lugar de 2
```

### Cambiar duración de semáforos
En `TrafficLight.java`:
```java
private static final int DEFAULT_GREEN = 100;  // 2 segundos
```

Ver [EXAMPLES.md](EXAMPLES.md) para más ejemplos.

## 📊 Estadísticas de Rendimiento

A **50 fps** (velocidad por defecto Greenfoot):
- ✅ 3-5 vehículos por carril: Sin lag
- ✅ 8-10 vehículos: Rendimiento normal
- ⚠️ 15+ vehículos: Posible bajada de fps

## 🎯 Próximos Pasos

### Básico
1. ✅ Ejecutar simulación base
2. ✅ Observar cambios de semáforos
3. ✅ Verificar logs en consola

### Intermedio
1. Agregar más vehículos
2. Cambiar algoritmo de control
3. Personalizar duración de fases

### Avanzado
1. Agregar estadísticas de tráfico
2. Implementar SARSA learning (futuro)
3. Crear multi-intersecciones

## 🔮 Extensiones Futuras

- **SARSA Learning**: Entrenar semáforos con refuerzo
- **GAN**: Generar escenarios adversarios
- **Keep-Alive**: Monitoreo de sensores fallidos
- **Multi-intersección**: Sistema de 4+ intersecciones
- **Base de datos**: Guardar estadísticas
- **Emergencias**: Ambulancias con prioridad

## 📞 Soporte

### Preguntas sobre:
- **Configuración**: Ver [SETUP.md](SETUP.md)
- **ActiveMQ**: Ver [ACTIVEMQ_GUIDE.md](ACTIVEMQ_GUIDE.md)
- **Arquitectura**: Ver [ARCHITECTURE.md](ARCHITECTURE.md)
- **Testing**: Ver [TESTING.md](TESTING.md)
- **Código**: Ver [EXAMPLES.md](EXAMPLES.md)

## 📄 Licencia

Proyecto educativo ILI140 - Mayo 2026

---

**Estado:** ✅ Funcional y listo para usar  
**Versión:** 1.0  
**Última actualización:** Mayo 2026

**¿Necesitas ayuda?** Lee [SETUP.md](SETUP.md) → [TESTING.md](TESTING.md) → [TROUBLESHOOTING en ACTIVEMQ_GUIDE.md](ACTIVEMQ_GUIDE.md)
