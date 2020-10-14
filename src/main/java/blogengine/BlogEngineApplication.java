package blogengine;

/*
 * http://springbootblog-env.eba-gedscb4d.us-east-2.elasticbeanstalk.com/posts/recent
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BlogEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogEngineApplication.class, args);
    }
}