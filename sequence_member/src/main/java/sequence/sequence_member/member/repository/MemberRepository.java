package sequence.sequence_member.member.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUsername(String username);

    List<MemberEntity> findByNicknameIn(List<String> nickanmeList);

//    List<String> findByNicknameContaining(String nickname, int limit);

    @Query("""
    SELECT m.nickname
    FROM MemberEntity m
    WHERE m.nickname LIKE %:nickname%
    ORDER BY
        CASE
            WHEN m.nickname = :nickname THEN 0
            WHEN m.nickname LIKE :nickname% THEN 1
            ELSE 2
        END,
        LENGTH(m.nickname) ASC
    """)
    List<String> searchMemberNicknames(@Param("nickname") String nickname, Pageable pageable);
}
