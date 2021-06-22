package com.vincent.filepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.MultipleListAdapter;
import com.powyin.scroll.adapter.PowViewHolder;
import com.powyin.slide.widget.OnItemClickListener;
import com.powyin.slide.widget.SlideSwitch;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.R;
import com.vincent.filepicker.filter.FileFilter;
import com.vincent.filepicker.filter.entity.BaseFile;
import com.vincent.filepicker.filter.entity.ViewHolderSufName;
import com.vincent.filepicker.filter.entity.TypeSwitch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Vincent Woo
 * Date: 2016/10/26
 * Time: 10:14
 */

public class NormalFilePickActivity extends BaseActivity {
    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final String SUFFIX = "Suffix";
    private int mMaxNumber;
    private String[] mSuffix;

    SlideSwitch slideSwitch;
    ViewPager viewPager;
    TextView tv_count;
    ArrayList<BaseFile> baseFiles = new ArrayList<>();
    List<TypeSwitch> typeSwitchList = new ArrayList<>();
    MultipleListAdapter<TypeSwitch> typeSwitch;

    AdapterDelegate.OnItemClickListener<BaseFile> onItemClickListener = new AdapterDelegate.OnItemClickListener<BaseFile>() {
        @Override
        public void onClick(PowViewHolder<BaseFile> holder, BaseFile data, int index, int resId) {
            View viewById = holder.mItemView.findViewById(R.id.cbx);
            if (data.isSelected()) {
                data.setSelected(false);
                viewById.setSelected(false);
                tv_count.setText(getSelect().size() + "/" + mMaxNumber);
            } else {
                if (getSelect().size() >= mMaxNumber) {
                    Toast.makeText(NormalFilePickActivity.this, "文件选择超过最大限制: " + mMaxNumber, Toast.LENGTH_SHORT).show();
                } else {
                    data.setSelected(true);
                    viewById.setSelected(true);
                    tv_count.setText(getSelect().size() + "/" + mMaxNumber);
                }
            }
        }
    };

    @Override
    void permissionGranted() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_file_pick_pow);
        tv_count = findViewById(R.id.tv_count);
        slideSwitch = findViewById(R.id.slide_switch);
        viewPager = findViewById(R.id.view_page);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        mMaxNumber = Math.max(1, mMaxNumber);
        mMaxNumber = Math.min(DEFAULT_MAX_NUMBER, mMaxNumber);


        mSuffix = getIntent().getStringArrayExtra(SUFFIX);
        if (mSuffix == null || mSuffix.length == 0) {
            mSuffix = new String[]{"zip", "xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"};
        }

        for (String k : mSuffix) {
            TypeSwitch typeSwitch = new TypeSwitch(this, k, onItemClickListener);
            typeSwitchList.add(typeSwitch);
        }
        typeSwitch = MultipleListAdapter.getByViewHolder(this, ViewHolderSufName.class);
        typeSwitch.addDataAtLast(typeSwitchList);

        initView();
        loadData();
    }

    @Override
    public void onBackClick(View view) {
        for (BaseFile baseFile : baseFiles) {
            baseFile.setSelected(false);
        }
        super.onBackClick(view);

    }


    @Override
    public void onBackPressed() {
        for (BaseFile baseFile : baseFiles) {
            baseFile.setSelected(false);
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constant.RESULT_PICK_FILE, getSelect());
        setResult(RESULT_OK, intent);
        super.finish();
    }


    private void initView() {
        tv_count.setText(getSelect().size() + "/" + mMaxNumber);

        if (mSuffix.length <= 6) {
            try {
                Field mSelectMaxItem = SlideSwitch.class.getDeclaredField("mSelectMaxItem");
                mSelectMaxItem.setAccessible(true);
                mSelectMaxItem.set(slideSwitch, mSuffix.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return typeSwitchList.get(position).fragment;
            }

            @Override
            public int getCount() {
                return typeSwitchList.size();
            }
        });
        slideSwitch.setAdapter(typeSwitch);

        viewPager.addOnPageChangeListener(slideSwitch.getSupportOnPageChangeListener());
        slideSwitch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                viewPager.setCurrentItem(position);
            }
        });

        findViewById(R.id.rl_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadData() {
        FileFilter.getFiles(this, null, mSuffix).buffer(10)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<BaseFile>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Looper.getMainLooper();
            }

            @Override
            public void onNext(List<BaseFile> baseFiles) {
                for (BaseFile baseFile : baseFiles) {
                    String toLowerCase = getFileNameFromUrl(baseFile.getPath())[1];
                    for (TypeSwitch typeSwitch : typeSwitchList) {
                        if (typeSwitch.typeName.equalsIgnoreCase(toLowerCase)) {
                            typeSwitch.multipleRecycleAdapter.addData(typeSwitch.multipleRecycleAdapter.getDataCount(), baseFile);
                        }
                    }
                }
                NormalFilePickActivity.this.baseFiles.addAll(baseFiles);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                for (TypeSwitch typeSwitch : typeSwitchList) {
                    typeSwitch.onLoadComplete();
                }
            }
        });
    }


    private ArrayList<BaseFile> getSelect() {
        ArrayList<BaseFile> ret = new ArrayList<>();
        for (BaseFile baseFile : baseFiles) {
            if (baseFile.isSelected()) {
                ret.add(baseFile);
            }
        }
        return ret;
    }


    /**
     * 提取url中 前缀名 后缀名 完整名
     *
     * @return https://github.com/apache/incubator-weex.txt#cc=23   return   ["incubator-weex","txt","incubator-weex.txt"]
     */
    public static String[] getFileNameFromUrl(String url) {
        String[] preName_SuffixName_FullName = new String[3];
        preName_SuffixName_FullName[0] = "";
        preName_SuffixName_FullName[1] = "";
        preName_SuffixName_FullName[2] = "";
        if (url != null && url.length() > 0) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }
            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }
            int filenamePos = url.lastIndexOf('/');
            String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;
            preName_SuffixName_FullName[2] = filename;
            int dotPos = filename.lastIndexOf('.');
            if (dotPos >= 0) {
                preName_SuffixName_FullName[0] = filename.substring(0, dotPos);
                preName_SuffixName_FullName[1] = filename.substring(dotPos + 1);
            } else {
                preName_SuffixName_FullName[0] = filename;
            }
        }
        return preName_SuffixName_FullName;
    }

}
