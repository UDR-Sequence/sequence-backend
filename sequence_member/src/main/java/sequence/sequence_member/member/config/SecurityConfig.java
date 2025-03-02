package sequence.sequence_member.member.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import sequence.sequence_member.member.jwt.CustomLogoutFilter;
import sequence.sequence_member.member.jwt.JWTFilter;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.jwt.LoginFilter;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.repository.RefreshRepository;
import sequence.sequence_member.member.service.TokenReissueService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final TokenReissueService tokenReissueService;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // Custom LoginFilter 등록
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenReissueService, memberRepository);
        loginFilter.setFilterProcessesUrl("/api/login"); // 엔드포인트를 /api/login으로 변경

        http
                .cors((cors)-> cors
                        .configurationSource(new CorsConfigurationSource(){

                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request){
                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Arrays.asList("Authorization", "access"));
                                return configuration;
                            }

                        }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //form 로그인 방식 disable
        http
                .formLogin((auth)->auth.disable());


        //http basic 인증 방식 disable
        http
                .httpBasic((auth)->auth.disable());

        http
                .logout((auth)->auth.disable()); // 기본 로그아웃 필터 비활성화


        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/api/login", "/api/users/join", "/api/token", "/api/users/check_username", "/api/users/check_email", "/api/users/check_nickname", "/api/user/delete").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/projects/**").permitAll()
                        .requestMatchers("/api/archive/*/evaluations/**").authenticated()
                        .requestMatchers("/api/archive/**").permitAll()
                        .requestMatchers("/error").permitAll() // 예외 처리 로직 없을 때 Tomcat이 포워딩 하는 경로 접근 허용하여 예외 메세지 잘 전달되도록 허용
                        .anyRequest().authenticated());

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        //usernamePasswordAuthenticationFilter를 대신하여 우리가 로그인 필터를 작성하였기 때문에, (폼 로그인 방식을 disabled 하였기 때문)
        //usernamePasswordAuthenticationFilter자리에 로그인필터를 등록해준다(addFilter 사용)
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenReissueService,memberRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }

}
