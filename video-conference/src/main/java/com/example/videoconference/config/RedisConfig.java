package com.example.videoconference.config;

import com.example.videoconference.infra.redis.listener.RedisConferenceChatMessageSubscriber;
import com.example.videoconference.infra.redis.listener.RedisConferenceSignalingMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter chatListenerAdapter,
                                                        MessageListenerAdapter signalingListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(chatListenerAdapter, new PatternTopic("conference:chat:*"));

        container.addMessageListener(signalingListenerAdapter, new PatternTopic("conference:signaling:room:*"));

        return container;
    }

    @Bean
    public MessageListenerAdapter chatListenerAdapter(RedisConferenceChatMessageSubscriber redisConferenceChatMessageSubscriber) {
        return new MessageListenerAdapter(redisConferenceChatMessageSubscriber);
    }

    @Bean
    public MessageListenerAdapter signalingListenerAdapter(RedisConferenceSignalingMessageSubscriber redisConferenceSignalingMessageSubscriber) {
        return new MessageListenerAdapter(redisConferenceSignalingMessageSubscriber);
    }
}
