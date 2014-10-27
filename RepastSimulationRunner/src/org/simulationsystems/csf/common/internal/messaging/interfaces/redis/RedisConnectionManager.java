package org.simulationsystems.csf.common.internal.messaging.interfaces.redis;

import redis.clients.jedis.Jedis;

/*
 * Manages the CRUD operations to Redis. This class has no business-level knowledge.
 */
public class RedisConnectionManager {
	private Jedis jedis=null;

	public void initializeRedisConnection(final String host) {
		jedis = new Jedis(host);

		// TODO: Throw exception is Jedis returns null?
	}

	public void postMessage(String channel, String message) {
		jedis.set(channel, message);
		String value = jedis.get(channel);
		System.out.println("Set: " + value);
	}
}
