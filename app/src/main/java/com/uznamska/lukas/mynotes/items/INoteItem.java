/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.items;

import android.content.Context;
import android.net.Uri;

/**
 * Created by luaksz on 2016-02-22.
 */
public interface INoteItem {
    boolean isSimple();
    int getId();
    void setId(int id);
    void saveDb(Context context);
    void deleteFromDb(Context context);
}
