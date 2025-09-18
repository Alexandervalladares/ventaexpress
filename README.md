
## Video demostrativo

[![Ver demostración en video] https://drive.google.com/drive/folders/1LAQ2YXJEhxyJK2SZgAv6Q4jXTYRYZmDf?usp=drive_link
INTEGRANTES 
ALEXANDER VALLADARES VA230394
Enrique Delgado Peñate DP240093
Adriel Montano ML232940

 VentaExpress

VentaExpress es una aplicación Android orientada a la gestión y visualización de productos en un catálogo tipo e-commerce. El proyecto demuestra cómo estructurar una app con el patrón Modelo-Vista-Controlador (MVC) y cómo emplear un `RecyclerView` para renderizar listados de forma eficiente.

Arquitectura MVC

La aplicación se ha organizado siguiendo el patrón MVC:

- **Modelo**: clases de datos que representan entidades de negocio como productos, categorías y carritos. Encapsulan la información y la lógica simple necesaria para manipular los datos.
- **Vista**: actividades, fragmentos y archivos de layout responsables de mostrar la interfaz de usuario. En este caso, el `RecyclerView` y sus elementos de celda reflejan el contenido preparado por el controlador.
- **Controlador**: componentes que coordinan la comunicación entre el modelo y la vista (por ejemplo, `Activity`/`Fragment` que prepara la lista de productos, invoca adaptadores y responde a interacciones del usuario).

Esta separación favorece la mantenibilidad, permite probar cada pieza por separado y facilita reemplazar componentes sin impactar en el resto de la aplicación.

 Flujo del RecyclerView

El listado de productos se alimenta mediante un `RecyclerView` configurado con los siguientes pasos:

1. El controlador obtiene la colección de productos del modelo (estático o a través de una fuente de datos).
2. Se instancia un `RecyclerView.Adapter` que conoce cómo inflar cada celda del listado y vincular los datos del producto con la vista.
3. El `RecyclerView` aplica un `LayoutManager` (por ejemplo, `LinearLayoutManager`) para definir la disposición de los elementos.
4. Cuando el usuario se desplaza, el `RecyclerView` recicla las vistas existentes y el adaptador actualiza el contenido con nuevos productos, evitando inflar vistas innecesariamente.

