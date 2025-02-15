package sequence.sequence_member.member.repository;

import java.lang.reflect.Member;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.MemberEntity;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUsername(String username);
    Optional<MemberEntity> findByNickname(String nickname);
    Optional<MemberEntity> findByEmail(String email);
    boolean existsByNickname(String nickname);

    @Transactional
    @Modifying
    void deleteByUsername(@Param("username") String username);

    List<MemberEntity> findByNicknameIn(List<String> nickanmeList);

    Optional<MemberEntity> findById(Long userId);

//    List<String> findByNicknameContaining(String nickname, int limit);

    @Query(value = """
    SELECT m.nickname
    FROM MemberEntity m
    WHERE m.nickname LIKE CONCAT('%', :nickname, '%')
    ORDER BY
        CASE
            WHEN m.nickname = :nickname THEN 0
            WHEN m.nickname LIKE CONCAT(:nickname, '%') THEN 1
            ELSE 2
        END,
        LOCATE(:nickname, m.nickname),
        LENGTH(m.nickname) ASC
    """)
    List<String> searchMemberNicknames(@Param("nickname") String nickname, Pageable pageable);

}
