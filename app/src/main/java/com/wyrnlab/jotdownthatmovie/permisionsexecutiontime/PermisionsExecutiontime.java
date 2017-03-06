package com.wyrnlab.jotdownthatmovie.permisionsexecutiontime;

/**
 * Created by Jota on 06/03/2017.
 */

public interface PermisionsExecutiontime {

    public void getPermissions();
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}
