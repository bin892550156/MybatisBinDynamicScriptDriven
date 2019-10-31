package bin.common.mapper;

import bin.common.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    @Select("select * from user where  [IS_NOT_EMPTY_name&&age: name=#{name}] ")
    List<User> selectUserListIsNotEmptyAnd(@Param("name") String name, @Param("age") String age);

    @Select("select * from user where [IS_NOT_EMPTY_name||age: name=#{name}] ")
    List<User> selectUserListIsNotEmptyOr(@Param("name") String name, @Param("age") String age);

    @Select("select * from user where [IS_EMPTY_name&age: name='czb'] ")
    List<User> selecUserListIsEmptyAnd(@Param("name") String name,@Param("age") Integer age);

    @Select("select * from user where [IS_EMPTY_name|age: age=#{age}] ")
    List<User> selectUserListIsEmptyOr(@Param("name") String name,@Param("age") Integer age);

    @Select({"select * from user where [IS_NOT_NULL_name&&age: name like #{name} and age = #{age} ]"})
    List<User> selectUserListIsNotNullAnd(@Param("name") String name,@Param("age") Integer age);

    @Select({"select * from user where [IS_NOT_NULL_name||age: name like #{name} or age = #{age} ]"})
    List<User> selectUserListIsNotNullOr(@Param("name") String name,@Param("age") Integer age);

    @Select({"select * from user where [IS_NULL_name||age: name like #{name} or age = #{age} ]"})
    List<User> selectUserListIsNullOr(@Param("name") String name,@Param("age") Integer age);

    @Select({"select * from user where [IS_NULL_name&&age: name like #{name} or age = #{age} ]"})
    List<User> selectUserListIsNullAnd(@Param("name") String name,@Param("age") Integer age);

    @Select({"select * from user where [IF_name!=null&&name!='': age = #{age} ]"})
    List<User> selectUserListTest(@Param("name") String name,@Param("age") Integer age);

    @Select("select * from user where [IN_ids: id in (#{id},)]")
    List<User> selectUserListIn(@Param("ids") List<String> ids);

        @Select("select * from user where [CASE_testCase:czb@name=#{testCase},1@name='xiaoHong',3@age=90,ELSE@name=#{name}]")
    List<User> selectUserListCase(@Param("testCase") String testCase,@Param("name") String name);

    @Select("select * from user where [IS_NOT_NULL_user.age: and age=#{user.age}] [IS_NOT_EMPTY_user.name: and name = #{user.name}]")
    List<User> selectUserListUser(@Param("user") User user);

    @Select("select * from user where [IS_NOT_NULL_user.age&user.name: and age=#{user.age}] [IS_NOT_EMPTY_user.name: and name = #{user.name}]")
    List<User> selectUserListUserAnd(@Param("user") User user);

    @Select("select * from user where [IS_NOT_NULL_user.name: and name='czb'] [IN_users: id in (#{user.id},)]")
    List<User> selectUserListUserOrIn(@Param("user") User user,@Param("users")List<User> users);

    @Select("select * from user where [CASE_user.age:90@and age = 90 ,else@and age = 1 ] [IN_users: and id in (#{user.id},)]")
    List<User> selectUserListUserCaseIn(@Param("user") User user,@Param("users")List<User> users);

    @Select("select * from user where [IN_users:id in (#{user.id},)]")
    List<User> selectUserListUserIn(@Param("users")List<User> users);

    @Select({
            "select u.id,u.name,u.age,u.dept_id,u.des_name,d.name dept_name",
            "from user u ",
            "left join dept d on d.id=u.dept_id ",
            "where ",
            "[IS_NOT_EMPTY_user.name: and u.name=#{user.name}]"
    })
    List<Map<String,Object>> selectUserListLeftJoin(@Param("user") User user);

    @Insert("[INSERT:age > 30@age,name != ''@name]")
    boolean insert(User user);

    @Insert("[INSERT_NOT_NULL:name!=''@name]")
    boolean insertNotNull(User user);

    @Insert("[INSERT_NOT_EMPTY:age>30@age]")
    boolean insertNotEmpty(User user);

    @Insert("[INSERT_FOR_EACH_users:]")
    boolean insertList(@Param("users") List<User> users);

    @Update("[UPDATE:id==1@id,age>#{age}@where]")
    boolean update(User user);

    @Update("[UPDATE_NOT_NULL:id==1@id,id = #{id}@where ]")
    boolean updateNotNull(User user);

    @Update("[UPDATE_NOT_EMPTY:id==1@id,id = #{id}@where ]")
    boolean updateNotEmpty(User user);
}
