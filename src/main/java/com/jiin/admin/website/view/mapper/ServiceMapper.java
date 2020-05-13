package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.ServerConnectionEntity;
import com.jiin.admin.entity.ServerRelationEntity;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.ServerConnectionModel;
import com.jiin.admin.website.model.ServerRelationModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@BaseMapper
public interface ServiceMapper {
    @Insert("INSERT INTO _SERVER_CONNECTION(ID, KEY, TITLE, TYPE, IP_ADDRESS, USERNAME, PASSWORD) VALUES(#{id}, #{key}, #{title}, #{type}, #{ipAddress}, #{username}, #{password})")
    @SelectKey(statement="SELECT NEXTVAL('SERVER_CONNECTION_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertServerConnectionWithModel(ServerConnectionModel serverConnectionModel);

    @Insert("INSERT INTO _SERVER_RELATION(ID, MAIN_SVR_ID, SUB_SVR_ID) VALUES(#{id}, #{mainSvrId}, #{subSvrId})")
    @SelectKey(statement="SELECT NEXTVAL('SERVER_RELATION_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertServerRelationWithModel(ServerRelationModel serverRelationModel);

    @Select("SELECT * FROM _SERVER_CONNECTION WHERE KEY = #{key}")
    ServerConnectionEntity findServerConnectionByName(@Param("key") String key);

    @Select("SELECT * FROM _SERVER_CONNECTION WHERE IP_ADDRESS = #{ipAddress}")
    ServerConnectionEntity findServerConnectionsByIpAddress(@Param("ipAddress") String ipAddress);

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
}
