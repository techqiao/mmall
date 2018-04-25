package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * <p>Description : Redis 工具类
 * <p>Date : 2018-04-20 15:01
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Slf4j
public class RedisPoolUtil {
    /**
     * 重新设置key的有效期，单位是秒
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            //从连接池拿一个实例Jedis
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            //把 坏的jedis 放回连接池
            RedisPool.returnBrokenResource(jedis);
            return result;//null
        }
        //把 正常的 jedis 放回连接池
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;//null
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置key value 有效期
     *
     * @param key
     * @param value
     * @param exTime
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();

        RedisPoolUtil.set("keyTest", "value");

        String value = RedisPoolUtil.get("keyTest");

        RedisPoolUtil.setEx("keyex", "valueex", 60 * 10);

        RedisPoolUtil.expire("keyTest", 60 * 20);

        RedisPoolUtil.del("keyTest");


        String aaa = RedisPoolUtil.get(null);
        System.out.println(aaa);

        System.out.println("end");


    }

    public static boolean addListToJedis(String key, List<String> list, int cacheSeconds, String methodName) {
        if (list != null && list.size() > 0) {
            Jedis jedis = null;
            boolean isBroken = false;
            try {
                jedis = RedisPool.getJedis();
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                for (String aList : list) {
                    jedis.rpush(key, aList);
                }
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } catch (Exception e) {
                isBroken = true;
                log.error("addList key:{} value:{} error:{}", key, list, e);
                RedisPool.returnBrokenResource(jedis);
            }
            return !isBroken;
        }
        return true;
    }

    public static boolean addToSetJedis(String key, String[] value, String methodName) {
        Jedis jedis;
        boolean isBroken = false;
        try {
            jedis = RedisPool.getJedis();
            jedis.sadd(key, value);
        } catch (Exception e) {
            isBroken = true;
            log.error("addSet redis failed{}", e);
        }
        return !isBroken;
    }

    public static boolean addHashMapToJedis(String key, Map<String, String> map, int cacheSeconds) {
        boolean isBroken = false;
        Jedis jedis;
        if (map != null && map.size() > 0) {
            try {
                jedis = RedisPool.getJedis();
                jedis.hmset(key, map);
                if (cacheSeconds > 0)
                    jedis.expire(key, cacheSeconds);
            } catch (Exception e) {
                isBroken = true;
                log.error("redis failed{}", e);
            }
        }
        return !isBroken;
    }

    public static long getLength(String key) {
        Jedis jedis;
        long result = 0;
        try {
            jedis = RedisPool.getJedis();
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("redis failed{}", e);
        }
        return result;
    }

    public boolean existKey(String key, String methodName) {
        Jedis jedis;
        boolean isBroken = false;
        try {
            jedis = RedisPool.getJedis();
            return jedis.exists(key);
        } catch (Exception e) {
            isBroken = true;
            log.error("redis failed{}", e);
        }
        return !isBroken;
    }

}
