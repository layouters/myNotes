package com.uznamska.lukas.mynotes.items;

import android.view.View;

/**
 * Created by Anna on 2016-02-28.
 */
public interface IBindActionTaker {
    void onBindViewHolder(final int position);
    void setViewClickListener(View.OnClickListener listener);
}
