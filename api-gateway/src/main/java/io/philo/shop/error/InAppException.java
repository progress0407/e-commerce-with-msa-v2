package io.philo.shop.error;

import org.springframework.http.HttpStatusCode;

public class InAppException extends RuntimeException {

    private final HttpStatusCode httpStatus;

    public InAppException(HttpStatusCode httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatusCode getHttpStatus() {
        return httpStatus;
    }
}
