package org.brandon.articlekraftbackend.util;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordUtils {

  private final PasswordEncoder passwordEncoder;

  public String encodePassword(Supplier<String> passwordSupplier) {
    return passwordEncoder.encode(passwordSupplier.get());
  }
}
