package sequence.sequence_member.archive.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.Status;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
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

    private String thumbnail;      

    private String link;           

    @Column(name = "skills")
    private String skills;  // "Java,Spring,JPA" 형태로 저장

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    @Builder.Default
    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArchiveMember> archiveMembers = new ArrayList<>();

    @Builder.Default
    @Column(name = "view", nullable = false, columnDefinition = "int default 0")
    private Integer view = 0;      // 조회수

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
} 
