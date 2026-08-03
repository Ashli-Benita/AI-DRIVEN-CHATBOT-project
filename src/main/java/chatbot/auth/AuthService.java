package chatbot.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository,
                       @Value("${jwt.secret:change-me-in-production-change-me-in-production}") String jwtSecret,
                       @Value("${jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        String normalizedSecret = jwtSecret.length() < 32 ? jwtSecret + "-default-secret-key-for-chatbot-2026" : jwtSecret;
        this.jwtSecretKey = Keys.hmacShaKeyFor(normalizedSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(jwtSecretKey)
                .compact();
    }

    public Optional<String> extractUsername(String token) {
        try {
            return Optional.ofNullable(
                    Jwts.parser()
                            .verifyWith(jwtSecretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject()
            );
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
