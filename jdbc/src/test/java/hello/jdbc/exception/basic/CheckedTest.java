package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
    }



    // 1. try ~ catch 활용해서 잡기
    // 2. 밖으로 던지기
    static class Service {

        Repository repository = new Repository();

        // 예외 잡기
        public void callCatch() {

            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        // 예외 던지기
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }


    // 내가 만든 체크드 예외
    // Exception을 상속 받으면 체크드 예외가 된다.
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }


}
