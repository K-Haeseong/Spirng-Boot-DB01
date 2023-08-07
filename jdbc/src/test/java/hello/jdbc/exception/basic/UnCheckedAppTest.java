package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class UnCheckedAppTest {

    // 언체크드 예외가 처리 불가능한 경우 - 계속 밖으로 던진다.
    @Test
    void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();

        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();
        public void request() {
            service.logic();
        }
    }

    // throws 생략가능
    static class Service {

        NetworkClient networkClient = new NetworkClient();
        Repository repository = new Repository();

            public void logic() {

            repository.call();
            networkClient.call();
        }
    }



    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결실패");
        }
    }


    // 체크드 예외 -> 언체크드 예외
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        private static void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }


    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message) {
            super(message);
        }
    }


    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }


}
