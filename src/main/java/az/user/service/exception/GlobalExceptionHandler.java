package az.user.service.exception;

import az.user.service.model.constants.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalErrorResponse.builder()
                        .requestId(UUID.randomUUID())
                        .errorCode(ErrorCode.NOT_FOUND)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .build()
                );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handleNotFoundException(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .requestId(UUID.randomUUID())
                        .errorCode(ErrorCode.ALREADY_EXISTS)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .build()
                );
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<GlobalErrorResponse> handleNotFoundException(InvalidInputException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .requestId(UUID.randomUUID())
                        .errorCode(ErrorCode.INVALID_INPUT)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalErrorResponse> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build()
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GlobalErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build()
                );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GlobalErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalErrorResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleAllExceptions(Exception e) {
        e.printStackTrace(); // terminalda görə bilərsən
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalErrorResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .errorMessage(e.getMessage())
                        .timeStamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build()
                );
    }
}
