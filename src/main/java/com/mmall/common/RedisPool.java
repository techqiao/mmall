package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>Description : 连接池
 * <p>Date : 2018-04-20 13:48
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class RedisPool {
    //jedis连接池
    private static JedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    //在jedispool中最大的idle状态(空闲状态)的jedis实例的个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));
    //在jedispool中最小的idle状态(空闲状态)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));
    //在borrow(拿,借)一个jedis实例的时候,是否要进行验证操作,如果赋值true,则得到的jedis实例肯定是可以用的.
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    //在return(还)一个jedis实例的时候,是否要进行验证操作,如果赋值true,则放回jedispool的jedis实例肯定是可以用的.
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "false"));
    //redis ip
    private static String redisIp = PropertiesUtil.getProperty("redis1.ip");
    //redis port
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));


    private static void initPool() {
        //Jedis 连接处配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽的时候,是否阻塞,false会抛出异常,true阻塞直到超时(jedis设置连接超时时间timeout),默认为true.
        config.setBlockWhenExhausted(true);
        //初始化jedis连接池 超时时间 2秒
        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }

    //加载到jvm 初始化连接池
    static {
        initPool();
    }

    //从连接池拿一个实例Jedis
    public static Jedis getJedis() {
        return pool.getResource();
    }

    //把 坏的jedis 放回连接池
    public static void returnBrokenResource(Jedis jedis) {
        //源码里面已经判断 jedis != null
        pool.returnBrokenResource(jedis);
    }

    //把 jedis 放回连接池
    public static void returnResource(Jedis jedis) {
        //源码里面已经判断 jedis != null
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.setex("key",1000*10,"value");
        returnResource(jedis);

        //pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");
    }

}


