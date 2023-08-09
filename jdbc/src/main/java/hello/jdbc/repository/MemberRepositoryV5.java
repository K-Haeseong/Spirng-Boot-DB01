package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * SQLExceptionTranslator 추가
 * */

@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    // <--------------------  CRUD  -------------------->
    // 저장
    @Override
    public Member save(Member member) {
        String sql = "insert into member(MEMBER_ID, MONEY) values(?,?)";
        template.update(sql, member.getMemberId(), member.getMoney());

        return member;
    } // save


    // 조회
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowmapper(), memberId);
    } // findById


    // 수정
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, memberId, money);
    } // update


    // 삭제
    @Override
    public void delete(String memberId) {

        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    } // delete

    private RowMapper<Member> memberRowmapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString(member.getMemberId()));
            member.setMoney((rs.getInt(member.getMoney())));
            return member;
        };
    }
}
