package uk.gov.justice.laa.amend.claim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2700) // 45 minutes
@Configuration
public class SessionConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ClassLoader loader = getClass().getClassLoader();
        BasicPolymorphicTypeValidator.Builder validatorBuilder = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType((ctx, clazz) -> true);
        return GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .customize(builder -> builder.addModules(SecurityJacksonModules.getModules(loader, validatorBuilder)))
                .build();
    }
}
