package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "_SERVER_RELATION")
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames = { "MAIN_SVR_ID", "SUB_SVR_ID" })
})
@SequenceGenerator(
        name = "SERVER_RELATION_SEQ_GEN",
        sequenceName = "SERVER_RELATION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class ServerRelationEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SERVER_RELATION_SEQ_GEN"
    )
    private Long id;

    @ManyToOne
    @JoinColumn
    private ServerConnectionEntity mainSvr;

    @ManyToOne
    @JoinColumn
    private ServerConnectionEntity subSvr;
}
