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

@Configuration
@EnableWebSecurity
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
        // Web Page
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/view/error").permitAll()
            .antMatchers("/view/publish/**").permitAll()
            .antMatchers("/view/display/**").permitAll()
            .antMatchers("/view/welcome.jiin").permitAll()
            .antMatchers("/view/auth/edit").authenticated()
            .antMatchers("/view/auth/**").permitAll()
            .antMatchers("/view/home/guest").anonymous()
            .antMatchers("/server/server-state").permitAll()
            .antMatchers("/server/api/account/**").hasRole("ADMIN")
            .antMatchers("/**").authenticated();

        http.csrf().disable();

        http
            .formLogin()
            .loginPage("/view/auth/login")
            .loginProcessingUrl("/view/auth/login_process")
            .failureUrl("/view/auth/login?error")
            .defaultSuccessUrl("/view/home/user")
            .usernameParameter("username")
            .passwordParameter("password");

        http
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/view/auth/logout_process"))
            .logoutSuccessUrl("/view/home/guest")
            .invalidateHttpSession(true);

        http.authenticationProvider(accountAuthProvider);

        http.headers().frameOptions().disable();
    }
}
