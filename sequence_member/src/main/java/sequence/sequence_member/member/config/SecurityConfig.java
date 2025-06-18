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
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import sequence.sequence_member.member.authority.OAuth2FailureHandler;
import sequence.sequence_member.member.authority.OAuth2SuccessHandler;
import sequence.sequence_member.member.jwt.CustomLogoutFilter;
import sequence.sequence_member.member.jwt.JWTFilter;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.jwt.LoginFilter;
import sequence.sequence_member.member.repository.CustomAuthorizationRequestRepository;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.repository.RefreshRepository;
import sequence.sequence_member.member.service.CustomOidcUserService;
import sequence.sequence_member.member.service.TokenReissueService;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOidcUserService customOidcUserService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CustomAuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // Custom LoginFilter 등록
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenReissueService, memberRepository);
        loginFilter.setFilterProcessesUrl("/api/login"); // 엔드포인트를 /api/login으로 변경

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(Arrays.asList(
                                        "http://localhost:3000",
                                        "https://parkdu7.github.io",
                                        "https://sequence-zeta.vercel.app"
                                ));
                                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                configuration.setAllowCredentials(true);

                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setExposedHeaders(Arrays.asList("Authorization", "access"));
                                return configuration;
                            }
                        }))
                .csrf((csrf) -> csrf.disable()); // 필요에 따라 유지

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
                        // 인증이 필요 없는 경로들을 먼저 정의합니다.
                        .requestMatchers(
                                "/api/login",
                                "/api/users/join",
                                "/api/token",
                                "/api/users/check_username",
                                "/api/users/check_email",
                                "/api/users/check_nickname",
                                "/api/skills/**",
                                "/api/users/test",
                                "/api/auth/**",
                                "/error",
                                "/actuator/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/favicon.ico",
                                "/.well-known/**",
                                "/"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/archive/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/archive/{archiveId}").permitAll()
                        .requestMatchers("/api/archive/**").authenticated() // 인증 필요
                        .anyRequest().authenticated()); // 나머지 모든 요청은 인증 필요
        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                );

        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(new AntPathRequestMatcher("/oauth2/authorization/**"), new Http403ForbiddenEntryPoint());
        entryPoints.put(new AntPathRequestMatcher("/favicon.ico"), new Http403ForbiddenEntryPoint());
        entryPoints.put(new AntPathRequestMatcher("/.well-known/**"), new Http403ForbiddenEntryPoint());

        http
                .exceptionHandling(exceptionHandling -> {
                    DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
                    delegatingEntryPoint.setDefaultEntryPoint(new Http403ForbiddenEntryPoint()); // 기본 엔트리포인트 명시적으로 설정
                    exceptionHandling.authenticationEntryPoint(delegatingEntryPoint);
                });

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
