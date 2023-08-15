package com.coder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.coder.community.entity.Event;
import com.coder.community.util.CommunityConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventProducer implements CommunityConstant {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //处理事件
    public void fireEvent(Event event) {
        rabbitTemplate.convertAndSend("discussPost", TOPIC_PUBLISH, JSONObject.toJSONString(event));
    }
}
