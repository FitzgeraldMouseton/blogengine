package blogengine.security;

import blogengine.mappers.UserDtoMapper;
import blogengine.util.SessionStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;
    private final SessionStorage sessionStorage;
    private final UserDtoMapper userDtoMapper;

    @Bean
    public CustomUsernamePasswordAuthFilter authenticationFilter() throws Exception {
        CustomUsernamePasswordAuthFilter authenticationFilter
                = new CustomUsernamePasswordAuthFilter();
        authenticationFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(sessionStorage, userDtoMapper));
        authenticationFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/auth/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler deniedHandler = new CustomAccessDeniedHandler();
        return deniedHandler;
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
                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/api/post/**").hasRole(Role.USER.name())
                .antMatchers(HttpMethod.PUT, "/api/post/**").hasAnyRole(Role.USER.name(), Role.MODERATOR.name())
//                .antMatchers(HttpMethod.POST, "/api/*").hasAnyRole(Role.USER.name(), Role.MODERATOR.name())
                .antMatchers("/api/auth/login", "/css/*", "/js/*").permitAll()
                .and()
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/api/auth/login")
//                .usernameParameter("e_mail")
//                .passwordParameter("password")
                .logout().logoutUrl("/api/auth/logout")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
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
