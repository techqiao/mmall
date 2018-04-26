package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description :sharded redis 连接池
 * <p>Date : 2018-04-26 10:26
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class RedisShardedPool {
    //sharded jedis连接池
    private static ShardedJedisPool pool;
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
    //redis1 ip
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    //redis1 port
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    //redis2 ip
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    //redis2 port
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));


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
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        //sharded jedis连接池 MurmurHash 是一种非加密型哈希函数,适用于一般的哈希检索操作
        //ShardedJedis是通过一致性哈希来实现分布式缓存的，通过一定的策略把不同的key分配到不同的redis server上，达到横向扩展的目的
        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    //加载到jvm 初始化连接池
    static {
        initPool();
    }

    //从连接池拿一个实例Jedis
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    //把 坏的jedis 放回连接池
    public static void returnBrokenResource(ShardedJedis jedis) {
        //源码里面已经判断 jedis != null
        pool.returnBrokenResource(jedis);
    }

    //把 jedis 放回连接池
    public static void returnResource(ShardedJedis jedis) {
        //源码里面已经判断 jedis != null
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        jedis.setex("key", 1000 * 10, "value");
        returnResource(jedis);

        //pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");
    }
}
