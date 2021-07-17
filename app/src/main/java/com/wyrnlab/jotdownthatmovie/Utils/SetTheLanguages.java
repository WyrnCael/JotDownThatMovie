package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.View.Activities.MainActivity;

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
}
