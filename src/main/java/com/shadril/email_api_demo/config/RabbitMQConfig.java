package com.shadril.email_api_demo.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${rabbitmq.config.email-queue}")
    private String emailQueueName;

    @Value("${rabbitmq.config.email-exchange}")
    private String emailExchangeName;

    @Value("${rabbitmq.config.email-routing-key}")
    private String emailRoutingKeyName;
    @Value("${rabbitmq.config.email-dlx}")
    private String emailDLXName;

    @Value("${rabbitmq.config.email-queue-retry}")
    private String emailQueueRetryName;

    @Value("${rabbitmq.config.email-retry-routing-key}")
    private String emailRetryRoutingKeyName;

    @Value("${rabbitmq.config.email-queue-dlq}")
    private String emailQueueDLQName;

    @Value("${rabbitmq.config.email-dlq-routing-key}")
    private String emailDLQRoutingKeyName;

    @Value("${rabbitmq.config.email-queue-scheduled}")
    private String emailQueueScheduledName;

    @Value("${rabbitmq.config.email-scheduled-routing-key}")
    private String emailScheduledRoutingKeyName;

    @Value("${rabbitmq.config.email-max-retries}")
    private Long emailMaxRetries;

    @Value("${rabbitmq.config.email-min-priority}")
    private int emailMinPriority;

    @Value("${rabbitmq.config.email-max-priority}")
    private int emailMaxPriority;

    @Value("${rabbitmq.config.email-message-ttl}")
    private int emailMessageTTL;

    @Value("${rabbitmq.config.concurrent-consumers}")
    private int concurrentConsumers;

    @Value("${rabbitmq.config.max-concurrent-consumers}")
    private int maxConcurrentConsumers;

    @Value("${rabbitmq.config.consumer-start-timeout}")
    private int consumerStartTimeout;

    @Value("${rabbitmq.config.start-consumer-min-interval}")
    private int startConsumerMinInterval;

    @Value("${rabbitmq.config.recovery-interval}")
    private int recoveryInterval;

    @Value("${rabbitmq.config.prefetch-count}")
    private int prefetchCount;

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", emailDLXName)
                .withArgument("x-dead-letter-routing-key", emailRetryRoutingKeyName)
                .withArgument("x-max-priority", emailMaxPriority)
                .build();
    }

    @Bean
    public Queue emailRetryQueue() {
        return QueueBuilder.durable(emailQueueRetryName)
                .withArgument("x-message-ttl", emailMessageTTL)
                .withArgument("x-dead-letter-exchange", emailExchangeName)
                .withArgument("x-dead-letter-routing-key", emailRoutingKeyName)
                .build();
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(emailQueueDLQName).build();
    }

    @Bean
    public Queue emailScheduledQueue() {
        return QueueBuilder.durable(emailQueueScheduledName)
                .withArgument("x-dead-letter-exchange", emailExchangeName)
                .withArgument("x-dead-letter-routing-key", emailRoutingKeyName)
                .build();
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(emailExchangeName);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(emailDLXName);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(emailRoutingKeyName);
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(emailRetryQueue()).to(deadLetterExchange()).with(emailRetryRoutingKeyName);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(deadLetterExchange()).with(emailDLQRoutingKeyName);
    }

    @Bean
    public Binding scheduledBinding() {
        return BindingBuilder.bind(emailScheduledQueue()).to(emailExchange()).with(emailScheduledRoutingKeyName);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean(name = "virtualThreadExecutor")
//    public Executor virtualThreadExecutor() {
//        return Executors.newVirtualThreadPerTaskExecutor();
//    }


    @Primary
    @Bean(name = "configurableThreadExecutor")
    public TaskExecutor configurableThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(20);
        executor.setKeepAliveSeconds(5 * 60); //5 minutes
        executor.setThreadNamePrefix("EmailSender-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "mQThreadExecutor")
    public Executor MQThreadExecutor() {
        return Executors.newCachedThreadPool();
    }
}
