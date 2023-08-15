package com.coder.community.config;

import com.coder.community.util.CommunityConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig implements CommunityConstant {
    @Bean
    public DirectExchange direct(){
        return new DirectExchange("discussPost");
    }

    @Bean
    public Queue discussPostDirectQueue(){
        return new AnonymousQueue();
    }

    @Bean
    public Queue discussPostDeleteDirectQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding discussPostInsertBinding() {
        return BindingBuilder
                .bind(discussPostDirectQueue())
                .to(direct())
                .with(TOPIC_PUBLISH);
    }

    @Bean
    public Binding discussPostDeleteBinding() {
        return BindingBuilder
                .bind(discussPostDeleteDirectQueue())
                .to(direct())
                .with(TOPIC_DELETE);
    }
}
