package sequence.sequence_member.member.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sequence.sequence_member.member.entity.MemberEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String user_id;
    private String user_pwd;

    public static LoginDTO toLoginDTO(MemberEntity memberEntity){
        LoginDTO loginDTO  = new LoginDTO();
        loginDTO.setUser_id(memberEntity.getUsername());
        loginDTO.setUser_pwd(memberEntity.getPassword());

        return loginDTO;
    }
}
