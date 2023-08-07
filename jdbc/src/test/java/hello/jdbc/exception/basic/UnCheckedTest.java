package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
        void unchecked_throw() {
            UnCheckedTest.Service service = new UnCheckedTest.Service();
            assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUnCheckedException.class);
        }


        // 1. try ~ catch 활용해서 잡기
        // 2. 밖으로 던지기
        static class Service {

        Repository repository = new Repository();

        // 예외 잡기
        public void callCatch() {

            try {
                repository.call();
            } catch (MyUnCheckedException e) {
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        // 예외 던지기
        public void callThrow() {
            repository.call();
        }
    }


    // 내가 만든 언체크드 예외
    // RuntimeException을 상속 받으면 언체크드 예외(런타임 예외)가 된다.
    static class MyUnCheckedException extends RuntimeException {
        public MyUnCheckedException(String message) {
            super(message);
        }
    }

    // throws 생략해도 괜찮다! - 대신 예외를 던지는 건 같다.
    static class Repository {
        public void call() {
            throw new MyUnCheckedException("ex");
        }
    }


}
