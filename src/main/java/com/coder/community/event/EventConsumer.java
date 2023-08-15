package com.coder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.elasticsearch.DiscussPostRepository;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Event;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.ElasticsearchService;
import com.coder.community.util.CommunityConstant;
import com.mysql.cj.xdevapi.JsonString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 注意！！ SpringBoot component类中无法注入service类
     */
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @RabbitListener(queues = "#{discussPostDirectQueue}")
    public void handlePublishMessage(String record) throws InterruptedException {
        if (record == null) {
            LOGGER.error("消息格式错误!");
            return;
        }
        Event event = JSONObject.parseObject(record, Event.class); //json字符串直接转给java对象
        if (event == null) {
            LOGGER.error("格式转换错误！");
            return;
        }
        DiscussPost post = discussPostMapper.selectDiscussPostById(event.getEntityId());
        if (post == null) {
            LOGGER.error("post 为空！");
            return;
        }
        discussPostRepository.save(post);
        LOGGER.info("MQ:消费了一个事件： 帖子{}", post);
    }

    @RabbitListener(queues = "#{discussPostDeleteDirectQueue}")
    public void handleDeleteMessage(String record) throws InterruptedException {
        if (record == null) {
            LOGGER.error("消息格式错误!");
            return;
        }
        Event event = JSONObject.parseObject(record, Event.class); //json字符串直接转给java对象
        if (event == null) {
            LOGGER.error("格式转换错误！");
            return;
        }

//        elasticsearchService.deleteDiscussPost(event.getEntityId());
        discussPostRepository.deleteById(event.getEntityId());
    }
}
