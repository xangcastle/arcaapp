package com.twine.arca_app.general;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;

/**
 * Created by TWINE-DELL on 28/3/2017.
 */

public class myMapView extends MapView {

    public myMapView(Context context) {
        super(context);
    }
    public myMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                System.out.println("unlocked");
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
                System.out.println("locked");
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}

