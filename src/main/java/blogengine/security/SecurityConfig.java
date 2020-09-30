package blogengine.security;

import blogengine.controllers.ApiAuthController;
import blogengine.mappers.UserDtoMapper;
import blogengine.models.dto.authdto.AuthenticationResponse;
import blogengine.models.dto.authdto.LoginRequest;
import blogengine.util.SessionStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;
    private final ApiAuthController authController;
    private final SessionStorage sessionStorage;
    private final UserDtoMapper userDtoMapper;

    @Bean
    public CustomUsernamePasswordAuthFilter authenticationFilter() throws Exception {
        CustomUsernamePasswordAuthFilter authenticationFilter
                = new CustomUsernamePasswordAuthFilter();
        authenticationFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(sessionStorage, userDtoMapper));
        authenticationFilter.setAuthenticationFailureHandler(new CustomAccessDeniedHandler());
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/auth/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
//                .exceptionHandling()
//                .accessDeniedHandler((request, response, accessDeniedException) -> {
//                    response.sendError(HttpServletResponse.SC_OK);
//                })
//                .authenticationEntryPoint((request, response, authException) -> {
//                    response.sendError(HttpServletResponse.SC_OK);
//                })
//                .and()
//                .cors()
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/post", "/login").hasRole("USER")
//                .antMatchers("/api/auth/login").permitAll()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/api/auth/login")
//                .usernameParameter("e_mail")
//                .passwordParameter("password")
//                .failureHandler(this::loginFailureHandler);
//                .and()
//                .logout()
//                .and()

                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

//    private void loginSuccessHandler(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     Authentication authentication) throws IOException {
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        log.info("username: " + userDetails.getUsername());
//        log.info("password: " + userDetails.getPassword());
//        LoginRequest loginRequest = new LoginRequest(userDetails.getUsername(), userDetails.getPassword());
//        ResponseEntity<AuthenticationResponse> login = authController.login(loginRequest);
//        log.info(String.valueOf(login));
//    }

//    private void loginFailureHandler(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     AuthenticationException authentication) throws IOException {
//        throw new IncorrectCredentialsException("Логин и/или пароль введен(ы) неверно");
//    }
}
