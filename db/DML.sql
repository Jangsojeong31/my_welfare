-- 공통 코드 / API 마스터 초기 데이터

INSERT INTO `life_stage` (`code`, `name`, `description`) VALUES
	('01', '전생애', '생애주기 코드_전생애'),
	('02', '영유아', '생애주기 코드_영유아'),
	('03', '아동', '생애주기 코드_아동'),
	('04', '청소년', '생애주기 코드_청소년'),
	('05', '청년', '생애주기 코드_청년'),
	('06', '중장년', '생애주기 코드_중장년'),
	('07', '노년', '생애주기 코드_노년'),
	('08', '임신·출산', '생애주기 코드_임신·출산');

INSERT INTO `household_type` (`code`, `name`, `description`) VALUES
	('01', '다문화·탈북민', '가구형태 코드_다문화·탈북민'),
	('02', '다자녀', '가구형태 코드_다자녀'),
	('03', '보훈대상자', '가구형태 코드_보훈대상자'),
	('04', '장애인', '가구형태 코드_장애인'),
	('05', '저소득', '가구형태 코드_저소득'),
	('06', '한부모·조손', '가구형태 코드_한부모·조손');

INSERT INTO `interest` (`code`, `name`, `description`) VALUES
	('01', '신체건강', '관심분야 코드_신체건강'),
	('02', '정신건강', '관심분야 코드_정신건강'),
	('03', '생활지원', '관심분야 코드_생활지원'),
	('04', '주거', '관심분야 코드_주거'),
	('05', '일자리', '관심분야 코드_일자리'),
	('06', '문화·여가', '관심분야 코드_문화·여가'),
	('07', '안전·위기', '관심분야 코드_안전·위기'),
	('08', '임신·출산', '관심분야 코드_임신·출산'),
	('09', '보육', '관심분야 코드_보육'),
	('10', '교육', '관심분야 코드_교육'),
	('11', '입양·위탁', '관심분야 코드_입양·위탁'),
	('12', '보호·돌봄', '관심분야 코드_보호·돌봄'),
	('13', '서민금융', '관심분야 코드_서민금융'),
	('14', '법률', '관심분야 코드_법률'),
	('15', '관계개선', '관심분야 코드_관계개선'),
	('16', '에너지', '관심분야 코드_에너지');

INSERT INTO `welfare_api` (`api_cd`, `api_name`, `api_source_cd`, `api_url`, `description`) VALUES
	(
		'NAW01',
		'NationalWelfarelistV001',
		'NAW',
		'https://apis.data.go.kr/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001',
		'한국사회보장정보원 중앙부처 복지서비스 목록 조회 API'
	),
	(
		'NAW02',
		'NationalWelfaredetailedV001',
		'NAW',
		'https://apis.data.go.kr/B554287/NationalWelfareInformationsV001/NationalWelfaredetailedV001',
		'한국사회보장정보원 중앙부처 복지서비스 상세 조회 API'
	),
	(
		'LCW01',
		'LcgvWelfarelist',
		'LCW',
		'https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations/LcgvWelfarelist',
		'한국사회보장정보원 지자체 복지서비스 목록 조회 API'
	),
	(
		'LCW02',
		'LcgvWelfaredetailed',
		'LCW',
		'https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations/LcgvWelfaredetailed',
		'한국사회보장정보원 지자체 복지서비스 상세 조회 API'
	);
