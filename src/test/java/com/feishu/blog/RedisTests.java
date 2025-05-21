package com.feishu.blog;

import com.feishu.blog.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/20
 */
@Slf4j
@SpringBootTest
public class RedisTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testRedis() {
        redisUtil.setWithExpire("hello", "world", 1, TimeUnit.MINUTES);
        System.out.println(redisUtil.get("hello"));
        System.out.println(redisUtil.get("world"));
    }

    @Test
    public void testRedisTimeOut() {
        System.out.println(redisUtil.get("hello"));
    }
}
