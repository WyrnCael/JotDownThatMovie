package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TranslationModel;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.View.Activities.MainActivity;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Jota on 06/03/2017.
 */

public class SetTheLanguages {
    private static String language = Locale.getDefault().getDisplayLanguage();
    private static String country = Locale.getDefault().getCountry();

    public static String getLanguage(){
        return General.SearchLanguage;
    }

    public static String getMobileLanguage(){
        if(language.compareTo("español") == 0){
            return "es";
        } else {
            return "en";
        }
    }

    public static String getMobileActualLanguage(Activity activity){
        return activity.getResources().getConfiguration().locale.getLanguage();
    }

    public static String getMobileSearchLanguage(){
        if(language.compareTo("español") == 0){
            if(country.equalsIgnoreCase("MX")){
                return "es-MX";
            } else {
                return "es-ES";
            }
        } else {
            return "en-US";
        }
    }

    public static Integer getAppLangagePosition(Activity activity){
        if(General.AppLanguage == null) {
            return General.appLanguagesArray.indexOf(getMobileActualLanguage(activity));
        } else {
            return General.appLanguagesArray.indexOf(General.AppLanguage);
        }
    }

    public static Integer getSearchLangagePosition(){
        if(General.SearchLanguage == null) {
            return General.searchLanguagesArray.indexOf(getMobileSearchLanguage());
        } else {
            return General.searchLanguagesArray.indexOf(General.SearchLanguage);
        }
    }

    public static void setLocale(Activity activity, String languageCode, Boolean restartActivity) {

        if(!getMobileActualLanguage(activity).equalsIgnoreCase(General.AppLanguage)) {
            Locale myLocale = new Locale(languageCode);
            Resources res = activity.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            if (restartActivity) {
                Intent refresh = new Intent(activity, MainActivity.class);
                activity.finish();
                activity.startActivity(refresh);
            }
        }
    }

    /*private static String getSavedAppLanguage(){

    }

    private static String getSavedSearchLanguage(){

    }*/

    public static Boolean isEmptyLangageSettings(Activity context){
        SharedPreferences settings = context.getSharedPreferences(General.LANGUAGE_SETTINGS, 0);
        General.AppLanguage = settings.getString(General.APP_LANGUAGE_SETTINGS, null);
        General.SearchLanguage = settings.getString(General.SEARCH_LANGUAGE_SETTINGS, null);
        if(General.AppLanguage == null || General.SearchLanguage == null){
            return true;
        } else {
            setLocale(context, General.AppLanguage, false);
            return false;
        }
    }

    public static void savePreferrences(Activity context){
        SharedPreferences settings = context.getSharedPreferences(General.LANGUAGE_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(General.APP_LANGUAGE_SETTINGS, General.AppLanguage);
        editor.putString(General.SEARCH_LANGUAGE_SETTINGS, General.SearchLanguage);
        editor.commit();

        setLocale(context, General.AppLanguage, true);
    }

    private static void parseYAML(Context context){
        Yaml yaml = new Yaml(new Constructor(TranslationModel.class));
        TranslationModel items = (TranslationModel) yaml.load(context.getResources().openRawResource(R.raw.es_es));
        Map<String, Object> itemMap = items.es_ES;
        General.jobTranslations = (Map<String, Object>) itemMap.get("jobs");
        General.departamentsTranslations = (Map<String, Object>) itemMap.get("departments");
    }

    public static String getJobTranslation(Context context, String textToTranslate){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            if (General.jobTranslations == null) {
                parseYAML(context);
            }

            return (String) (General.jobTranslations.containsKey(textToTranslate) ? General.jobTranslations.get(textToTranslate) : textToTranslate);
        } else {
            return textToTranslate;
        }
    }

    public static String getDepartamentsTranslation(Context context, String textToTranslate){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            if (General.departamentsTranslations == null) {
                parseYAML(context);
            }

            return (String) (General.departamentsTranslations.containsKey(textToTranslate) ? General.departamentsTranslations.get(textToTranslate) : textToTranslate);
        } else {
            return textToTranslate;
        }
    }

    public static Bitmap getImageStub(Context context){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.stub_spanish);
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.stub_english);
        }
    }

    public static Integer getImageStubResourceId(){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            return R.drawable.stub_spanish;
        } else {
            return R.drawable.stub_english;
        }
    }

    public static Bitmap getPersonImageStub(Context context){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.stub_person_spanish);
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.stub_person_english);
        }
    }

    public static Integer getPersonImageStubResourceId(){
        if(General.SearchLanguage.substring(0,2).equalsIgnoreCase("es")) {
            return R.drawable.stub_person_spanish;
        } else {
            return R.drawable.stub_person_english;
        }
    }


}
