package sequence.sequence_member.member.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.dto.PasswordResetInputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.entity.PasswordResetEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.repository.PasswordResetTokenRepository;

import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final MemberRepository memberRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private static final String BASE_URL = "https://sequence-zeta.vercel.app";

    @Transactional
    public void createPasswordResetToken(FindPasswordInputDTO findPasswordInputDTO) {
        MemberEntity member = memberRepository.findByEmail(findPasswordInputDTO.getEmail())
                .orElseThrow(() -> new UserNotFindException("존재하지 않는 이메일입니다."));

        // 기존 토큰 제거
        passwordResetTokenRepository.deleteByToken(member.getUsername());

        // 새 토큰 발급
        PasswordResetEntity entity = new PasswordResetEntity(member, Duration.ofHours(1));
        passwordResetTokenRepository.save(entity);
        String token = entity.getToken();

        // 메일 전송
        String resetLink = BASE_URL + "/reset-password/" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(findPasswordInputDTO.getEmail());
        msg.setSubject("[Sequence] 비밀번호 재설정 안내");
        msg.setText(
                "안녕하세요.\n\n" +
                        "아래 링크를 클릭해 비밀번호를 재설정해주세요:\n" +
                        resetLink + "\n\n" +
                        "※ 이 링크는 발송 후 1시간 동안만 유효합니다."
        );
        mailSender.send(msg);
    }

    @SneakyThrows
    @Transactional
    public void resetPassword(PasswordResetInputDTO dto) {
        // Optional.orElseThrow 사용: 값이 없으면 바로 BadRequestException 던짐
        PasswordResetEntity entity = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new BadRequestException("유효하지 않은 토큰입니다."));

        // 만료 검사
        if (entity.isExpired()) {
            passwordResetTokenRepository.deleteByToken(dto.getToken());
            throw new BadRequestException("토큰이 만료되었습니다.");
        }

        // 비밀번호 변경
        MemberEntity member = entity.getMember();
        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.save(member);

        // 사용된 토큰 삭제
        passwordResetTokenRepository.deleteByToken(dto.getToken());
    }

}
