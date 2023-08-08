package hello.jdbc.exception.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    // Repository = DB에 회원 저장, 체크드 익셉션 -> MyDuplicateKeyException으로 전환
    // Service = 중복아이디 들어올 경우 뒤에 랜덤 숫자 붙여서 다시 저장

    Repository repository;

    Service service;


    @BeforeEach
    void init() {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeyCheck() {
       service.create("member");
        service.create("member");
    }



    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;

        public Member save(Member member) {

            String sql = "insert into member(MEMBER_ID, MONEY) values(?,?)";

            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();

                return member;

            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) { // h2 db 에러코드
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        } // save
    } // Repository

    @RequiredArgsConstructor
    static class Service {

        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("saveId = {}", memberId);
            } catch(MyDuplicateKeyException e) {
                log.info("키 중복");
                String retryId = createNewId(memberId);
                log.info("retryId = {}", retryId);
                repository.save(new Member(retryId, 0));
            }
        }

        // 새로운 아이디 만들기
        private String createNewId(String memberId) {
            return memberId + new Random().nextInt(1000);
        }
    }

}
