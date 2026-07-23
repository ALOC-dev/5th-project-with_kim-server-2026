package com.with_kim.aloc_study.infrastructure;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeoulRealEstateApiResponse {

    @JsonProperty("tbLnOpendataRtmsV")
    private Body body;

    public List<Row> rows() {
        return body == null || body.row == null ? List.of() : body.row;
    }

    public int totalCount() {
        return body == null ? 0 : body.listTotalCount;
    }

    public String resultCode() {
        return body == null || body.result == null ? null : body.result.code;
    }

    public String resultMessage() {
        return body == null || body.result == null ? null : body.result.message;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Row> row = new ArrayList<>();
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Row {
        @JsonProperty("RCPT_YR")
        private String receiptYear;

        @JsonProperty("CGG_CD")
        private String cggCode;

        @JsonProperty("CGG_NM")
        private String cggName;

        @JsonProperty("STDG_CD")
        private String stdgCode;

        @JsonProperty("STDG_NM")
        private String stdgName;

        @JsonProperty("LOTNO_SE")
        private String lotNoType;

        @JsonProperty("LOTNO_SE_NM")
        private String lotNoTypeName;

        @JsonProperty("MNO")
        private String mainLotNumber;

        @JsonProperty("SNO")
        private String subLotNumber;

        @JsonProperty("BLDG_NM")
        private String buildingName;

        @JsonProperty("CTRT_DAY")
        private String contractDay;

        @JsonProperty("THING_AMT")
        private String amount;

        @JsonProperty("ARCH_AREA")
        private String buildingArea;

        @JsonProperty("LAND_AREA")
        private String landArea;

        @JsonProperty("FLR")
        private String floor;

        @JsonProperty("RGHT_SE")
        private String rightType;

        @JsonProperty("RTRCN_DAY")
        private String cancellationDay;

        @JsonProperty("ARCH_YR")
        private String constructionYear;

        @JsonProperty("BLDG_USG")
        private String buildingUsage;

        @JsonProperty("DCLR_SE")
        private String declarationType;

        @JsonProperty("OPBIZ_RESTAGNT_SGG_NM")
        private String agentSggName;
    }
}
