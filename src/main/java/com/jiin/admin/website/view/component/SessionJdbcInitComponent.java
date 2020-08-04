package com.jiin.admin.website.view.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SessionJdbcInitComponent {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Spring Session 와 관련된 SQL 문단은 Dependencies 안의 SQL 문단을 참고할 것.
    private static final String SPRING_SESSION_DDL =
        "CREATE TABLE SPRING_SESSION ( " +
            "PRIMARY_ID CHAR(36) NOT NULL, " +
            "SESSION_ID CHAR(36) NOT NULL, " +
            "CREATION_TIME BIGINT NOT NULL, " +
            "LAST_ACCESS_TIME BIGINT NOT NULL, " +
            "MAX_INACTIVE_INTERVAL INT NOT NULL, " +
            "EXPIRY_TIME BIGINT NOT NULL, " +
            "PRINCIPAL_NAME VARCHAR(100), " +
            "CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)" +
        ")";

    private static final String SPRING_SESSION_IX1_DDL = "CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID)";

    private static final String SPRING_SESSION_IX2_DDL = "CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME)";

    private static final String SPRING_SESSION_IX3_DDL = "CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME)";

    private static final String SPRING_SESSION_ATTRIBUTES_DDL =
        "CREATE TABLE SPRING_SESSION_ATTRIBUTES ( " +
            "SESSION_PRIMARY_ID CHAR(36) NOT NULL, " +
            "ATTRIBUTE_NAME VARCHAR(200) NOT NULL, " +
            "ATTRIBUTE_BYTES BYTEA NOT NULL, " +
            "CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME), " +
            "CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE" +
        ")";

    public void initializeSession(){
        DataSource dataSource = (DataSource) jdbcTemplate.getDataSource();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet res = metaData.getTables("", "", "spring_session", new String[] { "TABLE" });
            if (!res.next()) {
                jdbcTemplate.execute(SPRING_SESSION_DDL);
            }

            res = metaData.getIndexInfo("", "", "spring_session", false, false);
            if (!res.next()) {
                jdbcTemplate.execute(SPRING_SESSION_IX1_DDL);
                jdbcTemplate.execute(SPRING_SESSION_IX2_DDL);
                jdbcTemplate.execute(SPRING_SESSION_IX3_DDL);
            }

            res = metaData.getTables("", "", "spring_session_attributes", new String[] { "TABLE" });
            if(!res.next()){
                jdbcTemplate.execute(SPRING_SESSION_ATTRIBUTES_DDL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
