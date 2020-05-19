package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.ServerConnectionEntity;
import com.jiin.admin.entity.ServerRelationEntity;
import com.jiin.admin.entity.ServicePortEntity;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.ServerConnectionModel;
import com.jiin.admin.website.model.ServerRelationModel;
import com.jiin.admin.website.model.ServicePortModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@BaseMapper
public interface ServiceMapper {
    @Insert("INSERT INTO _SERVER_CONNECTION(ID, KEY, TITLE, TYPE, IP_ADDRESS, PORT, USERNAME, PASSWORD) VALUES(#{id}, #{key}, #{title}, #{type}, #{ipAddress}, #{port}, #{username}, #{password})")
    @SelectKey(statement="SELECT NEXTVAL('SERVER_CONNECTION_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertServerConnectionWithModel(ServerConnectionModel serverConnectionModel);

    @Insert("INSERT INTO _SERVER_RELATION(ID, MAIN_SVR_ID, SUB_SVR_ID) VALUES(#{id}, #{mainSvrId}, #{subSvrId})")
    @SelectKey(statement="SELECT NEXTVAL('SERVER_RELATION_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertServerRelationWithModel(ServerRelationModel serverRelationModel);

    @Insert("INSERT INTO _SERVICE_PORT(ID, SVR_ID, POSTGRE_SQL_PORT, WATCHDOG_PORT, WATCHDOG_HB_PORT, PCP_PROCESS_PORT, PG_POOL_2_PORT, ADMIN_SERVER_PORT, MAP_PROXY_PORT, MAP_SERVER_PORT, VECTOR_TILE_PORT, JIIN_HEIGHT_PORT, LOS_PORT, MINIO_PORT, MAPNIK_PORT, SYNCTHING_TCP_PORT, SYNCTHING_UDP_PORT) " +
            "VALUES(#{id}, #{svrId}, #{postgreSQLPort}, #{watchdogPort}, #{watchdogHbPort}, #{pcpProcessPort}, #{pgPool2Port}, #{adminServerPort}, #{mapProxyPort}, #{mapServerPort}, #{vectorTilePort}, #{jiinHeightPort}, #{losPort}, #{minioPort}, #{mapnikPort}, #{syncthingTcpPort}, #{syncthingUdpPort})")
    @SelectKey(statement="SELECT NEXTVAL('SERVICE_PORT_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertServicePortWithModel(ServicePortModel servicePortModel);

    @Update("UPDATE _SERVER_CONNECTION SET TITLE = #{title}, TYPE = #{type}, IP_ADDRESS = #{ipAddress}, PORT = #{port}, USERNAME = #{username}, PASSWORD = #{password} WHERE KEY = #{key}")
    void updateServerConnectionWithModel(ServerConnectionModel serverConnectionModel);

    @Update("UPDATE _SERVICE_PORT SET POSTGRE_SQL_PORT = #{postgreSQLPort}, WATCHDOG_PORT = #{watchdogPort}, WATCHDOG_HB_PORT = #{watchdogHbPort}, PCP_PROCESS_PORT = #{pcpProcessPort}, PG_POOL_2_PORT = #{pgPool2Port}, ADMIN_SERVER_PORT = #{adminServerPort}, MAP_PROXY_PORT = #{mapProxyPort}, MAP_SERVER_PORT = #{mapServerPort}, VECTOR_TILE_PORT = #{vectorTilePort}, JIIN_HEIGHT_PORT = #{jiinHeightPort}, LOS_PORT = #{losPort}, MINIO_PORT = #{minioPort}, MAPNIK_PORT = #{mapnikPort}, SYNCTHING_TCP_PORT = #{syncthingTcpPort}, SYNCTHING_UDP_PORT = #{syncthingUdpPort} WHERE SVR_ID = #{svrId}")
    void updateServicePortWithModel(ServicePortModel servicePortModel);

    @Select("SELECT * FROM _SERVER_CONNECTION WHERE KEY = #{key}")
    ServerConnectionEntity findServerConnectionByName(@Param("key") String key);

    @Select("SELECT * FROM _SERVER_CONNECTION ORDER BY ID DESC LIMIT 1")
    ServerConnectionEntity findRecentlyInsertConnectionEntity();

    @Select("SELECT * FROM _SERVER_CONNECTION WHERE TYPE = #{type}")
    List<ServerConnectionEntity> findServerConnectionByType(@Param("type") String type);

    @Select("SELECT * FROM _SERVER_CONNECTION WHERE IP_ADDRESS = #{ipAddress}")
    ServerConnectionEntity findServerConnectionsByIpAddress(@Param("ipAddress") String ipAddress);

    @Select("SELECT COUNT(*) FROM _SERVER_RELATION WHERE MAIN_SVR_ID = #{mainSvrId} AND SUB_SVR_ID = #{subSvrId}")
    long countServerRelationByEachSvrIds(@Param("mainSvrId") long mainSvrId, @Param("subSvrId") long subSvrId);

    @Select({
            "SELECT SC.* ",
            "FROM _SERVER_RELATION SR LEFT JOIN _SERVER_CONNECTION MC ",
            "ON SR.MAIN_SVR_ID = MC.id ",
            "LEFT JOIN _SERVER_CONNECTION SC ",
            "ON SR.SUB_SVR_ID = SC.id ",
            "WHERE MC.IP_ADDRESS = #{ipAddress}"
    })
    List<ServerConnectionEntity> findOwnRelateConnectionsByIpAddress(@Param("ipAddress") String ipAddress);

    @Select("SELECT * FROM _SERVER_RELATION")
    List<ServerRelationEntity> findAllServerRelations();

    @Select("SELECT * FROM _SERVICE_PORT WHERE SVR_ID = #{svrId}")
    ServicePortEntity findPortConfigBySvrId(@Param("svrId") long svrId);

    @Update("TRUNCATE TABLE ${table} CASCADE")
    void truncateTableWithName(@Param("table") String table);

    @Delete("DELETE FROM _SERVER_CONNECTION WHERE ID = #{id}")
    void deleteServerConnection(@Param("id") long id);

    @Delete("DELETE FROM _SERVICE_PORT WHERE SVR_ID = #{svrId}")
    void deleteServicePortBySvrId(@Param("svrId") long svrId);

    @Delete("DELETE FROM _SERVER_RELATION WHERE MAIN_SVR_ID = #{id} OR SUB_SVR_ID = #{id}")
    void deleteServerRelationBySvrId(@Param("id") long id);
}
