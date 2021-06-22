package com.vincent.filepicker.filter.entity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.vincent.filepicker.R;
import com.vincent.filepicker.ToastUtil;
import com.vincent.filepicker.Util;

public class ViewHolderFile extends PowViewHolder<BaseFile> {

    public ViewHolderFile(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        mIvIcon = (ImageView) findViewById(R.id.ic_file);
        mTvTitle = (TextView) findViewById(R.id.tv_file_title);
        mCbx = (ImageView) findViewById(R.id.cbx);
        registerItemClick(R.id.cbx);
    }

    private ImageView mIvIcon;
    private TextView mTvTitle;
    private ImageView mCbx;


    @Override
    protected int getItemViewRes() {
        return R.layout.vw_layout_item_normal_file_pick;
    }

    @Override
    public void loadData(AdapterDelegate<? super BaseFile> multipleAdapter, BaseFile data, int position) {
//        textView.setText(data.typeName);
        final NormalFile file = (NormalFile) data;

        mTvTitle.setText(Util.extractFileNameWithSuffix(file.getPath()));
        mTvTitle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (mTvTitle.getMeasuredWidth() >
                Util.getScreenWidth(mActivity) - Util.dip2px(mActivity, 10 + 32 + 10 + 48 + 10 * 2)) {
            mTvTitle.setLines(2);
        } else {
            mTvTitle.setLines(1);
        }

        if (file.isSelected()) {
            mCbx.setSelected(true);
        } else {
            mCbx.setSelected(false);
        }

        if (file.getPath().endsWith("xls") || file.getPath().endsWith("xlsx")) {
            mIvIcon.setImageResource(R.drawable.vw_ic_excel);
        } else if (file.getPath().endsWith("doc") || file.getPath().endsWith("docx")) {
            mIvIcon.setImageResource(R.drawable.vw_ic_word);
        } else if (file.getPath().endsWith("ppt") || file.getPath().endsWith("pptx")) {
            mIvIcon.setImageResource(R.drawable.vw_ic_ppt);
        } else if (file.getPath().endsWith("pdf")) {
            mIvIcon.setImageResource(R.drawable.vw_ic_pdf);
        } else if (file.getPath().endsWith("txt")) {
            mIvIcon.setImageResource(R.drawable.vw_ic_txt);
        } else {
            mIvIcon.setImageResource(R.drawable.vw_ic_file);
        }
    }
}
