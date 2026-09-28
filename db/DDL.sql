-- MySQL 8.x DDL
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_life_stage`;
DROP TABLE IF EXISTS `user_household_type`;
DROP TABLE IF EXISTS `user_interest`;
DROP TABLE IF EXISTS `user_profile`;
DROP TABLE IF EXISTS `welfare_life_stage`;
DROP TABLE IF EXISTS `welfare_household_type`;
DROP TABLE IF EXISTS `welfare_interest`;
DROP TABLE IF EXISTS `welfare_link`;
DROP TABLE IF EXISTS `welfare_form`;
DROP TABLE IF EXISTS `welfare_law`;
DROP TABLE IF EXISTS `welfare_contact`;
DROP TABLE IF EXISTS `welfare_application`;
DROP TABLE IF EXISTS `welfare_ai_content`;
DROP TABLE IF EXISTS `api_collection_history`;
DROP TABLE IF EXISTS `welfare_api`;
DROP TABLE IF EXISTS `welfare_service`;
DROP TABLE IF EXISTS `life_stage`;
DROP TABLE IF EXISTS `household_type`;
DROP TABLE IF EXISTS `interest`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `life_stage` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '생애주기 ID',
	`code` VARCHAR(20) NOT NULL COMMENT '생애주기 코드',
	`name` VARCHAR(50) NOT NULL COMMENT '생애주기명',
	`description` VARCHAR(500) NULL COMMENT '생애주기 설명',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='생애주기';

CREATE TABLE `user_life_stage` (
	`user_id` VARCHAR(50) NOT NULL COMMENT '회원 ID',
	`life_stage_id` VARCHAR(50) NOT NULL COMMENT '생애주기 ID',
	PRIMARY KEY (`user_id`, `life_stage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 생애주기';

CREATE TABLE `welfare_link` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '링크 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`serv_se_code` VARCHAR(50) NULL COMMENT '서비스 구분 코드',
	`link_name` VARCHAR(200) NULL COMMENT '링크명',
	`link_url` TEXT NULL COMMENT '링크 URL',
	`sort_order` INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 관련 링크';

CREATE TABLE `users` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '회원 ID',
	`email` VARCHAR(255) NOT NULL COMMENT '로그인 이메일',
	`password` VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
	`name` VARCHAR(50) NULL COMMENT '회원 이름',
	`status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '회원 상태 (ACTIVE: 활성, INACTIVE: 비활성)',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '회원 생성 일시',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '회원 수정 일시',
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원';

CREATE TABLE `welfare_service` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '복지서비스 ID',
	`api_cd` VARCHAR(20) NULL COMMENT '수집 api 코드',
	`serv_cd` VARCHAR(20) NULL COMMENT '복지서비스 코드',
	`serv_nm` VARCHAR(200) NOT NULL COMMENT '복지서비스명',
	`jur_mnof_nm` VARCHAR(300) NULL COMMENT '소관 부처명',
	`jur_org_nm` VARCHAR(300) NULL COMMENT '소관 기관명',
	`inq_num` INT NOT NULL DEFAULT 0 COMMENT '조회수',
	`serv_dgst` TEXT NULL COMMENT '서비스 요약',
	`serv_dtl_link` VARCHAR(1000) NULL COMMENT '서비스 상세 링크',
	`svcfrst_reg_ts` VARCHAR(20) NULL COMMENT '서비스 최초 등록 일시',
	`sprt_cyc_nm` VARCHAR(500) NULL COMMENT '지원 주기',
	`srv_pvsn_nm` VARCHAR(500) NULL COMMENT '서비스 제공 형태',
	`rprs_ctadr` VARCHAR(500) NULL COMMENT '대표 문의처',
	`onap_psblt_yn` CHAR(1) NULL COMMENT '온라인 신청 가능 여부',
	`enfc_bgng_ymd` VARCHAR(8) NULL COMMENT '시행 시작일',
	`enfc_end_ymd` VARCHAR(8) NULL COMMENT '시행 종료일',
	`biz_chr_dept_nm` VARCHAR(300) NULL COMMENT '사업 담당 부서명',
	`ctpv_nm` VARCHAR(100) NULL COMMENT '시도명',
	`sgg_nm` VARCHAR(100) NULL COMMENT '시군구명',
	`wlfare_info_outl_cn` TEXT NULL COMMENT '복지서비스 개요',
	`crtr_yr` VARCHAR(4) NULL COMMENT '기준연도',
	`tgtr_dtl_cn` TEXT NULL COMMENT '대상자 상세 내용',
	`slct_crit_cn` TEXT NULL COMMENT '선정 기준',
	`alw_serv_cn` TEXT NULL COMMENT '급여 서비스 내용',
	`sprt_trgt_cn` TEXT NULL COMMENT '지원 대상 내용',
	`last_mod_ymd` VARCHAR(8) NULL COMMENT '최종 수정일',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 생성 일시',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '데이터 수정 일시',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지서비스';

CREATE TABLE `welfare_form` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '서식 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`form_name` VARCHAR(200) NULL COMMENT '서식명',
	`form_url` TEXT NULL COMMENT '서식 다운로드 URL',
	`sort_order` INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 서식';

CREATE TABLE `user_profile` (
	`user_id` VARCHAR(50) NOT NULL COMMENT '회원 ID',
	`birth_date` DATE NULL COMMENT '생년월일',
	`gender` VARCHAR(10) NULL COMMENT '성별',
	`region_code` VARCHAR(100) NULL COMMENT '거주 지역 (시도/시군구)',
	`income_level` VARCHAR(30) NULL COMMENT '소득 수준',
	`disabled_yn` CHAR(1) NOT NULL DEFAULT 'N' COMMENT '장애인 여부 (Y/N)',
	`marital_status` VARCHAR(20) NULL COMMENT '혼인 상태',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '프로필 생성 일시',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '프로필 수정 일시',
	PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 프로필';

CREATE TABLE `welfare_law` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '법령 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`law_name` VARCHAR(300) NULL COMMENT '법령명',
	`law_url` TEXT NULL COMMENT '법령 관련 URL',
	`sort_order` INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 법령';

CREATE TABLE `household_type` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '가구 형태 ID',
	`code` VARCHAR(20) NOT NULL COMMENT '가구 형태 코드',
	`name` VARCHAR(100) NOT NULL COMMENT '가구 형태명',
	`description` VARCHAR(500) NULL COMMENT '가구 형태 설명',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='가구 형태';

CREATE TABLE `api_collection_history` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT 'API 수집 이력 ID',
	`api_cd` VARCHAR(20) NULL COMMENT 'API 코드',
	`api_type` VARCHAR(20) NOT NULL COMMENT '호출한 API 유형 (LIST, DETAIL 등)',
	`request_url` TEXT NULL COMMENT 'API 요청 URL',
	`request_params` LONGTEXT NULL COMMENT 'API 요청 파라미터(JSON 문자열)',
	`response_code` INT NULL COMMENT 'API 응답 코드',
	`response_message` TEXT NULL COMMENT 'API 응답 메시지',
	`page_no` INT NULL COMMENT '요청 페이지 번호',
	`num_of_rows` INT NULL COMMENT '페이지당 요청 건수',
	`total_count` INT NULL COMMENT '전체 데이터 건수',
	`success_yn` CHAR(1) NOT NULL COMMENT 'API 수집 성공 여부 (Y/N)',
	`started_at` DATETIME NULL COMMENT 'API 수집 시작 일시',
	`finished_at` DATETIME NULL COMMENT 'API 수집 종료 일시',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API 수집 이력';

CREATE TABLE `welfare_interest` (
	`interest_id` VARCHAR(50) NOT NULL COMMENT '관심 분야 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	PRIMARY KEY (`interest_id`, `serv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 관심 분야';

CREATE TABLE `welfare_contact` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '문의처 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`serv_se_code` VARCHAR(50) NULL COMMENT '서비스 구분 코드',
	`contact_name` VARCHAR(200) NULL COMMENT '문의처명',
	`contact_value` VARCHAR(500) NULL COMMENT '문의처 정보',
	`sort_order` INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 문의처';

CREATE TABLE `welfare_household_type` (
	`household_type_id` VARCHAR(50) NOT NULL COMMENT '가구 형태 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	PRIMARY KEY (`household_type_id`, `serv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 가구 형태';

CREATE TABLE `welfare_life_stage` (
	`life_stage_id` VARCHAR(50) NOT NULL COMMENT '생애주기 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	PRIMARY KEY (`life_stage_id`, `serv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 생애주기';

CREATE TABLE `user_household_type` (
	`user_id` VARCHAR(50) NOT NULL COMMENT '회원 ID',
	`household_type_id` VARCHAR(50) NOT NULL COMMENT '가구 형태 ID',
	PRIMARY KEY (`user_id`, `household_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 가구 형태';

CREATE TABLE `interest` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '관심 분야 ID',
	`code` VARCHAR(20) NOT NULL COMMENT '관심 분야 코드',
	`name` VARCHAR(100) NOT NULL COMMENT '관심 분야명',
	`description` VARCHAR(500) NULL COMMENT '관심 분야 설명',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관심 분야';

CREATE TABLE `user_interest` (
	`user_id` VARCHAR(50) NOT NULL COMMENT '회원 ID',
	`interest_id` VARCHAR(50) NOT NULL COMMENT '관심 분야 ID',
	PRIMARY KEY (`user_id`, `interest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 관심 분야';

CREATE TABLE `welfare_ai_content` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT 'AI 콘텐츠 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`summary` TEXT NULL COMMENT '복지 서비스 전체 요약',
	`application_summary` TEXT NULL COMMENT '신청 방법 AI 요약',
	`target_summary` TEXT NULL COMMENT '지원 대상 AI 요약',
	`ai_model` VARCHAR(100) NULL COMMENT '콘텐츠 생성에 사용한 AI 모델',
	`prompt_version` VARCHAR(50) NULL COMMENT '사용한 프롬프트 버전',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'AI 콘텐츠 생성 일시',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'AI 콘텐츠 수정 일시',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 AI 콘텐츠';

CREATE TABLE `welfare_application` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT '신청 방법 ID',
	`serv_id` VARCHAR(50) NOT NULL COMMENT '복지서비스 ID',
	`serv_se_code` VARCHAR(50) NULL COMMENT '서비스 구분 코드',
	`serv_se_detail_nm` VARCHAR(200) NULL COMMENT '신청 방법 상세명',
	`serv_se_detail_link` TEXT NULL COMMENT '신청 방법 상세 링크',
	`sort_order` INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 신청 방법';

CREATE TABLE `welfare_api` (
	`id` VARCHAR(50) NOT NULL DEFAULT (UUID()) COMMENT 'API ID',
	`api_cd` VARCHAR(20) NULL COMMENT 'API 코드',
	`api_name` VARCHAR(50) NULL COMMENT 'API 명',
	`api_source_cd` VARCHAR(20) NULL COMMENT 'API 출처 코드',
	`api_url` VARCHAR(200) NULL COMMENT 'API URL',
	`description` VARCHAR(300) NULL COMMENT 'API 설명',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='복지 Open API';

-- ALTER TABLE `user_life_stage` ADD CONSTRAINT `FK_users_TO_user_life_stage_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `user_life_stage` ADD CONSTRAINT `FK_life_stage_TO_user_life_stage_1` FOREIGN KEY (`life_stage_id`) REFERENCES `life_stage` (`id`);
-- ALTER TABLE `welfare_link` ADD CONSTRAINT `FK_welfare_service_TO_welfare_link_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_form` ADD CONSTRAINT `FK_welfare_service_TO_welfare_form_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `user_profile` ADD CONSTRAINT `FK_users_TO_user_profile_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `welfare_law` ADD CONSTRAINT `FK_welfare_service_TO_welfare_law_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_interest` ADD CONSTRAINT `FK_interest_TO_welfare_interest_1` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`);
-- ALTER TABLE `welfare_interest` ADD CONSTRAINT `FK_welfare_service_TO_welfare_interest_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_contact` ADD CONSTRAINT `FK_welfare_service_TO_welfare_contact_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_household_type` ADD CONSTRAINT `FK_household_type_TO_welfare_household_type_1` FOREIGN KEY (`household_type_id`) REFERENCES `household_type` (`id`);
-- ALTER TABLE `welfare_household_type` ADD CONSTRAINT `FK_welfare_service_TO_welfare_household_type_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_life_stage` ADD CONSTRAINT `FK_life_stage_TO_welfare_life_stage_1` FOREIGN KEY (`life_stage_id`) REFERENCES `life_stage` (`id`);
-- ALTER TABLE `welfare_life_stage` ADD CONSTRAINT `FK_welfare_service_TO_welfare_life_stage_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `user_household_type` ADD CONSTRAINT `FK_users_TO_user_household_type_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `user_household_type` ADD CONSTRAINT `FK_household_type_TO_user_household_type_1` FOREIGN KEY (`household_type_id`) REFERENCES `household_type` (`id`);
-- ALTER TABLE `user_interest` ADD CONSTRAINT `FK_users_TO_user_interest_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `user_interest` ADD CONSTRAINT `FK_interest_TO_user_interest_1` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`);
-- ALTER TABLE `welfare_ai_content` ADD CONSTRAINT `FK_welfare_service_TO_welfare_ai_content_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
-- ALTER TABLE `welfare_application` ADD CONSTRAINT `FK_welfare_service_TO_welfare_application_1` FOREIGN KEY (`serv_id`) REFERENCES `welfare_service` (`id`);
