package com.hot.sentefinder.utilities;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by Jamie on 5/5/2017.
 */

public class URLSpanEdit extends URLSpan {
    public URLSpanEdit(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
