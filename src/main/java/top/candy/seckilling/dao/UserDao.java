package top.candy.seckilling.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import top.candy.seckilling.pojo.User;

@Component
@Mapper
public interface UserDao {
    //bigint映射的是long
    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") Long id);

    @Insert("insert into user(id, nickname)values(#{id}, #{nickname})")
    public void insert(User user);

    @Update("update user set password = #{password} where id = #{id}")
    public void updatePassword(User user);
}
