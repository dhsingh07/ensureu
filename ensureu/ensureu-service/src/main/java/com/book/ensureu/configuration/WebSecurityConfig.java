package com.book.ensureu.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.book.ensureu.security.JwtAuthenticationEntryPoint;
import com.book.ensureu.security.JwtAuthorizationTokenFilter;
import com.book.ensureu.security.util.JwtSecurityTokenUtil;
import com.book.ensureu.service.AutenticationService;
import com.book.ensureu.service.impl.JwtUserDetailsService;

/**
 * @author dharmendra.singh
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtSecurityTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private AutenticationService autenticationService;

    @Value("${spring.jwt.authentication.path}")
    private String authenticationPath;

    public static final String USER_ADMIN = "ADMIN";
    public static final String USER_USER = "USER";
    public static final String USER_SUPERADMIN = "SUPERADMIN";
    public static final String USER_TEACHER = "TEACHER";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()
                // .anonymous().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()

                // user,token and metadata create service
                .antMatchers("/user/create").permitAll()
                .antMatchers("/auth/token/**").permitAll()
                .antMatchers("/auth/providertoken").permitAll()
                .antMatchers("/course/list").permitAll()
                .antMatchers("/subscription/getAllType/**").permitAll()
                .antMatchers("/role/**").permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/pass/**").permitAll()
                .antMatchers("/add/**").permitAll()
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()

                .antMatchers("/otp/**").permitAll()
                .antMatchers("/api/actuator/**").permitAll()
                .antMatchers("/pastpaper/user/list/paperType/**").permitAll()

                .antMatchers("/subscription/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_USER)
                .antMatchers("/paper/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_USER)
                .antMatchers("/mail/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_USER)
                .antMatchers("/pastpaper/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_USER)
                // .antMatchers("/otp/**").hasAnyRole(USER_ADMIN,USER_USER)

                .antMatchers("/paidpaper/**").hasAnyRole(USER_ADMIN)
                .antMatchers("/paidpapercoll/**").hasAnyRole(USER_ADMIN)
                .antMatchers("/FreePaperApi/**").hasAnyRole(USER_ADMIN)
                .antMatchers("/FreePaperCollectionApi/**").hasAnyRole(USER_ADMIN)
                .antMatchers("/pastpaperCollection/**").hasAnyRole(USER_ADMIN)
                .antMatchers("/practicepaper/**").permitAll()

                //blog
                .antMatchers("/blog/create").hasAnyRole(USER_ADMIN)
                .antMatchers("/blog/update").hasAnyRole(USER_ADMIN)
                .antMatchers("/blog/upload/image").hasAnyRole(USER_ADMIN)
                .antMatchers("/blog/delete/**").hasAnyRole(USER_ADMIN)

                .antMatchers("/blog/**").permitAll()
                .antMatchers("/notification/config/**").permitAll()
                //pastpaperCollection

                // File storage - GET is public, POST/DELETE requires auth
                .antMatchers(HttpMethod.GET, "/files/**").permitAll()
                .antMatchers("/files/upload/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)
                .antMatchers("/files/delete/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)

                // SUPERADMIN-only endpoints
                .antMatchers("/admin/feature-config/**").hasAnyRole(USER_SUPERADMIN)
                .antMatchers("/admin/llm-config/**").hasAnyRole(USER_SUPERADMIN)
                .antMatchers("/admin/subscription/**").hasAnyRole(USER_SUPERADMIN)
                .antMatchers("/admin/subscription-management/**").hasAnyRole(USER_SUPERADMIN)
                .antMatchers("/admin/user-management/**").hasAnyRole(USER_SUPERADMIN)

                // SUPERADMIN + ADMIN endpoints
                .antMatchers("/admin/dashboard/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN)

                // SUPERADMIN only for paper deletion
                .antMatchers(HttpMethod.DELETE, "/admin/paper/delete/**").hasRole(USER_SUPERADMIN)

                // SUPERADMIN + ADMIN + TEACHER endpoints
                .antMatchers("/admin/paper/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)
                .antMatchers("/admin/paper-image/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)
                .antMatchers("/admin/source-material/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)
                .antMatchers("/admin/upload/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)
                .antMatchers("/admin/question-bank/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER)

                // AI Service endpoints - available to authenticated users
                .antMatchers("/ai/health").permitAll()
                .antMatchers("/ai/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN, USER_TEACHER, USER_USER)

                // Catch-all for remaining admin endpoints
                .antMatchers("/admin/**").hasAnyRole(USER_SUPERADMIN, USER_ADMIN)

                .antMatchers(HttpMethod.OPTIONS).permitAll()
                //.antMatchers("analytics/**").permitAll()
               // .antMatchers("dataIngest/**").permitAll()
                .anyRequest().authenticated();

        // Custom JWT based security filter
        JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), jwtTokenUtil, "Authorization", autenticationService);
        httpSecurity
                .addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().and()  // required to set will be blank.
                .cacheControl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // AuthenticationTokenFilter will ignore the below paths
        web
                .ignoring()
                .antMatchers(
                        HttpMethod.POST,
                        authenticationPath
                )
                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/static/image/*",
                        "/swagger-ui.html",
                        "/swagger-ui.**",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                );
    }
}
