package com.authorizationserver.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "credential")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Builder
public class Credential implements Serializable {

    private static final long serialVersionUID = 4896855634323645583L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Authority> authorities;

}
