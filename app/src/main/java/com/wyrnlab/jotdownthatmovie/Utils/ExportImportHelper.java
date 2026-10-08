package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.content.FileProvider;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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


    private static class ExportTask extends AsyncTask<Void, Void, File> {
        private final Activity activity;

        ExportTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected File doInBackground(Void... voids) {
            // Stream straight to disk, one record at a time, instead of building the whole
            // payload (and its base64-inflated JSON) as Strings in memory: with thousands of
            // saved items - each carrying a poster image blob - that used to OutOfMemoryError
            // and crash, since OOM is an Error, not an Exception, so it skipped the catch below.
            File file = null;
            try {
                File exportDir = new File(activity.getCacheDir(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                String fileName = "AnotaCine_backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".json";
                file = new File(exportDir, fileName);

                JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(file)));
                try {
                    writer.beginObject();
                    writer.name("version").value(PAYLOAD_VERSION);
                    writer.name("exportedAt").value(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date()));
                    writer.name("items");
                    writer.beginArray();
                    DAO.getInstance().streamExportAll(activity, writer);
                    writer.endArray();
                    writer.endObject();
                } finally {
                    writer.close();
                }

                return file;
            } catch (Exception | OutOfMemoryError e) {
                if (file != null) {
                    file.delete();
                }
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

    private static class ImportTask extends AsyncTask<Void, Integer, Boolean> {
        private final Activity activity;
        private final Uri fileUri;
        private final Runnable onComplete;
        private ProgressDialog pDialog;

        ImportTask(Activity activity, Uri fileUri, Runnable onComplete) {
            this.activity = activity;
            this.fileUri = fileUri;
            this.onComplete = onComplete;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(activity.getResources().getString(R.string.ImportingProgress, 0));
            // Not cancelable: this replaces the whole library inside a single DB transaction,
            // so letting the user bail out mid-stream would still finish or roll back on its
            // own - escaping the dialog wouldn't stop it, only hide its progress.
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Parses the backup with a streaming JsonReader and inserts straight into the DB
            // via DAO.streamImportAll, instead of reading the whole file into a String and
            // building a full List<AudiovisualInterface> in memory first (which used to
            // OutOfMemoryError and crash with large backups, just like export did before).
            try (InputStream is = activity.getContentResolver().openInputStream(fileUri)) {
                if (is == null) {
                    return false;
                }
                JsonReader reader = new JsonReader(new InputStreamReader(is));
                try {
                    reader.beginObject();
                    boolean foundItems = false;
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if ("items".equals(name)) {
                            foundItems = true;
                            DAO.getInstance().streamImportAll(activity, reader, this::publishProgress);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    return foundItems;
                } finally {
                    reader.close();
                }
            } catch (Exception | OutOfMemoryError e) {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.setMessage(activity.getResources().getString(R.string.ImportingProgress, values[0]));
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (pDialog != null && pDialog.isShowing() && !activity.isFinishing()) {
                pDialog.dismiss();
            }
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
