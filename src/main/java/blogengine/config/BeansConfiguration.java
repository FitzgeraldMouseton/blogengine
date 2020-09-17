package blogengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class BeansConfiguration {

    @Value("${image.max_size}")
    private int uploadImageMaxSize;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        int maxUploadSizeInMb = uploadImageMaxSize;
        cmr.setMaxUploadSizePerFile(maxUploadSizeInMb);
        return cmr;
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
}
