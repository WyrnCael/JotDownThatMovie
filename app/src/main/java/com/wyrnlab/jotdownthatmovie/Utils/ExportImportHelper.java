package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.Export.ExportPayload;
import com.wyrnlab.jotdownthatmovie.Model.Export.ExportedItem;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportImportHelper {

    public static final int REQUEST_CODE_IMPORT_FILE = 501;
    private static final int PAYLOAD_VERSION = 1;

    public static void exportData(Activity activity) {
        MyUtils.execute(new ExportTask(activity));
    }

    public static void pickImportFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(intent, REQUEST_CODE_IMPORT_FILE);
    }

    public static void confirmAndImport(Activity activity, Uri fileUri, Runnable onImportComplete) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.ImportWarningTitle)
                .setMessage(R.string.ImportWarningMessage)
                .setPositiveButton(R.string.MenuImport, (dialog, which) ->
                        MyUtils.execute(new ImportTask(activity, fileUri, onImportComplete)))
                .setNegativeButton(R.string.Cancel, null)
                .show();
    }

    private static List<AudiovisualInterface> collectAllItems(android.content.Context context) {
        Map<String, List<AudiovisualInterface>> byType = DAO.getInstance().readAll(context);
        List<AudiovisualInterface> all = new ArrayList<>();
        all.addAll(byType.get(General.ALL_TYPE));
        all.addAll(byType.get(General.VIEWED));
        return all;
    }

    private static ExportedItem toExportedItem(AudiovisualInterface movie) {
        ExportedItem item = new ExportedItem();
        item.filmId = movie.getId();
        item.titulo = movie.getTitulo();
        item.tituloOriginal = movie.getTituloOriginal();
        item.anyo = movie.getAnyo();
        item.descripcion = movie.getDescripcion();
        item.imageBase64 = movie.getImage() != null ? Base64.encodeToString(movie.getImage(), Base64.NO_WRAP) : null;
        item.directores = movie.getDirectoresToString();
        item.generos = movie.getGenerosToStrig();
        item.rating = String.valueOf(movie.getRating() == null ? 0.0 : movie.getRating());
        item.tipo = movie.getTipo();
        item.temporadas = movie.getSeasons();
        item.originalLanguage = movie.getOriginalLanguage();
        item.viewed = movie.getViewed();
        return item;
    }

    private static AudiovisualInterface fromExportedItem(ExportedItem item) {
        Pelicula movie = new Pelicula();
        movie.setId(item.filmId);
        movie.setTitulo(item.titulo);
        movie.setTituloOriginal(item.tituloOriginal);
        movie.setAnyo(item.anyo);
        movie.setDescripcion(item.descripcion);
        if (item.imageBase64 != null && !item.imageBase64.isEmpty()) {
            movie.setImage(Base64.decode(item.imageBase64, Base64.NO_WRAP));
        }
        addCommaSeparated(movie, item.directores, true);
        addCommaSeparated(movie, item.generos, false);
        movie.setRating(parseDoubleSafe(item.rating));
        movie.setTipo(item.tipo);
        movie.setSeasons(item.temporadas);
        movie.setOriginalLanguage(item.originalLanguage);
        movie.setViewed(item.viewed);
        return movie;
    }

    private static void addCommaSeparated(Pelicula movie, String value, boolean isDirectores) {
        if (value == null || value.isEmpty()) {
            return;
        }
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (isDirectores) {
                movie.addDirectores(trimmed);
            } else {
                movie.addGeneros(trimmed);
            }
        }
    }

    private static double parseDoubleSafe(String value) {
        try {
            return value == null ? 0.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static class ExportTask extends AsyncTask<Void, Void, File> {
        private final Activity activity;

        ExportTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected File doInBackground(Void... voids) {
            try {
                List<AudiovisualInterface> all = collectAllItems(activity);

                ExportPayload payload = new ExportPayload();
                payload.version = PAYLOAD_VERSION;
                payload.exportedAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());
                payload.items = new ArrayList<>();
                for (AudiovisualInterface movie : all) {
                    payload.items.add(toExportedItem(movie));
                }

                String json = new Gson().toJson(payload);

                File exportDir = new File(activity.getCacheDir(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                String fileName = "AnotaCine_backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".json";
                File file = new File(exportDir, fileName);

                FileWriter writer = new FileWriter(file);
                writer.write(json);
                writer.close();

                return file;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (activity.isFinishing()) {
                return;
            }
            if (file == null) {
                MyUtils.showSnacknar(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.ExportError));
                return;
            }

            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/json");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.MenuExport)));
        }
    }

    private static class ImportTask extends AsyncTask<Void, Void, Boolean> {
        private final Activity activity;
        private final Uri fileUri;
        private final Runnable onComplete;

        ImportTask(Activity activity, Uri fileUri, Runnable onComplete) {
            this.activity = activity;
            this.fileUri = fileUri;
            this.onComplete = onComplete;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                StringBuilder sb = new StringBuilder();
                InputStream is = activity.getContentResolver().openInputStream(fileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();

                ExportPayload payload = new Gson().fromJson(sb.toString(), ExportPayload.class);
                if (payload == null || payload.items == null) {
                    return false;
                }

                List<AudiovisualInterface> items = new ArrayList<>();
                for (ExportedItem exportedItem : payload.items) {
                    items.add(fromExportedItem(exportedItem));
                }

                DAO.getInstance().deleteAll(activity);
                DAO.getInstance().bulkInsert(activity, items);

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (activity.isFinishing()) {
                return;
            }
            if (success) {
                MyUtils.showSnacknar(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.ImportSuccess));
                if (onComplete != null) {
                    onComplete.run();
                }
            } else {
                MyUtils.showSnacknar(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.ImportError));
            }
        }
    }
}
