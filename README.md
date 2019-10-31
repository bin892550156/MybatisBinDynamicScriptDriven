这个一个基于Mybatis的驱动框架，该框架专门用于处理自主研发的Bin SQL，以更加方便快捷的方式实现Mybatis的注解的动态SQL.该框架基于Mybatis的默认驱动`XMLLanguageDriver`进行扩展的，保留着`XMLLanguageDriver`的原有功能和特性。

#什么是 BIN SQL
`select * from user where  [IS_NOT_EMPTY_name&&age: name=#{name}]`
这个就是Bin SQL ,` [IS_NOT_EMPTY_name&&age: name=#{name}]`这个就是Bin SQL 表达式.
1. `[IS_NOT_EMPTY` 表示 Bin SQL 表达式前缀 ，不同的Bin SQL 表达式前缀对应不同的业务功能。这个对个字符串取名为`binKey`。
2. `name&&age` 表示 属性表达式，供给Bin KEY的业务功能做对应的业务处理。这里对该字符串取名为`propertyExpression`。
3. `name=#{name}` 一般表示属性表达式有效时，返回的SQL碎片。这里对该字符串取名为`resolvedSqlFrag`。
最终，经过框架的对Bin SQL处理后，可得到:
 -- 如果 'name'和'age' 都不为null，且不为空字符串时，可得到 `select * from user where name=#{name}`.
 -- 如果 'name'或者'age'为null，或者其中一个是空字符串，可以得到 `select * from user `

#Bin SQL 表达式介绍
1. `[IS_NOT_EMPTY` 如果`propertyExpression`的属性不为null；如果属性是字符串时判断不为空字符串；如果属性为Collection对象或者数组对象不是空集合或空数组，则条件成立，返回`resolvedSqlFrag`。否则返回空字符串。 该表达式支持 '&' 和 '|' 的运算。
示例：
`select * from user where  [IS_NOT_EMPTY_name&&age: name=#{name}]`  
`select * from user where [IS_NOT_EMPTY_name||age: name=#{name}]` 
`select * from user where [IS_NOT_EMPTY_name:name=#{name}]`
条件成立时，可得到 `select * from user where name=#{name}` ； 否则，得到 `select * from user `
2. `[IS_EMPTY` 如果`propertyExpression`的属性为null;如果属性是字符串时判断为空字符串；如果属性为Collection对象或者数组对象判断是空集合或空数组，则条件成立，返回`resolvedSqlFrag`。否则返回空字符串。该表达式支持 '&' 和 '|' 的运算。
示例：
`select * from user where  [IS_EMPTY_name&&age: name=#{name}]`  
`select * from user where [IS_EMPTY_name||age: name=#{name}]` 
`select * from user where [IS_EMPTY_name:name=#{name}]`
条件成立时，可得到 `select * from user where name=#{name}` ； 否则，得到 `select * from user `
3. `{IS_NOT_NULL` 如果`propertyExpression`的属性不为null，则条件成立返回`resolvedSqlFrag`。否则返回空字符串。该表达式支持 '&' 和 '|' 的运算。
示例：
`select * from user where [IS_NOT_NULL_name&&age: name like #{name} and age = #{age}] `
`select * from user where [IS_NOT_NULL_name||age: name like #{name} or age = #{age}]`
条件成立时，可得到`select * from user where name like #{name} and age = #{age}` ; 否则，得到 `select * from user `
4. `[IS_NULL` 如果`propertyExpression`的属性为null，则条件成立返回`resolvedSqlFrag`，否则返回空字符串。该表达式支持 '&' 和 '|' 的运算。
示例：
`select * from user where [IS_NULL_name||age: name like #{name} or age = #{age} ]`
`select * from user where [IS_NULL_name&&age: name like #{name} or age = #{age} ]`
条件成立时，可得到`select * from user where name like #{name} and age = #{age}` ; 否则，得到 `select * from user `
5. `[IF` 将`propertyExpression`看作EL表达式，如果EL表达式的执行结果为ture，就返回`resolvedSqlFrag`,否则返回空字符串。如果 `propertyExpression` 是 
 `[IS_NOT_EMPT 属性 ` 形式的表达式，会自动转成 `属性名 !=null and 属性名!='' ` 形式的表达式。
示例：
`select * from user where [IF_name!=null&&name!='': age = #{age} ]`
`select * from user where [IF_IS_NOT_EMPTY name: age=#{age}]`
条件成立时，可得到`select * from user where age = #{age}` ; 否则，得到 `select * from user `
6. `[IN` 这时`propertyExpression` 整个字符串会被看作一个属性名。对`resolvedSqlFrag` 进行修整。
示例：
`select * from user where [IN_ids: id in (#{id},)]`
条件成立时，可得到`select * from user where id in (#{id1},#{id2},#{id3})` ; 否则，得到 `select * from user `
7. `[CASE` 这时`propertyExpression` 整个字符串会被看作一个属性名。`resolvedSqlFrag`会被解析成 key=`属性值`,value=`SQL 碎片` 的映射。然后取出属性名对应的值，然后在映射中找到与属性值对应的SQL碎片。相当于Java的`switch`关键字用法。
示例：
`select * from user where [CASE_testCase:czb@name=#{testCase},1@name='xiaoHong',3@age=90,ELSE@name=#{name}]`
可得到：
  -- 如果testCase为czb，可得到` select * from user where name=#{testCase} `。
  -- 如果testCase为1，可得到 ` select * from user where name = 'xiaoHong'`。
  -- 如果testCase为3，可得到 ` select * from user where age=90`。
  -- 如果testCase为其他值，可得到 `select * from user name=#{name} `。
8. `[INSERT` 该Bin SQL表达式是用于生成INSERT脚本的。这时`propertyExpression`应该为空字符串；而`resolvedSqlFrag`变成反射属性的过滤条件，格式为`EL表达式@条件成立后引用的属性名`，多个条件用','隔开。生成的INSERT脚本是非常依赖命名规范的，驼峰命名的属性，会被转换成下划线形式的列名。而类名，会被当做表名（转换成下换线形式），当然可以通过`@BinMappingTable`去指定表名。而不想被映射的属性，也可以使用`@BinIgnoreMapping`来标记，生成时会自动忽略被该注解标记住的属性。
示例：`[INSERT:age > 30@age,name != ''@name]`
再看实体类：
```java
@BinMappingTable
public class User {
    private int id;
    private String name;
    private String deptId;
    @BinIgnoreMapping
    private String phone;
    @BinIgnoreMapping
    private String website;
    private String desName;
    private Integer age;
```
可得到
  --如果`age>30`成立，但`name!=''`不成立，可得到`INSERT INTO user ( dept_id,id,age,des_name) values ( #{deptId},#{id},#{age},#{desName})`
  --如果`name!=''`成立,且`age>30`成立，可得到`INSERT INTO user ( name,dept_id,id,age,des_name) values ( #{name},#{deptId},#{id},#{age},#{desName})`
  --如果`age>30`和`name!=''`都不成立，可得到`NSERT INTO user ( dept_id,id,des_name) values ( ?,?,?) `

9. ` [INSERT_NOT_NULL`  与`[INSERT`类似，但对每个属性加上的不能为null的过滤，如果配置了`resovledSqlFrag` ,会使用`resovledSqlFrag`对应属性的过滤条件而不使用该表达式原有的业务过滤。
示例：`[INSERT_NOT_NULL:name!=''@name]`
可得到：
    --如果`age为null`而`name=''`,可得到`INSERT INTO user ( dept_id,id,des_name) values ( #{deptId},#{id},#{desName})`
    -- 如果`age为null`,而`name='1'`,可得到`INSERT INTO user ( name,dept_id,id,des_name) values ( #{name},#{deptId},#{id},#{desName})`

10. `[INSERT_NOT_EMPTY` 与`[INSERT`类似，但对每个属性加上不为null且如果是字符串类型不能为空字符串的过滤，如果配置了`resovledSqlFrag` ,会使用`resovledSqlFrag`对应属性的过滤条件而不使用该表达式原有的业务过滤。
示例：`[INSERT_NOT_EMPTY:age>30@age]`
可得到：
  --如果`age>30` 且`descName=''`,可得到 ` INSERT INTO user ( name,dept_id,id,age) values ( #{name},#{deptId},#{id},#{age})`
 --如果`age<30`  且`descName=''`,可得到 `INSERT INTO user ( name,dept_id,id,age) values ( ?,?,?,?)  `

11. `[INSERT_FOR_EACH_users]`：这个是针对MySQL的批量插入所做出来的Bin SQL 表达是，目前仅支持`@BinIgnoreMapping`注解过滤，而`resovledSqlFrag`的条件过滤尚未支持。该BIN SQL 表达需要设置`PropertyExpression`去指定参数名。
示例：`[INSERT_FOR_EACH_users:]`
可得到：`INSERT INTO user ( name,dept_id,id,age,des_name ) VALUES (#{name0},#{deptId0},#{id0},#{age0},#{desName0}),(#{name1},#{deptId1},#{id1},#{age1},#{desName1}),(#{name2},#{deptId2},#{id2},#{age2},#{desName2}),(#{name3},#{deptId3},#{id3},#{age3},#{desName3}),(#{name4},#{deptId4},#{id4},#{age4},#{desName4})`

12. `[UPDATE` 该Bin SQL表达式是用于生成INSERT脚本的。这时`propertyExpression`应该为空字符串；而`resolvedSqlFrag`变成反射属性的过滤条件，格式为`EL表达式@条件成立后引用的属性名`，多个条件用','隔开。生成的INSERT脚本是非常依赖命名规范的，驼峰命名的属性，会被转换成下划线形式的列名。而类名，会被当做表名（转换成下换线形式），当然可以通过`@BinMappingTable`去指定表名。而不想被映射的属性，也可以使用`@BinIgnoreMapping`来标记，生成时会自动忽略被该注解标记住的属性。另外修改SQL的`where`条件可以通过`resolvedSqlFrag`进行配置。
示例：`[UPDATE:age>#{age}@where]`
可得到：` UPDATE user SET  name = #{name},dept_id = #{deptId},id = #{id},age = #{age},des_name = #{desName} WHERE age>#{age}`

13. `[UPDATE_NOT_NULL` 与`[UPDATE`类似，但对每个属性加上的不能为null的过滤，如果配置了resovledSqlFrag ,会使用resovledSqlFrag对应属性的过滤条件而不使用该表达式原有的业务过滤。
示例：`[UPDATE_NOT_NULL:id==1@id,id = #{id}@where ]`
可得到：
  -- 如果`id==1`成立，可得到：` UPDATE user SET  name = #{name},dept_id = #{deptId},id = #{id},des_name = #{desName} WHERE id = #{id}`
  -- 如果`id==1`不成立，可得到：`UPDATE user SET  name = #{name},dept_id = #{deptId},des_name = #{desName} WHERE id = #{id}`

14.` [UPDATE_NOT_EMPTY` 与`[UPDATE`类似，但对每个属性加上不为null且如果是字符串类型不能为空字符串的过滤，如果配置了resovledSqlFrag ,会使用resovledSqlFrag对应属性的过滤条件而不使用该表达式原有的业务过滤。
示例：` [UPDATE_NOT_EMPTY:id==1@id,id = #{id}@where ]`
可得到：
  --如果`id==1`成立，且`desName==''`可得到：`UPDATE user SET  name = #{name},dept_id = #{deptId},id = #{id} WHERE id = #{id}`
  -- 如果`id==1`不成立，而`descName==‘’`可得到：`UPDATE user SET  name = #{name},dept_id = #{deptId} WHERE id = #{id}`

##示例代码：
####UserMapper
```java
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
```
####MybatisBinMainTest
```java
package bin.common;


import bin.common.bean.User;
import bin.common.config.MyBatisBinConfig;
import bin.common.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

public class MybatisBinMainTest {

    private UserMapper userMapper;
    private SqlSession sqlSession;

    @Before
    public void init(){
        sqlSession=MyBatisBinConfig.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @After
    public void close(){
        sqlSession.close();
    }

    @Test
    public void test_selectUserListCase(){
        List<User> users = userMapper.selectUserListCase("222","ZB");
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIn(){
        List<User> users = userMapper.selectUserListIn(Arrays.asList("1", "2"));
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNotEmptyAnd(){
        List<User> users = userMapper.selectUserListIsNotEmptyAnd("czb", "");
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNotEmptyOr(){
        List<User> users = userMapper.selectUserListIsNotEmptyOr("czb", "");
        System.out.println(users);
    }

    @Test
    public void test_selecUserListIsEmptyAnd(){
        List<User> users=userMapper.selecUserListIsEmptyAnd(null,1);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsEmptyOr(){
        List<User> users=userMapper.selectUserListIsEmptyOr("null",90);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNotNullAnd(){
        List<User> users = userMapper.selectUserListIsNotNullAnd("czb", 10);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNotNullOr(){
        List<User> users = userMapper.selectUserListIsNotNullOr("czb", null);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNullOr(){
        List<User> users = userMapper.selectUserListIsNullOr("czb", null);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListIsNullAnd(){
        List<User> users=userMapper.selectUserListIsNullAnd(null,null);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListTest(){
        List<User> users = userMapper.selectUserListTest("czb", 90);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListUser(){
        User user=new User();
        user.setName("czb");
        user.setAge(10);
        List<User> users = userMapper.selectUserListUser(user);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListUserAnd(){
        User user=new User();
//        user.setName("czb");
//        user.setAge(10);
        List<User> users = userMapper.selectUserListUserAnd(user);
        System.out.println(users);
    }

    @Test
    public void test_selectUserListUserOrIn(){
        List<User> users=new ArrayList<>(5);
        IntStream.range(0,5).forEach(i->{
            User user=new User();
            user.setId(i);
            users.add(user);
        });
        List<User> userList=userMapper.selectUserListUserOrIn(null,users);
        System.out.println(userList);
    }

    @Test
    public void test_selectUserListUserIn(){
        List<User> users=new ArrayList<>(5);
        IntStream.range(0,5).forEach(i->{
            User user=new User();
            user.setId(i);
            users.add(user);
        });
        List<User> userList=userMapper.selectUserListUserIn(users);
        System.out.println(userList);
    }

    @Test
    public void test_selectUserListUserCaseIn(){
        List<User> users=new ArrayList<>(5);
        IntStream.range(0,5).forEach(i->{
            User user=new User();
            user.setId(i);
            users.add(user);
        });
        User user=new User();
        user.setAge(2);
        List<User> userList=userMapper.selectUserListUserCaseIn(user,users);
        System.out.println(userList);
    }

    @Test
    public void test_selectUserListLeftJoin(){
        User user=new User();
        user.setName("czb");
        List<Map<String, Object>> maps = userMapper.selectUserListLeftJoin(user);
        System.out.println(maps);
    }

    @Test
    public void test_insert(){
        User user=generateUser();
        user.setAge(20);
        user.setName("");
        boolean insert = userMapper.insert(user);
        Assert.assertEquals(true,insert);
    }

    @Test
    public void test_insertNotNull(){
        User user=generateUser();
        user.setAge(null);
        user.setName("12");
        boolean insert = userMapper.insertNotNull(user);
        Assert.assertEquals(true,insert);
    }

    @Test
    public void test_insertNotEmpty(){
        User user=generateUser();
        user.setDesName("");
        user.setAge(33);
        boolean insert = userMapper.insertNotEmpty(user);
        Assert.assertEquals(true,insert);
    }

    @Test
    public void test_insertList(){
        List<User> users=new ArrayList<>();
        IntStream.range(20,25).forEach(i->{
            User user=new User();
            user.setId(i);
            user.setName("name_"+i);
            users.add(user);
        });
        boolean b = userMapper.insertList(users);
        Assert.assertEquals(true,b);
    }

    @Test
    public void test_update(){
        User user=generateUser();
        user.setId(2);
        user.setAge(null);
        boolean update = userMapper.update(user);
        Assert.assertEquals(false,update);
    }

    @Test
    public void test_updateNotNull(){
        User user=generateUser();
        user.setId(2);
        user.setAge(null);
        boolean insert = userMapper.updateNotNull(user);
        Assert.assertEquals(true,insert);
    }

    @Test
    public void test_updateNotEmpty(){
        User user=generateUser();
        user.setId(2);
        user.setDesName("");
        user.setAge(null);
        boolean insert = userMapper.updateNotEmpty(user);
        Assert.assertEquals(true,insert);
    }

    public User generateUser(){
        Random random=new Random();
        User user=new User();
        int i=random.nextInt(100);
        user.setName("c"+i);
        user.setAge(i);
        user.setId(i);
        user.setDesName("desc_"+i);
        user.setDeptId("1");
        return user;
    }
}
```
##注意事项
1. 目前框架暂时不支持嵌套形式的Bin SQL。
2. 除了`INSERT`和`UPDATE`的相关BIN SQL 表达式不支持放在任何SQL脚本意味，其他BIN SQL表达式都可适用于任何的SQL脚本中。
3. 该框架兼容Mybatis的默认驱动`XMLLanguageDriver`的所有特性。只有SQL脚本中出现了`[`的字符才会被识别成BIN SQL。
##项目地址

