# CLAUDE.md — JotDownThatMovie / AnotaCine

Instrucciones y contexto acumulado para trabajar en este repositorio.

## Idioma

- Responder **siempre en español**, en todas las conversaciones sobre este proyecto.

## Flujo de trabajo con git

- **Nunca** subir de versión (`versionCode`/`versionName` en `AndroidManifest.xml`) ni hacer commit salvo que el usuario lo pida explícitamente. Son dos peticiones independientes ("sube la versión" y "haz el commit"), no asumir que una implica la otra.
- No hacer `git push` salvo petición explícita.
- Antes de aplicar un cambio que el usuario pide, si hay ambigüedad sobre a qué proyecto/sesión se refiere, confirmar antes de tocar código (ya ha pasado que una petición iba dirigida a otro proyecto).

## Identidad del proyecto

- Nombre visible (ES): **AnotaCine**. Nombre visible (EN): "Jot Down That Movie".
- `applicationId`: `com.wyrnLabs.jotdownthatmovie` (con "L" mayúscula) — distinto del namespace de paquete Java: `com.wyrnlab.jotdownthatmovie` (todo minúsculas). No confundir ambos al buscar/filtrar.
- Backend de datos: TheMovieDB (TMDB) API.

## Entorno de build y pruebas (Windows)

- Gradle wrapper cacheado en: `C:/Users/jprie/.gradle/wrapper/dists/gradle-8.13-bin/5xuhj0ry160q40clulazy9h7d/gradle-8.13/bin/gradle.bat`. Invocar siempre con `GRADLE_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"` (si no, falla la resolución de dependencias por el certificado SSL de Norton interceptando el tráfico).
- Emulador usado: AVD `OnePlus_8T_API_36` (API 36 / Android 16). No es rooteable (`adb root` falla con "Device must be bootloader unlocked"), así que para tocar datos de la app hay que usar `run-as <paquete>` en vez de acceso root directo.
- Heap por defecto de ese emulador (sin `android:largeHeap`): `dalvik.vm.heapgrowthlimit=192m` (límite real que aplica), `heapsize=576m`. Relevante para cualquier bug de memoria con datasets grandes.
- En Git Bash, **cualquier** comando `adb` que toque una ruta del dispositivo (`/sdcard/...`, `/data/local/tmp/...`) necesita el prefijo `MSYS_NO_PATHCONV=1`, si no MSYS reescribe la ruta como si fuera de Windows y falla.
- Para coordenadas de tap exactas en pruebas de UI por adb: usar `uiautomator dump` + buscar `bounds="[...]"` del elemento. Adivinar coordenadas a ojo falla a menudo, sobre todo si cambia la resolución/densidad del emulador (ya ha pasado sin aviso aparente).
- Para reproducir bugs de volumen de datos: se puede sembrar la base de datos SQLite de la app directamente sin pasar por la UI — generar un `.sql` con `INSERT`s (bash + `head -c N /dev/zero | tr '\0' 'A'` para rellenos grandes), `adb push` a `/data/local/tmp/`, y ejecutarlo con `adb shell run-as <paquete> sqlite3 databases/DBPeliculas '.read /data/local/tmp/seed.sql'`. Mucho más rápido y fiable que insertar registros uno a uno desde la app.

## Patrones y riesgos conocidos del código

- **Patrón de bug recurrente**: el parseo manual de JSON con la librería interna `ExternalLibraries/json` (`JsonObject.readFrom(...)`) no comprueba null antes de encadenar `.get("campo").asArray()`/`.asObject()`. Cuando TMDB no incluye ese campo (p. ej. `/images` sin `"posters"`), esto lanza `NullPointerException` **no capturada** por los `catch (IOException e) {}` de los `AsyncTask`, y crashea la app. Ya corregido en `SearchInfoMovie`, `SearchInfoShow`, `GetSimilarMovies` y `GetSimilarTVShows` (comprobando `info != null && info.get("posters") != null` antes de usarlo). Si aparece un crash similar en otra pantalla que consuma el endpoint `/images`, es probable que sea el mismo patrón sin corregir ahí.
- **Los `AsyncTask` de este proyecto solo capturan `Exception`, nunca `Throwable`/`Error`**. Un `OutOfMemoryError` (p. ej. al construir JSON gigantes en memoria) no lo capturan esos `catch`, y crashea la app en vez de mostrar un mensaje de error controlado. Tenerlo en cuenta en cualquier operación que maneje colecciones grandes o blobs de imagen.
- **`DAO.readAll()` no tiene límite de memoria**: carga todas las filas de la tabla `Peliculas` (incluyendo el blob de la portada de cada una) en una `List` en memoria de golpe. Con colecciones muy grandes (varios miles de registros con póster), esto puede hacer crashear el arranque mismo de `MainActivity` (`SQLiteException: Native could not create new byte[]` o `OutOfMemoryError`), **antes incluso** de llegar a ninguna pantalla. Confirmado en pruebas con ~3000 registros de prueba. Esto es un problema más amplio que el de exportar — pendiente de decidir si se aborda (requeriría paginar la carga de la lista principal, no solo el export).
- **Exportar datos** (`ExportImportHelper` + `DAO.streamExportAll`) ya está arreglado para streamear fila a fila directamente a disco vía `JsonWriter`, sin montar el payload completo en memoria. Antes crasheaba con `OutOfMemoryError` incluso con datasets moderados (~800 registros con pósters de 40KB ya era suficiente).
- **Importar datos** (`ImportTask` en `ExportImportHelper`) sigue leyendo el fichero completo a un `StringBuilder` y parseándolo entero con Gson antes de insertar — tiene el mismo riesgo teórico de `OutOfMemoryError` con ficheros de backup muy grandes que el export tenía antes de arreglarlo. No se ha tocado todavía porque no se ha pedido ni reportado como problema.
- `ProgressDialog.setCancelable(false)` se usa deliberadamente en guardados críticos (p. ej. `SaveAudiovisual.saveItem` vía `SearchInfoMovie`/`SearchInfoShow`) para evitar que el usuario pulse "atrás" y escape de la pantalla antes de que termine el guardado async en la base de datos (causaba que películas recién añadidas no aparecieran en la lista).
