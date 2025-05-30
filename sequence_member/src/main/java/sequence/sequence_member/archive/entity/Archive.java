package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Status;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
import sequence.sequence_member.member.entity.MemberEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "archive")
public class Archive extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id",nullable = false)
    private MemberEntity writer;
    
    @Column(nullable = false)
    private String title;          

    @Column(nullable = false)
    private String description;    

    @Column(nullable = false)
    private LocalDate startDate;  // 시작일

    @Column(nullable = false)
    private LocalDate endDate;    // 종료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;     // Category enum 사용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;         

    @Column(name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "thumbnail_file_name")
    private String thumbnailFileName;  // 썸네일 파일명 저장 필드 추가

    private String link;           

    @Column(name = "skills")
    private String skills;  // "Java,Spring,JPA" 형태로 저장

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    @Column(name = "file_names", columnDefinition = "TEXT")
    private String fileNames;  // 파일명 저장 필드 추가

    @Builder.Default
    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArchiveMember> archiveMembers = new ArrayList<>();

    @Builder.Default
    @Column(name = "view", nullable = false, columnDefinition = "int default 0")
    private Integer view = 0;      // 조회수

    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArchiveComment> comments;

    // skills를 List<String>으로 변환하는 메서드
    public List<String> getSkillList() {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(skills.split(","));
    }

    // List<String>을 문자열로 변환하는 메서드
    public void setSkillsFromList(List<String> skillList) {
        if (skillList == null || skillList.isEmpty()) {
            this.skills = "";
            return;
        }
        this.skills = String.join(",", skillList);
    }

    public List<String> getImageUrlsAsList() {
        if (this.imgUrl == null || this.imgUrl.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.imgUrl.split(","));
    }

    public void setImageUrlsFromList(List<String> imageUrlList) {
        if (imageUrlList == null || imageUrlList.isEmpty()) {
            this.imgUrl = "";
            return;
        }
        this.imgUrl = String.join(",", imageUrlList);
    }

    // 파일명 관련 메서드 추가
    public List<String> getFileNamesAsList() {
        if (this.fileNames == null || this.fileNames.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.fileNames.split(","));
    }
    
    public void setFileNamesFromList(List<String> fileNameList) {
        if (fileNameList == null || fileNameList.isEmpty()) {
            this.fileNames = "";
            return;
        }
        this.fileNames = String.join(",", fileNameList);
    }

    // duration String 대신 날짜 기간을 반환하는 메서드
    public String getDurationAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");
        return startDate.format(formatter) + " ~ " + endDate.format(formatter);
    }

    // 아카이브 업데이트 메서드 수정
    public void updateArchive(ArchiveUpdateDTO archiveUpdateDTO) {
        this.title = archiveUpdateDTO.getTitle();
        this.description = archiveUpdateDTO.getDescription();
        this.startDate = archiveUpdateDTO.getStartDate();
        this.endDate = archiveUpdateDTO.getEndDate();
        this.category = archiveUpdateDTO.getCategory();
        this.thumbnail = archiveUpdateDTO.getThumbnail();
        this.link = archiveUpdateDTO.getLink();
        setSkillsFromList(archiveUpdateDTO.getSkills());
        
        // 이미지 URL 설정
        if (archiveUpdateDTO.getImgUrls() != null && !archiveUpdateDTO.getImgUrls().isEmpty()) {
            setImageUrlsFromList(archiveUpdateDTO.getImgUrls());
        } else {
            this.imgUrl = "";  // imgUrls가 없으면 비움
        }
    }

    // 조회수 설정 메서드 추가
    public void setView(Integer view) {
        this.view = view;
    }

    // 썸네일 파일명 설정 메서드 추가
    public void setThumbnailFileName(String thumbnailFileName) {
        this.thumbnailFileName = thumbnailFileName;
    }

    public void setThumbnail(String thumbnailUrl) {
        this.thumbnail = thumbnailUrl;
    }

    // 상태 변경 메서드 추가
    public void setStatus(Status status) {
        this.status = status;
    }

    // 썸네일과 이미지를 제외한 기본 정보만 업데이트
    public void updateBasicInfo(ArchiveUpdateDTO archiveUpdateDTO) {
        this.title = archiveUpdateDTO.getTitle();
        this.description = archiveUpdateDTO.getDescription();
        this.startDate = archiveUpdateDTO.getStartDate();
        this.endDate = archiveUpdateDTO.getEndDate();
        this.category = archiveUpdateDTO.getCategory();
        this.link = archiveUpdateDTO.getLink();
        setSkillsFromList(archiveUpdateDTO.getSkills());
    }

    public void setTitle(@NotEmpty(message = "제목을 입력해주세요.") @Length(min = 1, max = 40, message = "제목은 40자 이하로 입력해주세요.") String title) {
        this.title = title;
    }

    public void setDescription(@NotEmpty(message = "설명을 입력해주세요.") @Length(min = 1, max = 450, message = "설명은 450자 이하로 입력해주세요.") String description) {
        this.description = description;
    }

    public void setStartDate(@NotNull(message = "시작일을 입력해주세요.") LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@NotNull(message = "종료일을 입력해주세요.") LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCategory(@NotNull(message = "카테고리를 선택해주세요.") Category category) {
        this.category = category;
    }

    public void setLink(String link) { this.link = link; }
    
    public Period calculatePeriod() {
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);

        if (monthsBetween < 1) {
            return Period.ONE_MONTH_LESS;
        } else if (monthsBetween < 3) {
            return Period.ONE_TO_THREE_MONTH;
        } else if (monthsBetween < 6) {
            return Period.THREE_TO_SIX_MONTH;
        } else if (monthsBetween < 12) {
            return Period.SIX_TO_ONE_YEAR;
        } else {
            return Period.OVER_ONE_YEAR;
        }
    }
} 
