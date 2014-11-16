package org.simulationsystems.csf.common.internal.messaging.interfaces.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * Manages the CRUD operations to Redis. This class has no business-level knowledge.
 */
public class RedisConnectionManager {
	// private Jedis jedis=null;
	private JedisPool jedisPool;
	private String host;

	public void initializeRedisConnection(final String host) {
		// jedis = new Jedis(host);
		jedisPool = new JedisPool(new JedisPoolConfig(), host);
		// Hold on to the host to re-establish connection in the future, if
		// necessary.
		this.host = host;

		// TODO: Throw exception is Jedis returns null?
	}

	public void postMessage(String channel, String message) {
		Jedis jedis = jedisPool.getResource();
		jedis.lpush(channel, message);

		System.out.println("Set: " + message);
		// System.out.println("lpop: " + jedis.lpop(channel));
		// System.out.println("lpop: " + jedis.lpop(channel));
	}

	public void closePool() {
		jedisPool.destroy();
	}
}
