<img src="https://github.com/user-attachments/assets/aa910e32-1c13-4878-be1f-f9fea4b90b5a" alt="서비스 소개" width="100%"/>


<br/>
<br/>

# 0. Getting Started (서비스 구성 및 실행 방법)
배포 링크 (개발 중입니다!) : https://seungyun-park.github.io/sequence/ 

<br/>
<br/>

# 1. About Project
- 프로젝트 목적: 대학 간 PM/디자이너/개발자 교류 서비스
- 프로젝트 설명: 대학에 인재들이 정말 많다고 생각합니다. 하지만 이 인재들이 학교 내에서만 머물러 있고, 타 학교의 여러 인재들과 여러가지 상황들로 인하여, 협업이 어렵다는 것을 깨닫고 인재 교류 플랫폼을 개발하게 되었습니다!!

<br/>
<img src="" alt="프로젝트 아키텍처 업로드 예정!">


<br/>
<br/>

# 2. Team14 Members (팀원 및 팀 소개)
| 나강민 |
|:------:|
| <img src="https://avatars.githubusercontent.com/u/139530542?v=4" alt="나강민" width="150"> |
| Lead |
| [GitHub](https://github.com/KangminNa) |

| 김대연 | 김재환 | 하헌찬 | 김민지 | 박규원 | 조승빈 | 
|:------:|:------:|:------:|:------:|:------:|:------:|
| <img src="https://avatars.githubusercontent.com/u/58037317?v=4" alt="김대연" width="150"> | <img src="https://avatars.githubusercontent.com/u/158824294?v=4" alt="김재환" width="150"> | <img src="https://avatars.githubusercontent.com/u/127941120?v=4" alt="하헌찬" width="150"> |<img src="https://github.com/user-attachments/assets/79a13b56-b031-4dd3-8146-b24902259017" alt="김민지" width="150"> |<img src="https://avatars.githubusercontent.com/u/125748258?v=4" alt="박규원" width="150"> |<img src="https://avatars.githubusercontent.com/u/67574367?s=130&v=4" alt="조승빈" width="150"> |
| TECH-LEAD | BE | BE | BE | BE | BE |
| [GitHub](https://github.com/kim946509) | [GitHub](https://github.com/Jaeboong) | [GitHub](https://github.com/HaHeonchan) |[GitHub](https://github.com/Minji6) |[GitHub](https://github.com/High-Quality-Coffee) |[GitHub](https://github.com/vkflco08) |


| 박승균 | 정준용 | 최보경 |
|:------:|:------:|:------:|
| <img src="https://avatars.githubusercontent.com/u/59389436?v=4" alt="박승균" width="150"> | <img src="https://avatars.githubusercontent.com/u/168815407?v=4" alt="정준용" width="150"> | <img src="https://avatars.githubusercontent.com/u/169350112?v=4" alt="최보경" width="150"> | 
| FE | FE | FE |
| [GitHub](https://github.com/seungyun-Park) | [GitHub](https://github.com/beshurl) | [GitHub](https://github.com/bogyeong11) |

<br/>
<br/>

# 3. Key Features (주요 기능)
- **회원 관리**:
  - 로그인이 진행되고, 필터 단에서 유저의 권한을 확인하여 토큰을 발급합니다.
  - 발급된 토큰을 기반으로 권한별 API를 처리합니다.
  - Security Config에서 권한을 처리합니다.

- **프로젝트**:
  - 프로젝트 CRUD가 가능합니다
  - 프로젝트 별 댓글/대댓글 작업이 가능합니다
  - 유저 초대가 가능합니다
  - 프로젝트 제목/카테고리 별 필터링이 가능합니다.

- **아카이빙**:
  - 지금까지 진행했던 프로젝트 아카이빙이 가능합니다.
  - 상호 팀원 평가가 가능합니다
  
- **마이페이지**:
  - 지금까지 진행했던 프로젝트를 볼 수 있습니다
  - 내 정보를 확인할 수 있습니다
  - 나의 기술스택 등 개인정보를 확인 할 수 있습니다.

- **회원탈퇴/신고**:
  - soft-delete 방식을 선택하였고 is_deleted 컬럼으로 회원탈퇴 여부를 처리해주고 있습니다.
  - 탈퇴에 영향을 받는 서비스들도 is_deleted 컬럼을 통해 관리해줍니다.


<br/>
<br/>

# 4. ERD
erdcloud : https://www.erdcloud.com/d/gYWeuxRwBSdStHtFD
<br/>
<br/>
<img src="https://github.com/user-attachments/assets/a3be58ba-d753-45e8-959b-9ea583c011f8" alt="erd">


<br/>
<br/>

# 6. Technology Stack (기술 스택)

## 6.1 BackEnd
|  |  |  |
|-----------------|-----------------|-----------------|
| SpringBoot    |  <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white" alt="SpringBoot" width="200"> | 3.4.3    |
| Java    |  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white" alt="Java" width="200" > | 17 |
| Spring Data JPA    |  <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=SpringDataJPA&logoColor=white" alt="JPA" width="200" >    | 5.0.0  |
| QueryDSL    |  <img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=QueryDSL&logoColor=white" alt="QueryDSL" alt="QueryDSL" width="200" >    | 1.11.12    |
| Spring Security |  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white" alt="QueryDSL" alt="QueryDSL" width="200">    | 3.4.2    |

<br/>

## 6.2 Infra
|  |  |  |
|-----------------|-----------------|-----------------|
| MySQL  |  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white" alt="MySQL" width="200">    |  |
| phpmyadmin    |  <img src="https://img.shields.io/badge/phpmyadmin-6C78AF?style=for-the-badge&logo=phpmyadmin&logoColor=white" alt="phpmyadmin" width="200">    |   |
| Nginx Proxy Manager |  <img src="https://img.shields.io/badge/Nginx Proxy Manager-F15833?style=for-the-badge&logo=NginxProxyManager&logoColor=white" alt="npm" width="200">    | latest |
| Docker  |  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white" alt="docker" width="200">    |  |

<br/>
<br/>

# 7. Development Workflow (개발 워크플로우)
## 브랜치 전략 (Branch Strategy)
우리의 브랜치 전략은 아래와 같은 Git Flow를 기반으로 하며, 다음과 같은 브랜치를 사용합니다.

<img src="https://github.com/user-attachments/assets/f683ef08-c485-4447-a1d8-a5c6468e093a" alt="git flow" width="500">


- Main Branch
  - 배포 가능한 상태의 코드를 유지합니다.
  - 모든 배포는 이 브랜치에서 이루어집니다.
  
- develop Branch
  - 통합 기능 관리 브랜치 입니다
  - feat에서 개발한 기능을 develop 브랜치에서 통합하여 관리합니다.
 
- feat Branch
  - 기능 개발 브랜치 입니다.
  - 기능 단위로 브랜치를 나누어 기능을 개발하였습니다.
 
- refactor Branch
  - 코드 리팩토링 브랜치 입니다.
  - 코드 리팩토링이 필요한 경우 refactor 브랜치에서 작업했습니다.
 
- release Branch
  - 배포 전 버전을 관리하는 브랜치 입니다.
  - 최종 배포하기 전 테스트를 진행하고, 이상이 없다면 Main브랜치로 배포를 진행합니다.
 
- hotfix Branch
  - 핫픽스를 관리하는 브랜치 입니다.
  - 배포된 환경에서 수정사항이 발생했을 경우, hotfix 브랜치에서 관리하였습니다.

<br/>
<br/>

# 8. Convention

## 네이밍 컨벤션
함수명은 카멜 케이스를 기본으로 하고 컬럼명은 스네이크케이스를 기본으로 한다.
```
// 카멜 케이스
camelCase
// 스네이크 케이스
snake_case
```

<br/>

<br/>
<br/>

# 9. 커밋 컨벤션
참고 : https://hqc24.tistory.com/9
<br/>
<br/>
```

[feat]: 회원탈퇴 기능 수정

[목적]: 기존 hard-delete 방식 대신, soft-delete 방식을 적용하여 기능 수정을 하기 위해서.  

[목표]: soft-delete 방식으로 회원탈퇴를 구현하여, 유저가 물리적으로 삭제되는 것이 아닌,
	   상태값으로 탈퇴여부를 관리하여, 유저 계정 복구에 대비.  

[달성도]: 

  - soft-delete 구현 완료.  

  - 멤버 테이블에 탈퇴여부 상태값 추가 완료.  

[기타]: DB 과부화를 방지하기 위해서, 탈퇴 회원은 30일 이후 자동 삭제되도록 처리할 필요가 있음.

```


<br/>
<br/>
