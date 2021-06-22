package com.vincent.filepicker.filter.entity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.MultipleRecycleAdapter;
import com.vincent.filepicker.R;
import com.vincent.filepicker.activity.BaseActivity;
import com.vincent.filepicker.adapter.OnSelectStateListener;

public class TypeSwitch {
    public String typeName;
    public MultipleRecycleAdapter<BaseFile> multipleRecycleAdapter;
    public TFragment fragment;

    public TypeSwitch(Activity activityCompat, String name, AdapterDelegate.OnItemClickListener<BaseFile> onItemClickListener) {
        this.typeName = name;
        multipleRecycleAdapter = MultipleRecycleAdapter.getByViewHolder(activityCompat, ViewHolderFile.class);
        fragment = new TFragment(multipleRecycleAdapter);
        multipleRecycleAdapter.setOnItemClickListener(onItemClickListener);
    }

    public void onLoadComplete() {
        fragment.onLoadComplete();
    }

    @SuppressLint("ValidFragment")
    public static class TFragment extends Fragment {
        MultipleRecycleAdapter<BaseFile> multipleRecycleAdapter;
        ProgressBar progress;
        View image;
        boolean isCom = false;

        public TFragment(MultipleRecycleAdapter<BaseFile> multipleRecycleAdapter) {
            this.multipleRecycleAdapter = multipleRecycleAdapter;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View inflate = inflater.inflate(R.layout.vw_frag_rey, container, false);
            progress = inflate.findViewById(R.id.progress);
            image = inflate.findViewById(R.id.image);
            RecyclerView recyclerView = inflate.findViewById(R.id.rv_image_pick);
            recyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
            recyclerView.setAdapter(multipleRecycleAdapter);
            if (isCom) {
                onLoadComplete();
            }
            return inflate;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        public void onLoadComplete() {
            isCom = true;
            if (progress != null) {
                progress.setVisibility(View.GONE);
            }
            if (multipleRecycleAdapter.getDataCount() == 0 && image != null) {
                image.setVisibility(View.VISIBLE);
            }
        }
    }


}
