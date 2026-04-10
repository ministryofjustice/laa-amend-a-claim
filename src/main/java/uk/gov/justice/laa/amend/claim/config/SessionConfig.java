package uk.gov.justice.laa.amend.claim.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;
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

    // MixIn to force Jackson to include type info for UUID, which it otherwise skips because UUID is
    // a final Java class and Spring's default typing uses DefaultTyping.NON_FINAL.
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    abstract static class UuidMixin {}

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ClassLoader loader = getClass().getClassLoader();
        BasicPolymorphicTypeValidator.Builder validatorBuilder = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType((ctx, clazz) -> true);
        return GenericJacksonJsonRedisSerializer.builder()
                // Required to embed type info in JSON so Redis can deserialise polymorphic session attributes
                .enableUnsafeDefaultTyping()
                .customize(builder -> {
                    builder.addModules(SecurityJacksonModules.getModules(loader, validatorBuilder));
                    builder.addMixIn(UUID.class, UuidMixin.class);
                })
                .build();
    }
}
