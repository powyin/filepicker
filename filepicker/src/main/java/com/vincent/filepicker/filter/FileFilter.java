package com.vincent.filepicker.filter;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;

import com.vincent.filepicker.adapter.OnSelectStateListener;
import com.vincent.filepicker.filter.callback.FileLoaderCallbacks;
import com.vincent.filepicker.filter.callback.FilterResultCallback;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.BaseFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_AUDIO;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_FILE;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_IMAGE;
import static com.vincent.filepicker.filter.callback.FileLoaderCallbacks.TYPE_VIDEO;

/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 10:19
 */

public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback) {
        activity.getSupportLoaderManager().initLoader(0, null, new FileLoaderCallbacks(activity, callback, TYPE_IMAGE, null));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback) {
        activity.getSupportLoaderManager().initLoader(1, null, new FileLoaderCallbacks(activity, callback, TYPE_VIDEO, null));
    }

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback) {
        activity.getSupportLoaderManager().initLoader(2, null, new FileLoaderCallbacks(activity, callback, TYPE_AUDIO, null));
    }

    public static Observable<BaseFile> getFiles(final FragmentActivity activity, final FilterResultCallback<NormalFile> callback, final String[] suffix) {
        return Observable.create(new ObservableOnSubscribe<BaseFile>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseFile> emitter) throws Exception {
                activity.getSupportLoaderManager().initLoader(3, null, new FileLoaderCallbacks(activity, callback, TYPE_FILE, suffix, emitter));
            }
        });
    }
}
