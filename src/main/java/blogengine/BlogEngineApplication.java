package blogengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BlogEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogEngineApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
//        ThreadPoolTaskScheduler threadPoolTaskScheduler
//                = new ThreadPoolTaskScheduler();
//        threadPoolTaskScheduler.setPoolSize(5);
//        threadPoolTaskScheduler.setThreadNamePrefix(
//                "ThreadPoolTaskScheduler");
//        threadPoolTaskScheduler.initialize();
//        return threadPoolTaskScheduler;
//    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        int maxUploadSizeInMb = 5 * 1024 * 1024;
        //cmr.setMaxUploadSize(maxUploadSizeInMb * 2);
        cmr.setMaxUploadSizePerFile(maxUploadSizeInMb);
        return cmr;
    }
}