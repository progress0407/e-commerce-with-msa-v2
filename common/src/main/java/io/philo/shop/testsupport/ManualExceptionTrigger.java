package io.philo.shop.testsupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 예외를 의도적으로 발생시키는 용도로 사용
 *
 * 마커 어노테이션 - 소스 코드 추적용으로 사용
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ManualExceptionTrigger {
}
