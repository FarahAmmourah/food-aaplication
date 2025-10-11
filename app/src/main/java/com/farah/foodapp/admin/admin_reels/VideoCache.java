package com.farah.foodapp.admin.admin_reels;

import android.content.Context;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;

import java.io.File;

@UnstableApi
public class VideoCache {

    private static Cache cache;

    public static synchronized Cache getCache(Context context) {
        if (cache == null) {
            File cacheDir = new File(context.getCacheDir(), "video_cache");
            long cacheSize = 100 * 1024 * 1024;
            cache = new SimpleCache(cacheDir, new LeastRecentlyUsedCacheEvictor(cacheSize));
        }
        return cache;
    }

    public static CacheDataSource.Factory getCacheDataSourceFactory(Context context) {
        return new CacheDataSource.Factory()
                .setCache(getCache(context))
                .setUpstreamDataSourceFactory(
                        new DefaultHttpDataSource.Factory()
                )
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }
}