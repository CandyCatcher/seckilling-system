package top.candyboy;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import top.candyboy.pojo.User;

@Component
@Mapper
public interface UserDao {
    //bigint映射的是long
    @Select("select * from seckill_user where id = #{id}")
    public User getById(@Param("id") Long id);

    @Insert("insert into seckill_user(id, nickname)values(#{id}, #{nickname})")
    public void insert(User user);

    @Update("update seckill_user set password = #{password} where id = #{id}")
    public void updatePassword(User user);
}
