-- MySQL 8.x 마이그레이션

-- 2026-09-22: 거주 지역을 코드가 아닌 시도/시군구 문자열로 저장하도록 컬럼 길이와 코멘트를 변경
ALTER TABLE `user_profile` MODIFY COLUMN `region_code` VARCHAR(100) NULL COMMENT '거주 지역 (시도/시군구)';
