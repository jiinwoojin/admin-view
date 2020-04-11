package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "_PROXY_CACHE_SOURCE_RELATION")
@SequenceGenerator(
        name = "PROXY_CACHE_SOURCE_SEQ_GEN",
        sequenceName = "PROXY_CACHE_SOURCE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = { "CACHE_ID", "SOURCE_ID" })
})
@Getter
@Setter
public class ProxyCacheSourceRelationEntity implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_CACHE_SOURCE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private ProxyCacheEntity cache;

    @ManyToOne
    @JoinColumn
    private ProxySourceEntity source;
}
