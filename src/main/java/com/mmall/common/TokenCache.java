package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by YangYang on 2018/4/5.
 */
@Slf4j
public class TokenCache {
//    private static Logger  logger = LoggerFactory.getLogger(TokenCache.class);

    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().
            initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        @Override
        //默认的加载实现，当调用
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static  void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return  value;
        }catch (Exception e){
//            logger.error("localCache get error ",e);
            log.error("localCache get error ",e);
        }
        return null;
    }
}
