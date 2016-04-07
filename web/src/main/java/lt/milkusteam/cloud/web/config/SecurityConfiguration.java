package lt.milkusteam.cloud.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by gediminas on 3/31/16.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Profile("!test")
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("gedkur").password("pikachu").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("arnmil").password("charmander").roles("USER");
        auth.inMemoryAuthentication().withUser("tombru").password("bulbasaur").roles("USER");
        auth.inMemoryAuthentication().withUser("vilstr3").password("squirtle").roles("USER");
    }

    @Profile("test")
    @Autowired
    public void configureGlobal2(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("gedkur").password("pikachu").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("arnmil").password("charmander").roles("USER");
        auth.inMemoryAuthentication().withUser("tombru").password("bulbasaur").roles("USER");
        auth.inMemoryAuthentication().withUser("vilstr3").password("squirtle").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/index", "/about").permitAll()
                .antMatchers("/secret/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").failureUrl("/login?error").permitAll()
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().logoutSuccessUrl("/login?logout").permitAll()
                .and()
                .csrf();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }
}
