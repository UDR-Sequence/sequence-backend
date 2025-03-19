package sequence.sequence_member.alarm.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.alarm.dto.ProjectArchiveAlarmDTO;
import sequence.sequence_member.archive.dto.UserArchiveDTO;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.AcceptProjectOutputDTO;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;
import sequence.sequence_member.member.service.InviteAccessService;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class alarmController {

    private final InviteAccessService inviteAccessService;
    private final ArchiveService archiveService;

    //유저가 초대받은 프로젝트 목록과 최신 아카이빙 프로젝트 최대 5개 조회
    @GetMapping("/project-archive")
    public ResponseEntity<ApiResponseData<ProjectArchiveAlarmDTO>> getProjectArchiveAlarm(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InviteProjectOutputDTO> invitedProjects = inviteAccessService.getInvitedProjects(customUserDetails);
        List<UserArchiveDTO> userArchiveList = archiveService.getUserArchiveList(customUserDetails);
        return ResponseEntity.ok(ApiResponseData.success(new ProjectArchiveAlarmDTO(invitedProjects,userArchiveList)));
    }

    //유저가 초대받은 프로젝트 목록을 조회하는 컨트롤러
    @GetMapping("/invited-projects")
    public ResponseEntity<ApiResponseData<List<InviteProjectOutputDTO>>> getInvitedProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(ApiResponseData.success(inviteAccessService.getInvitedProjects(customUserDetails)));
    }

    //유저가 초대받은 프로젝트에 승인하는 컨트롤러
    @PostMapping("/invited-projects/{projectInvitedMemberId}")
    public ResponseEntity<ApiResponseData<String>> acceptInvite(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long projectInvitedMemberId){
        inviteAccessService.acceptInvite(customUserDetails, projectInvitedMemberId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }

    //유저가 초대받은 프로젝트에 거절하는 컨트롤러
    @DeleteMapping("/invited-projects/{projectInvitedMemberId}")
    public ResponseEntity<ApiResponseData<String>> rejectInvite(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long projectInvitedMemberId){
        inviteAccessService.rejectInvite(customUserDetails, projectInvitedMemberId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }

    //유저가 승인한(참여하는) 프로젝트 목록을 조회하는 컨트롤러
    @GetMapping("/accepted-projects")
    public ResponseEntity<ApiResponseData<List<AcceptProjectOutputDTO>>> getAcceptedProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(ApiResponseData.success(inviteAccessService.getAcceptedProjects(customUserDetails)));
    }

}
