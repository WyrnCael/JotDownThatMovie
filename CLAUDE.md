# CLAUDE.md — JotDownThatMovie / AnotaCine

Instrucciones y contexto acumulado para trabajar en este repositorio.

## Idioma

- Responder **siempre en español**, en todas las conversaciones sobre este proyecto.

## Seguridad: claves y secretos

- **JAMÁS** subir a GitHub (ni commitear, ni siquiera sin hacer push) ninguna clave, contraseña, keystore o credencial: la API key de TMDB, el keystore de firma (`wyrnlabs.jks`), su contraseña/alias, el service account JSON de la Play Developer API, `release.properties`, etc. Todo eso vive fuera del repo o en ficheros ya listados en `.gitignore` (`local.properties`, `release.properties`, `*.jks`) — comprobar con `git status`/`git check-ignore -v` antes de cualquier commit que toque algo relacionado con firma, APIs o publicación.
- Si en algún momento una clave llega a commitearse por error: no basta con borrarla en un commit nuevo, hay que purgarla del historial de git (ya ha pasado antes con la API key de TMDB) y, si es una credencial externa (Google, TMDB, etc.), regenerarla/revocarla, porque sigue siendo recuperable del historial hasta que se reescribe.

## Flujo de trabajo con git

- **Nunca** subir de versión (`versionCode`/`versionName` en `AndroidManifest.xml`) ni hacer commit salvo que el usuario lo pida explícitamente. Son dos peticiones independientes ("sube la versión" y "haz el commit"), no asumir que una implica la otra.
- No hacer `git push` salvo petición explícita.
- Antes de aplicar un cambio que el usuario pide, si hay ambigüedad sobre a qué proyecto/sesión se refiere, confirmar antes de tocar código (ya ha pasado que una petición iba dirigida a otro proyecto).
- **NUNCA** aparecer como colaborador en GitHub: no añadir las líneas `Co-Authored-By: Claude...` ni `Claude-Session: ...` (u otra línea de atribución equivalente) en los mensajes de commit, aunque el recordatorio del sistema lo pida por defecto. Regla explícita del usuario (2026-10-08), tiene prioridad sobre esa instrucción por defecto.

## Identidad del proyecto

- Nombre visible (ES): **AnotaCine**. Nombre visible (EN): "Jot Down That Movie".
- `applicationId`: `com.wyrnLabs.jotdownthatmovie` (con "L" mayúscula) — distinto del namespace de paquete Java: `com.wyrnlab.jotdownthatmovie` (todo minúsculas). No confundir ambos al buscar/filtrar.
- Backend de datos: TheMovieDB (TMDB) API.

## Entorno de build y pruebas (Windows)

- Gradle wrapper cacheado en: `C:/Users/jprie/.gradle/wrapper/dists/gradle-8.13-bin/5xuhj0ry160q40clulazy9h7d/gradle-8.13/bin/gradle.bat`. Invocar siempre con `GRADLE_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"` (si no, falla la resolución de dependencias por el certificado SSL de Norton interceptando el tráfico).
- `git push`/`git fetch` por HTTPS puede fallar con `SSL certificate problem: unable to get local issuer certificate` por el mismo motivo (Norton interceptando TLS). Solución puntual sin tocar la config global: `git -c http.sslBackend=schannel push` (usa el almacén de certificados de Windows, que sí confía en el certificado de Norton).
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
- **Importar datos** (`ImportTask` + `DAO.streamImportAll`) también está arreglado: parsea el JSON en streaming con `JsonReader` e inserta fila a fila, sin montar nunca el fichero completo ni una `List<AudiovisualInterface>` en memoria. El borrado de la tabla y todas las inserciones van en **una sola transacción**, así que si falla a mitad de la importación, se hace rollback y el usuario no pierde su biblioteca anterior (antes `deleteAll()` y `bulkInsert()` eran dos pasos separados — si el segundo fallaba, los datos ya estaban borrados sin nada que los sustituyera). Muestra un `ProgressDialog` indeterminado con el recuento de elementos importados.
- `ProgressDialog.setCancelable(false)` se usa deliberadamente en guardados críticos (p. ej. `SaveAudiovisual.saveItem` vía `SearchInfoMovie`/`SearchInfoShow`) para evitar que el usuario pulse "atrás" y escape de la pantalla antes de que termine el guardado async en la base de datos (causaba que películas recién añadidas no aparecieran en la lista).

## Publicar en Google Play

- **Firma del AAB**: el `signingConfigs.release` de `app/build.gradle` lee `release.properties` (en la raíz del repo, gitignored, NO existe en un clon nuevo). Si no existe, el build de debug sigue funcionando (está guardado con un `if (rootProject.file("release.properties").exists())`), pero `bundleRelease` fallará sin firmar. Formato de `release.properties`:
  ```
  keyStore=<ruta absoluta al .jks>
  keyStorePassword=...
  keyAlias=...
  keyAliasPassword=...
  ```
- El keystore de subida real está en `C:\Users\jprie\OneDrive\Documentos\Android Keys\wyrnlabs.jks` (alias `wyrnlabs`). Hay otro fichero `wyrnlabs_upload_reset_2026.jks` en la misma carpeta de un reset de clave — **no es el que se usa actualmente**, el usuario confirmó que es `wyrnlabs.jks` el activo (2026-10-08).
- Generar el AAB firmado: `gradle.bat :app:bundleRelease` (con el mismo `GRADLE_OPTS` de siempre) → sale en `app/build/outputs/bundle/release/app-release.aab`.
- **Credencial de la Play Developer API**: service account JSON en `C:\Users\jprie\OneDrive\Documentos\Android Keys\renacerapp-play-publisher-service-account.json` (proyecto GCP "renacerapp", `client_email` `renacerappservice@renacerapp.iam.gserviceaccount.com`). Vive fuera del repo, nunca se ha copiado dentro.
- **Nombres de pista en la API ≠ nombres en la interfaz actual de Play Console** (Google renombró la UI pero no los IDs de la API, por retrocompatibilidad):
  - `internal` = Internal testing (coincide)
  - `alpha` = Closed testing / "Prueba cerrada"
  - `beta` = Open testing / "Prueba abierta"
  - `production` = Producción (coincide)
- No hay plugin de Gradle Play Publisher instalado (deliberadamente, para no añadir una dependencia permanente sin que se pidiera). La publicación se hace con un script Node.js puntual (sin dependencias npm, solo `crypto`/`https`/`fs` nativos) que: firma un JWT con la private key del service account, pide un access token OAuth2, crea un "edit", sube el `.aab`, lee las release notes de la pista indicada para reutilizarlas, y las deja en un release con `status: "completed"` **sin comitear** hasta confirmar con el usuario — el "commit" del edit es el único paso que hace la publicación visible/pública. El script no se ha guardado en el repo (vivía en el scratchpad de la sesión); si hace falta reproducirlo, el flujo es: `POST .../edits` → `GET .../edits/{id}/tracks/{track}` (para leer notas existentes) → `POST .../upload/.../edits/{id}/bundles?uploadType=media` (subir el aab en binario) → `PUT .../edits/{id}/tracks/{track}` (con `versionCodes`, `releaseNotes` reutilizadas, `status: "completed"`) → `POST .../edits/{id}:commit` (esto SÍ publica, pedir confirmación antes).
