package blogengine.security;

import blogengine.models.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.session.data.redis.RedisSessionRepository;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * @EnableRedisHttpSession - заставляет использовать Spring Session Redis вместо Tomcat HttpSession.
 * Создается Bean springSessionRepositoryFilter, имплементирующий Filter. Для того, чтобы Tomcat использвал
 * этот бин, создадим класс Initializer, расширяющий класс AbstractHttpSessionApplicationInitializer.
 */

//@EnableRedisHttpSession
public class SessionConfig {

//    @Bean
//    public LettuceConnectionFactory connectionFactory() {
//        return new LettuceConnectionFactory();
//    }

































//    private final RedisConnectionFactory redisConnectionFactory;
//
//
//    public SessionConfig(ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
//        this.redisConnectionFactory = redisConnectionFactory.getIfAvailable();
//    }
//
//    @Bean
//    public RedisOperations<String, User> sessionRedisOperations() {
//        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//
//    @Bean
//    public RedisSessionRepository sessionRepository(RedisOperations<String, User> sessionRedisOperations) {
//        return new RedisSessionRepository(sessionRedisOperations);
//    }
}

