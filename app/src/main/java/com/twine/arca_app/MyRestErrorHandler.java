package com.twine.arca_app;

import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */
@EBean
public class MyRestErrorHandler implements RestErrorHandler {
    String TAG="MyRestErrorHandler";
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        // Do whatever you want here.
        Log.i(TAG,e.getMessage() );
    }
}
