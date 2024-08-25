package org.example.redis.ui;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisViewer {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        syncCommands.keys("*").forEach(key -> {
            String type = syncCommands.type(key);
            System.out.println("Key: " + key + ", Type: " + type);

            switch (type) {
                case "string":
                    System.out.println("Value: " + syncCommands.get(key));
                    break;
                case "list":
                    System.out.println("Value: " + syncCommands.lrange(key, 0, -1));
                    break;
                case "set":
                    System.out.println("Value: " + syncCommands.smembers(key));
                    break;
                case "hash":
                    System.out.println("Value: " + syncCommands.hgetall(key));
                    break;
                case "zset":
                    System.out.println("Value: " + syncCommands.zrangeWithScores(key, 0, -1));
                    break;
                default:
                    System.out.println("Unsupported type: " + type);
                    break;
            }
        });

        connection.close();
        redisClient.shutdown();
    }
}