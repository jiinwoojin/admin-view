package com.jiin.admin.website.view.mapper;

import com.jiin.admin.dto.AccountDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.entity.RoleEntity;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@BaseMapper
public interface AccountMapper {
    @Select("SELECT a.*, r.* FROM _ACCOUNT a LEFT JOIN _ROLE r ON a.role_id = r.id")
    @Results(id = "AccountEntity", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "name", column = "name"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", javaType = RoleEntity.class, column = "role_id", one = @One(select = "findRoleById"))
    })
    List<AccountEntity> findAllAccounts();

    @Select("SELECT a.*, r.* FROM _ACCOUNT a LEFT JOIN _ROLE r ON a.role_id = r.id WHERE USERNAME = #{username}")
    @ResultMap("AccountEntity")
    AccountEntity findAccountByUsername(@Param("username") String username);

    @Select("SELECT * FROM _ROLE")
    List<RoleEntity> findAllRoles();

    @Select("SELECT * FROM _ROLE WHERE id = #{id}")
    RoleEntity findRoleById(@Param("id") long id);

    @Select("SELECT * FROM _ROLE WHERE TITLE = #{title}")
    RoleEntity findRoleByTitle(@Param("title") String title);

    @Insert("INSERT INTO _ACCOUNT(ID, USERNAME, EMAIL, NAME, PASSWORD, ROLE_ID) VALUES(#{id}, #{username}, #{email}, #{name}, #{password}, #{roleId})")
    @SelectKey(statement="SELECT NEXTVAL('ACCOUNT_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertAccount(AccountDTO AccountDTO);

    @Insert("INSERT INTO _ROLE(ID, TITLE, LABEL, ACCOUNT_MANAGE, CACHE_MANAGE, LAYER_MANAGE, MAP_BASIC, MAP_MANAGE) VALUES(#{id}, #{title}, #{label}, #{accountManage}, #{cacheManage}, #{layerManage}, #{mapBasic}, #{mapManage})")
    @SelectKey(statement="SELECT NEXTVAL('ROLE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertRole(RoleEntity roleEntity);

    @Update("UPDATE _ACCOUNT SET PASSWORD = #{password}, NAME = #{name}, EMAIL = #{email} WHERE username = #{username}")
    void updateAccount(AccountDTO accountDTO);

    @Update("UPDATE _ACCOUNT SET ROLE_ID = #{roleId} WHERE USERNAME = #{username}")
    void updateAccountRoleWithUsernameAndRoleId(@Param("username") String username, @Param("roleId") long roleId);

    @Delete("DELETE FROM _ACCOUNT WHERE USERNAME = #{username}")
    void deleteAccountByUsername(@Param("username") String username);
}
