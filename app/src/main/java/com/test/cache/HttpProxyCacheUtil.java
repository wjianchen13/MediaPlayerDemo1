package com.test.cache;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

public class HttpProxyCacheUtil {

    private static HttpProxyCacheServer audioProxy;

    public static HttpProxyCacheServer getAudioProxy() {
        return null;
    }

    public static HttpProxyCacheServer getAudioProxy(Context context) {
        if (audioProxy== null) {
            audioProxy= new HttpProxyCacheServer.Builder(context)
//                    .cacheDirectory(CachesUtil.getMediaCacheFile(CachesUtil.AUDIO))
//                    .cacheDirectory(StorageUtils.getIndividualCacheDirectory(context))
                    .maxCacheSize(1024 * 1024 * 1024) // 缓存大小
                    .fileNameGenerator(new CacheFileNameGenerator())
                    .build();
        }
//        audioProxy.isCached()
        return audioProxy;
    }
}