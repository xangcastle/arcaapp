package com.twine.arca_app.general;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.activeandroid.query.Select;

/**
 * Created by Jose Williams Garcia on 30/4/2017.
 */

public class SessionManager{
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public SessionManager(Context paramContext)
    {
        this._context = paramContext;
        this.pref = this._context.getSharedPreferences("AndroidGreeve", this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void saveSharedValue(String Key, String Value)
    {
        this.editor.putString(Key, Value);
        this.editor.commit();
    }
    public String getSharedValue(String Key)
    {
        try {
            return this.pref.getString(Key, null);
        }
        catch (NumberFormatException ex){
            return null;
        }
    }
}
