package com.ecab;

import com.ecab.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RabbitMQConfigTest {

    @Test
    void testRabbitTemplate() {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();

        RabbitTemplate rabbitTemplate = rabbitMQConfig.rabbitTemplate(connectionFactory);

        assertNotNull(rabbitTemplate);
        assertTrue(rabbitTemplate.getMessageConverter() instanceof Jackson2JsonMessageConverter);
    }
}
