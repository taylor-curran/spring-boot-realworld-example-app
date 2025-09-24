package io.spring.graphql;

import io.spring.core.user.User;
import java.util.Optional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
  public static Optional<User> getCurrentUser() {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null) {
      return Optional.empty();
    }
    
    Authentication authentication = context.getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken
        || authentication.getPrincipal() == null) {
      return Optional.empty();
    }
    io.spring.core.user.User currentUser = (io.spring.core.user.User) authentication.getPrincipal();
    return Optional.of(currentUser);
  }
}
