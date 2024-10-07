package org.example.tutoring.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.tutoring.jwt.dto.AccessTokenResponse;
import org.example.tutoring.jwt.dto.TokenResponse;
import org.example.tutoring.members.Member;
import org.example.tutoring.members.MemberRepository;
import org.example.tutoring.members.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key key;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(@Value("${jwt.token.key}") String secretKey, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 첫 로그인 시 토큰 생성
    public ResponseEntity<TokenResponse> createToken(LoginRequest request) {

        long now = (new Date()).getTime();

        // 토큰 회원 정보 찾기
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000);

        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim("memberInfo", createClaims(member))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + 86400000);

        String refreshToken = Jwts.builder()
                .setSubject(member.getUsername())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 빌드 및 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenDetail(refreshToken)
                .member(member)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    // 리프레시까지 만료가 됐을 경우
    public TokenResponse reCreateToken(Member member) {
        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim("userInfo", createClaims(member))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + 86400000);

        String refreshToken = Jwts.builder()
                .setSubject(member.getUsername())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 빌드 및 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenDetail(refreshToken)
                .member(member)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        System.out.println("새로 만든 리프레시 토큰 저장 완료");

        return new TokenResponse(accessToken, refreshToken);
    }

    // 리프레시 토큰을 기반으로 어세스 토큰 재발급
    public ResponseEntity<?> createAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        System.out.println(token);

        RefreshToken getRefreshToken = refreshTokenRepository.findByTokenDetail(token).orElseThrow();
        Member member = memberRepository.findById(getRefreshToken.member.getUserId()).orElseThrow();

        if(!validationToken(getRefreshToken.getTokenDetail())) {
            refreshTokenRepository.delete(getRefreshToken);
            TokenResponse tokenResponse = reCreateToken(member);
            return ResponseEntity.ok().body(tokenResponse);
        }

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 86400000);

        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim("memberInfo", createClaims(member))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return ResponseEntity.ok().body(new AccessTokenResponse(accessToken));
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(token);
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);
        // UserDetails 객체를 만들어서 Authentication return
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // jwt 검증
    public boolean validationToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // claim 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // claim 만들기
    private Map<String, Object> createClaims(Member member) {
//        String userId = authentication.getName();
//        Users users = usersRepository.findByUserId(userId).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getUserId());
        claims.put("username", member.getUsername());
        return claims;
    }

    // 클레임에서 권한 정보를 추출하는 메서드
    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        String roles = claims.get("Lank", String.class); // roles 클레임에서 역할 정보 추출
        if (roles == null) {
            return new ArrayList<>(); // 역할 정보가 없을 경우 빈 리스트 반환
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roles));
        return authorities;
    }
}