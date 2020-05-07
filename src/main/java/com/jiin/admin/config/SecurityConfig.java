package com.jiin.admin.config;

import com.jiin.admin.website.security.AccountAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@ComponentScan({ "com.jiin.admin.website.security" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public final AccountAuthProvider accountAuthProvider;

    public SecurityConfig(AccountAuthProvider accountAuthProvider) {
        this.accountAuthProvider = accountAuthProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("test").password("{noop}test").roles("ADMIN");
    }

    // Resource 접속 인가 확인 배제.
    @Override
    public void configure(WebSecurity web){
        web.ignoring().antMatchers(
            "/css/**",
            "/gis/**",
            "/img/**",
            "/jquery/**",
            "/js/**",
            "/json/**",
            "/style/**",
            "/scss/**",
            "/vendor/**",
            "/vue/**",
            "/design/**",
            "/design-park/**",
            "/favicon.ico"
        );
    }

    // HTTP URI Security Customization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Web Page
        http.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/view/error").permitAll()
                .antMatchers("/view/publish/**").permitAll()
                .antMatchers("/view/display/**").permitAll()
                .antMatchers("/view/welcome.jiin").permitAll()
                .antMatchers("/view/auth/edit").authenticated()
                .antMatchers("/view/auth/**").permitAll()
                .antMatchers("/view/home/guest").anonymous()
                .antMatchers("/view/los/**").permitAll()	//이지훈 LOS가시화 예외추가
                .antMatchers("/server/api/los/**").permitAll()	//이지훈 LOS가시화 예외추가
                .antMatchers("/server/server-state").permitAll()
                .antMatchers("/server/api/status/**").permitAll()
                .antMatchers("/server/api/layer/**").permitAll()
                .antMatchers("/server/api/map/**").permitAll()
                .antMatchers("/server/api/proxy/**").permitAll()
                .antMatchers("/server/api/check/**").permitAll()
                .antMatchers("/server/api/system/**").permitAll()
                .antMatchers("/server/api/account/**").hasRole("ADMIN")
                .antMatchers("/**").authenticated();

        http.csrf().disable();

        http.formLogin()
                .loginPage("/view/auth/login")
                .loginProcessingUrl("/view/auth/login_process")
                .failureUrl("/view/auth/login?error")
                .defaultSuccessUrl("/view/home/user")
                .usernameParameter("username")
                .passwordParameter("password");

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/view/auth/logout_process"))
                .logoutSuccessUrl("/view/home/guest")
                .invalidateHttpSession(true);

        http.authenticationProvider(accountAuthProvider);

        http.headers().frameOptions().disable();
    }
}
