package org.brandon.articlekraftbackend.handlers;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.brandon.articlekraftbackend.util.DateTimeUtils;
import org.springframework.http.HttpStatus;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Response<T> {

  private int statusCode;
  private String message;
  private boolean success;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String timestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String path;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T errors;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;

  public static <T> Response<T> success(T data, HttpStatus statusCode) {
    return Response.<T>builder()
        .success(Result.SUCCESS.value)
        .message("Success")
        .statusCode(statusCode.value())
        .data(data)
        .build();
  }

  public static <T> Response<T> error(HttpStatus statusCode, String message, T errors,
      HttpServletRequest request) {
    return Response.<T>builder()
        .success(Result.ERROR.value)
        .message(message)
        .statusCode(statusCode.value())
        .path(request.getServletPath())
        .errors(errors)
        .timestamp(DateTimeUtils.parseAndFormatDateTime(LocalDateTime.now()))
        .build();
  }

  @Getter
  @RequiredArgsConstructor
  private enum Result {
    SUCCESS(true),
    ERROR(false);

    private final boolean value;
  }
}
