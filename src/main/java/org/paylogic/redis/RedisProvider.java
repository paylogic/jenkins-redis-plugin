package org.paylogic.redis;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Provides Redis :D
 */
@Extension
public class RedisProvider extends GlobalConfiguration {

    @Getter @Setter private String hostname = "localhost";
    @Getter @Setter private int port = 6379;
    @Getter @Setter private int database = 0;

    private transient JedisPool jedisPool;

    public RedisProvider() {
        jedisPool = new JedisPool(new JedisPoolConfig(), this.hostname, this.port);
        load();
    }

    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public Jedis getConnection() {
        Jedis client = jedisPool.getResource();
        client.select(this.database);
        return client;
    }

    public void returnConnection(Jedis connection) {
        jedisPool.returnResource(connection);
    }

    public void returnBrokenConnection(Jedis connection) {
        jedisPool.returnBrokenResource(connection);
    }
}