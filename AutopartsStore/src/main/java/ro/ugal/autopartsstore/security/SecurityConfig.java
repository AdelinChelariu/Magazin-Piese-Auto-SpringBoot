package ro.ugal.autopartsstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(
                                "/", "/index.html",
                                "/login.html", "/register.html",
                                "/css/**", "/js/**", "/favicon.ico"
                        ).permitAll()

                        // register public
                        .requestMatchers("/api/auth/register").permitAll()

                        // client pages
                        .requestMatchers("/shop.html", "/cart.html")
                        .hasAnyRole("CLIENT", "EMPLOYEE", "ADMIN")

                        // static pages
                        .requestMatchers("/reports.html", "/js/reports.js").hasAnyAuthority("ROLE_ADMIN","ROLE_EMPLOYEE")
                        // api endpoints
                        .requestMatchers("/api/reports/**").hasAnyAuthority("ROLE_ADMIN","ROLE_EMPLOYEE")


                        // admin pages
                        .requestMatchers("/products.html", "/categories.html", "/manufacturers.html")
                        .hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers("/activity-log.html")
                        .hasRole("ADMIN")

                        // READ data: client ok
                        .requestMatchers(HttpMethod.GET,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/manufacturers/**"
                        ).hasAnyRole("CLIENT", "EMPLOYEE", "ADMIN")

                        .requestMatchers("/reports.html", "/js/reports.js")
                        .hasAnyAuthority("EMPLOYEE", "ADMIN")

                        .requestMatchers("/api/reports/**")
                        .hasAnyAuthority("EMPLOYEE", "ADMIN")

                        // client orders
                        .requestMatchers("/api/client/**")
                        .hasRole("CLIENT")

                        // manage endpoints
                        .requestMatchers("/api/*/manage/**")
                        .hasAnyRole("EMPLOYEE", "ADMIN")

                        // activity log API
                        .requestMatchers("/api/activity-log/**")
                        .hasRole("ADMIN")

                        // rest of api
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/shop.html", true) // client intră în shop
                        .failureUrl("/login.html?error=1")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout=1")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
