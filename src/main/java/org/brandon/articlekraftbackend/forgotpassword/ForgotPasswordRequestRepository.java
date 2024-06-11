package org.brandon.articlekraftbackend.forgotpassword;

import org.brandon.articlekraftbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForgotPasswordRequestRepository extends JpaRepository<ForgotPasswordRequest, Integer> {
    @Query("select fpr.user from ForgotPasswordRequest fpr where fpr.code = :code")
    User findUserByCode(@Param("code") String code);

    Optional<ForgotPasswordRequest> findByCode(String code);

    List<ForgotPasswordRequest> findAllByUser(User user);

    void deleteByCode(String code);
}
