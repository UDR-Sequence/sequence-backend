package sequence.sequence_member.member.repository;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUsernameAndIsDeletedFalse(String username);
    Optional<MemberEntity> findByNickname(String nickname);
    Optional<MemberEntity> findByEmail(String email);
    boolean existsByNickname(String nickname);

    @Transactional
    @Modifying
    void deleteByUsername(@Param("username") String username);

    List<MemberEntity> findByNicknameIn(List<String> nickanmeList);

    Optional<MemberEntity> findById(Long userId);

    Optional<MemberEntity> findByUsernameAndEmail(
            @NotBlank(message = "아이디를 입력해주세요.")
            String username,
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "유효한 이메일 형식이 아닙니다.")
            String email);


//    List<String> findByNicknameContaining(String nickname, int limit);

    @Query(value = """
    SELECT m.nickname
    FROM MemberEntity m
    WHERE m.nickname LIKE CONCAT('%', :nickname, '%')
    AND m.isDeleted = false
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

    // 아이디 찾기
    @Query("""
    SELECT m FROM MemberEntity m
    WHERE m.name = :name
      AND m.birth = :birth
      AND m.gender = :gender
      AND m.phone = :phone
      AND m.email = :email
      AND m.isDeleted = false
""")
    Optional<MemberEntity> findMemberForFindUsername(
            @Param("name") String name,
            @Param("birth") LocalDate birth,
            @Param("gender") MemberEntity.Gender gender,
            @Param("phone") String phone,
            @Param("email") String email
    );
}
