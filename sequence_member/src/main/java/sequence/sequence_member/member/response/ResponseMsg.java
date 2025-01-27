package sequence.sequence_member.member.response;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseMsg {
    private int status;
    private String message;
    private Map<String, String> resultMsg;

    public ResponseMsg(int status, String message,  Map<String, String> resultMsg){
        this.status = status;
        this.message=message;
        this.resultMsg = resultMsg;
    }

}
