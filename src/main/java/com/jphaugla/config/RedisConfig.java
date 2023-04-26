package com.jphaugla.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableAsync
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP, basePackages = {
        "com.aaaaa.bbbbb.persistence.model.repository" }, keyspaceNotificationsConfigParameter = "",
        shadowCopy = RedisKeyValueAdapter.ShadowCopy.OFF)
//  can also disable shadowCopy here with shadowCopy = ShadowCopy.OFF
//  the net effect of the above is that Spring Data Redis will no longer create the Shadow copy but will still subscribe
//  for the Keyspace events and purge the SET of the entry

@ComponentScan("com.jphaugla")
public class RedisConfig {
    @Autowired
    private Environment env;
    @Autowired
    private @Value("${app.corePoolSize:20}")
    int corePoolSize;

    @Bean(name = "redisConnectionFactory")
    @Primary
    public LettuceConnectionFactory redisConnectionFactory() {
        // LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
        //       .commandTimeout(redisCommandTimeout).poolConfig(new GenericObjectPoolConfig()).build();
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().build();
        RedisStandaloneConfiguration redisServerConf = new RedisStandaloneConfiguration();
        redisServerConf.setHostName(env.getProperty("spring.redis.host"));
        redisServerConf.setPort(Integer.parseInt(env.getProperty("spring.redis.port")));
        if(env.getProperty("spring.redis.password") != null && !env.getProperty("spring.redis.password").isEmpty()) {
            redisServerConf.setPassword(RedisPassword.of(env.getProperty("spring.redis.password")));
        }
        return new LettuceConnectionFactory(redisServerConf, clientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        on large 64 core machine, drove setCorePoolSize to 200 to really spike performance
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        return executor;
    }

}
