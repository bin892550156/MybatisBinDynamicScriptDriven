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
