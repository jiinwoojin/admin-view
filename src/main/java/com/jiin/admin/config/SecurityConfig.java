package com.jiin.admin.config;

import com.jiin.admin.website.security.AccountAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@ComponentScan({ "com.jiin.admin.website.security" })
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public AccountAuthProvider accountAuthProvider;

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
            "/scss/**",
            "/toastr-lib/**",
            "/vendor/**",
            "/vue/**",
            "/favicon.ico"
        );
    }

    // HTTP URI Security Customization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/view/publish/**").permitAll()
            .antMatchers("/view/welcome.jiin").permitAll()
            .antMatchers("/server/server-state").permitAll()
            .antMatchers("/view/auth/**").permitAll()
            .antMatchers("/**").authenticated();

        http.csrf().disable();

        http
            .formLogin()
            .loginPage("/view/auth/login")
            .loginProcessingUrl("/view/auth/login_process")
            .failureUrl("/view/auth/login?error")
            .defaultSuccessUrl("/view/user/home")
            .usernameParameter("username")
            .passwordParameter("password");

        http
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/view/auth/logout_process"))
            .logoutSuccessUrl("/view/guest/home")
            .invalidateHttpSession(true);

        http.authenticationProvider(accountAuthProvider);
    }
}
