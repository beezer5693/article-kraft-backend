package org.brandon.articlekraftbackend.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    List<Token> findByUserId(int userId);

    Optional<Token> findByAccessToken(String token);
}
