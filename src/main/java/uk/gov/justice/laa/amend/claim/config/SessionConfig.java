package uk.gov.justice.laa.amend.claim.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
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

  // TODO: Or should we use jdk for all default serialization and configure redisTemplate to use
  // json?
  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new HybridRedisSerializer(jsonSerializer(), new JdkSerializationRedisSerializer());
  }

  private RedisSerializer<Object> jsonSerializer() {
    ClassLoader loader = getClass().getClassLoader();
    BasicPolymorphicTypeValidator.Builder validatorBuilder =
        BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Object.class)
            .allowIfSubType((ctx, clazz) -> true);
    return GenericJacksonJsonRedisSerializer.builder()
        // Required to embed type info in JSON so Redis can deserialise polymorphic session
        // attributes
        .enableUnsafeDefaultTyping()
        .customize(
            builder -> {
              builder.addModules(SecurityJacksonModules.getModules(loader, validatorBuilder));
              builder.addMixIn(UUID.class, UuidMixin.class);
            })
        .build();
  }

  @RequiredArgsConstructor
  private static class HybridRedisSerializer implements RedisSerializer<Object> {

    private final RedisSerializer<Object> json;
    private final RedisSerializer<Object> jdk;

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
      if (obj == null) {
        return new byte[0];
      }

      if (isWebflowObject(obj)) {
        return jdk.serialize(obj);
      }
      return json.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
      if (bytes == null || bytes.length == 0) {
        return null;
      }

      try {
        return json.deserialize(bytes);
      } catch (Exception e) {
        return jdk.deserialize(bytes);
      }
    }

    private boolean isWebflowObject(Object obj) {
      return obj.getClass().getName().startsWith("org.springframework.webflow");
    }
  }
}
