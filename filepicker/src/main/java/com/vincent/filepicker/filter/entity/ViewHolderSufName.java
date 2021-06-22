package com.vincent.filepicker.filter.entity;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.vincent.filepicker.R;

public class ViewHolderSufName extends PowViewHolder<TypeSwitch> {
    TextView textView;
    public ViewHolderSufName(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);


        textView = findViewById(R.id.tv_folder_title);
    }


    @Override
    protected int getItemViewRes() {
        return R.layout.vw_layout_pow_list_type;
    }

    @Override
    public void loadData(AdapterDelegate<? super TypeSwitch> multipleAdapter, TypeSwitch data, int position) {
        textView.setText(data.typeName);
    }
}
