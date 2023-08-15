package com.coder.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.spring.web.json.Json;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testConnection() {

        System.out.println(redisTemplate.type("backup1"));
        ObjectMapper objectMapper = new ObjectMapper();


        long start = System.currentTimeMillis();

        Object backup1 = redisTemplate.opsForValue().get("backup1");
        long end = System.currentTimeMillis();
        System.out.println(backup1.toString());
        System.out.println("spend time :" + (end - start) + "ms");
    }

    @Test
    public void testString(){
        long start = System.currentTimeMillis();
        Object name = stringRedisTemplate.opsForValue().get("name");
        long end = System.currentTimeMillis();
        System.out.println(name.toString());
        System.out.println("spend time :" + (end - start) + "ms");
    }

    @Test
    public void testSet(){
        String str = "teachers";

        redisTemplate.opsForSet().add(str,"六八","搭地","大幅","的我");
        System.out.println(redisTemplate.opsForSet().size(str));
        System.out.println(redisTemplate.opsForSet().pop(str));
        System.out.println(redisTemplate.opsForSet().members(str));
    }

    @Test
    public void testSortedSet(){
        String str = "students";

        redisTemplate.opsForZSet().add(str,"大大发",100);
        redisTemplate.opsForZSet().add(str,"法发给",20);
        redisTemplate.opsForZSet().add(str,"阿达法",90);

        System.out.println(redisTemplate.opsForZSet().zCard(str));
        System.out.println(redisTemplate.opsForZSet().score(str,"大大发"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(str,"大大发"));
        System.out.println(redisTemplate.opsForZSet().range(str,0,-1));

        redisTemplate.delete("name");
        System.out.println(redisTemplate.hasKey("name"));
    }


    //多次访问同一个数据
    @Test
    public void testBound(){
        String str = "students";
        BoundZSetOperations bound = redisTemplate.boundZSetOps(str);
        System.out.println(bound.reverseRank("大大发"));
        System.out.println(bound.size());
    }

    //编程式事务
    /**
     * 注意：事务中间，不能进行查询，无意义
     * */
    @Test
    public void testTranslate(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String str = "test::tx";

                operations.multi();

                operations.opsForSet().add(str,"张三");
                operations.opsForSet().add(str,"李四");
                operations.opsForSet().add(str,"王五");
                System.out.println(operations.opsForSet().members(str));

                return operations.exec();
            }
        });

        System.out.println(obj);
    }
}
