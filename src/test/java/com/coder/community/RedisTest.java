package com.coder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testString(){
        redisTemplate.opsForValue().set("name","肖家新");
        System.out.println(redisTemplate.opsForValue().get("name"));

        String  str = "test::hash";
        redisTemplate.opsForHash().put(str,"id",1);
        redisTemplate.opsForHash().put(str,"name","肖家新");
        System.out.println(redisTemplate.opsForHash().get(str,"id"));
        System.out.println(redisTemplate.opsForHash().get(str,"name"));

        String s1 = "test::list";
        redisTemplate.opsForList().leftPush(s1,101);
        redisTemplate.opsForList().leftPush(s1,102);
        redisTemplate.opsForList().leftPush(s1,103);
        System.out.println(redisTemplate.opsForList().size(s1));
        System.out.println(redisTemplate.opsForList().range(s1,1,3));
        System.out.println(redisTemplate.opsForList().index(s1,1));
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
