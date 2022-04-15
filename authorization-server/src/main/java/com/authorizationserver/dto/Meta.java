package com.authorizationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class Meta {

    private String version;

    private String server;

    private Integer offset;

    private Integer limit;

    private Integer recordCount;

    private Long totalRecords;

}
