package com.welfare.welfare.domain;

import com.welfare.common.domain.UuidTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "welfare_service")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareService extends UuidTimeEntity {

    @Column(name = "api_cd", length = 20)
    private String apiCd;

    @Column(name = "serv_cd", length = 20)
    private String servCd;

    @Column(name = "serv_nm", nullable = false, length = 200)
    private String servNm;

    @Column(name = "jur_mnof_nm", length = 300)
    private String jurMnofNm;

    @Column(name = "jur_org_nm", length = 300)
    private String jurOrgNm;

    @Column(name = "inq_num", nullable = false)
    private Integer inqNum = 0;

    @Column(name = "serv_dgst", columnDefinition = "text")
    private String servDgst;

    @Column(name = "serv_dtl_link", length = 1000)
    private String servDtlLink;

    @Column(name = "svcfrst_reg_ts", length = 20)
    private String svcfrstRegTs;

    @Column(name = "sprt_cyc_nm", length = 500)
    private String sprtCycNm;

    @Column(name = "srv_pvsn_nm", length = 500)
    private String srvPvsnNm;

    @Column(name = "rprs_ctadr", length = 500)
    private String rprsCtadr;

    @Column(name = "onap_psblt_yn", length = 1, columnDefinition = "char(1)")
    private String onapPsbltYn;

    @Column(name = "enfc_bgng_ymd", length = 8)
    private String enfcBgngYmd;

    @Column(name = "enfc_end_ymd", length = 8)
    private String enfcEndYmd;

    @Column(name = "biz_chr_dept_nm", length = 300)
    private String bizChrDeptNm;

    @Column(name = "ctpv_nm", length = 100)
    private String ctpvNm;

    @Column(name = "sgg_nm", length = 100)
    private String sggNm;

    @Column(name = "wlfare_info_outl_cn", columnDefinition = "text")
    private String wlfareInfoOutlCn;

    @Column(name = "crtr_yr", length = 4)
    private String crtrYr;

    @Column(name = "tgtr_dtl_cn", columnDefinition = "text")
    private String tgtrDtlCn;

    @Column(name = "slct_crit_cn", columnDefinition = "text")
    private String slctCritCn;

    @Column(name = "alw_serv_cn", columnDefinition = "text")
    private String alwServCn;

    @Column(name = "sprt_trgt_cn", columnDefinition = "text")
    private String sprtTrgtCn;

    @Column(name = "last_mod_ymd", length = 8)
    private String lastModYmd;

    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(
            name = "welfare_life_stage",
            joinColumns = @JoinColumn(name = "serv_id"),
            inverseJoinColumns = @JoinColumn(name = "life_stage_id")
    )
    private Set<LifeStage> lifeStages = new HashSet<>();

    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(
            name = "welfare_household_type",
            joinColumns = @JoinColumn(name = "serv_id"),
            inverseJoinColumns = @JoinColumn(name = "household_type_id")
    )
    private Set<HouseholdType> householdTypes = new HashSet<>();

    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(
            name = "welfare_interest",
            joinColumns = @JoinColumn(name = "serv_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<Interest> interests = new HashSet<>();

    @OneToMany(mappedBy = "welfareService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WelfareApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "welfareService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WelfareContact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "welfareService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WelfareLink> links = new ArrayList<>();

    @OneToMany(mappedBy = "welfareService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WelfareForm> forms = new ArrayList<>();

    @OneToMany(mappedBy = "welfareService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WelfareLaw> laws = new ArrayList<>();

    @Builder
    private WelfareService(
            String apiCd,
            String servCd,
            String servNm,
            String jurMnofNm,
            String jurOrgNm,
            Integer inqNum,
            String servDgst,
            String servDtlLink,
            String svcfrstRegTs,
            String sprtCycNm,
            String srvPvsnNm,
            String rprsCtadr,
            String onapPsbltYn,
            String enfcBgngYmd,
            String enfcEndYmd,
            String bizChrDeptNm,
            String ctpvNm,
            String sggNm,
            String wlfareInfoOutlCn,
            String crtrYr,
            String tgtrDtlCn,
            String slctCritCn,
            String alwServCn,
            String sprtTrgtCn,
            String lastModYmd
    ) {
        this.apiCd = apiCd;
        this.servCd = servCd;
        this.servNm = servNm;
        this.jurMnofNm = jurMnofNm;
        this.jurOrgNm = jurOrgNm;
        this.inqNum = inqNum == null ? 0 : inqNum;
        this.servDgst = servDgst;
        this.servDtlLink = servDtlLink;
        this.svcfrstRegTs = svcfrstRegTs;
        this.sprtCycNm = sprtCycNm;
        this.srvPvsnNm = srvPvsnNm;
        this.rprsCtadr = rprsCtadr;
        this.onapPsbltYn = onapPsbltYn;
        this.enfcBgngYmd = enfcBgngYmd;
        this.enfcEndYmd = enfcEndYmd;
        this.bizChrDeptNm = bizChrDeptNm;
        this.ctpvNm = ctpvNm;
        this.sggNm = sggNm;
        this.wlfareInfoOutlCn = wlfareInfoOutlCn;
        this.crtrYr = crtrYr;
        this.tgtrDtlCn = tgtrDtlCn;
        this.slctCritCn = slctCritCn;
        this.alwServCn = alwServCn;
        this.sprtTrgtCn = sprtTrgtCn;
        this.lastModYmd = lastModYmd;
    }

    public void applyDetail(
            String jurMnofNm,
            String servDgst,
            String sprtCycNm,
            String srvPvsnNm,
            String rprsCtadr,
            Integer inqNum,
            String enfcBgngYmd,
            String enfcEndYmd,
            String bizChrDeptNm,
            String ctpvNm,
            String sggNm,
            String wlfareInfoOutlCn,
            String crtrYr,
            String tgtrDtlCn,
            String slctCritCn,
            String alwServCn,
            String sprtTrgtCn,
            String lastModYmd
    ) {
        if (jurMnofNm != null) {
            this.jurMnofNm = jurMnofNm;
        }
        if (servDgst != null) {
            this.servDgst = servDgst;
        }
        if (sprtCycNm != null) {
            this.sprtCycNm = sprtCycNm;
        }
        if (srvPvsnNm != null) {
            this.srvPvsnNm = srvPvsnNm;
        }
        if (rprsCtadr != null) {
            this.rprsCtadr = rprsCtadr;
        }
        if (inqNum != null) {
            this.inqNum = inqNum;
        }
        if (enfcBgngYmd != null) {
            this.enfcBgngYmd = enfcBgngYmd;
        }
        if (enfcEndYmd != null) {
            this.enfcEndYmd = enfcEndYmd;
        }
        if (bizChrDeptNm != null) {
            this.bizChrDeptNm = bizChrDeptNm;
        }
        if (ctpvNm != null) {
            this.ctpvNm = ctpvNm;
        }
        if (sggNm != null) {
            this.sggNm = sggNm;
        }
        if (wlfareInfoOutlCn != null) {
            this.wlfareInfoOutlCn = wlfareInfoOutlCn;
        }
        if (crtrYr != null) {
            this.crtrYr = crtrYr;
        }
        if (tgtrDtlCn != null) {
            this.tgtrDtlCn = tgtrDtlCn;
        }
        if (slctCritCn != null) {
            this.slctCritCn = slctCritCn;
        }
        if (alwServCn != null) {
            this.alwServCn = alwServCn;
        }
        if (sprtTrgtCn != null) {
            this.sprtTrgtCn = sprtTrgtCn;
        }
        if (lastModYmd != null) {
            this.lastModYmd = lastModYmd;
        }
    }

    public void replaceLifeStages(Set<LifeStage> values) {
        this.lifeStages.clear();
        this.lifeStages.addAll(values);
    }

    public void replaceHouseholdTypes(Set<HouseholdType> values) {
        this.householdTypes.clear();
        this.householdTypes.addAll(values);
    }

    public void replaceInterests(Set<Interest> values) {
        this.interests.clear();
        this.interests.addAll(values);
    }

    public void addApplication(WelfareApplication application) {
        applications.add(application);
        application.setWelfareService(this);
    }

    public void addContact(WelfareContact contact) {
        contacts.add(contact);
        contact.setWelfareService(this);
    }

    public void addLink(WelfareLink link) {
        links.add(link);
        link.setWelfareService(this);
    }

    public void addForm(WelfareForm form) {
        forms.add(form);
        form.setWelfareService(this);
    }

    public void addLaw(WelfareLaw law) {
        laws.add(law);
        law.setWelfareService(this);
    }
}
