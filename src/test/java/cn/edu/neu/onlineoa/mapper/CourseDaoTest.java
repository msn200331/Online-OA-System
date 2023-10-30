package cn.edu.neu.onlineoa.mapper;

import cn.edu.neu.onlineoa.bean.Course;
import cn.edu.neu.onlineoa.utils.MybatisUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.lang.Float.NaN;
import static org.junit.Assert.*;


public class CourseDaoTest {

    SqlSessionFactory sqlSessionFactory = MybatisUtils.getSessionFactoryInstance();


    CourseDao courseDao;

    @Before
    public void init() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        courseDao = sqlSession.getMapper(CourseDao.class);
    }

    @Test
    public void findAllCourse() {
        List<Course> courseList = courseDao.findAllCourse();
        assertNotNull(courseList);
        assertEquals(15, courseList.size());
        for ( Course c : courseList ) {
            System.out.println(c);
        }
    }

    @Test
    public void findCourseByCid() {
        Course result1 = courseDao.findCourseByCid(null);
        Course result2 = courseDao.findCourseByCid("");
        Course result3 = courseDao.findCourseByCid("A00001");
        Course result4 = courseDao.findCourseByCid("A000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001");
        Course result5 = courseDao.findCourseByCid("''; DROP TABLE course;");

        assertNull(result1);
        assertNull(result2);
        assertEquals("A00001", result3.getCid());
        assertNull(result4);
        assertNull(result5);
    }

    @Test
    public void findTeacherIdByCid() {
        String result1 = courseDao.findTeacherIdByCid(null);
        String result2 = courseDao.findTeacherIdByCid("");
        String result3 = courseDao.findTeacherIdByCid("A00001");
        String result4 = courseDao.findTeacherIdByCid("A000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001");
        String result5 = courseDao.findTeacherIdByCid("''; DROP TABLE course;");

        assertNull(result1);
        assertNull(result2);
        assertEquals("00210001", result3);
        assertNull(result4);
        assertNull(result5);
    }

    @Test
    public void addCourse() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        courseDao = sqlSession.getMapper(CourseDao.class);

        //TC1:正常新增课程测试
        Course course1 = new Course("testId1",
                "testName1",
                1.0f,
                "00210001",
                "lisi",
                "每周一",
                "无备注");
        int result1 = courseDao.addCourse(course1);
        assertEquals(1, result1); //result为受影响行数

        //TC2:cid边界值测试
        Course course2 = new Course("testId00000000",
                "testName",
                1.0f,
                "00210001",
                "lisi",
                "每周一",
                "无备注");
        PersistenceException exception2 =
                assertThrows(PersistenceException.class, () -> {
            int result2 = courseDao.addCourse(course2);
        }); //断言出现PersistenceException异常
        System.out.println("Test Case 2 :");
        System.out.println(exception2.getMessage());

        //TC3:cname边界值测试
        Course course3 = new Course("testId3",
                "testName000000000000000000000000",
                1.0f,
                "00210001",
                "lisi",
                "每周一",
                "无备注");
        PersistenceException exception3 =
                assertThrows(PersistenceException.class, () -> {
            int result3 = courseDao.addCourse(course3);
        }); //断言出现PersistenceException异常
        System.out.println("Test Case 3 :");
        System.out.println(exception3.getMessage());

        //TC4:学分(credit,浮点数)边界值测试
        Course course4 = new Course("testId4",
                "testName4",
                NaN,
                "00210001",
                "lisi",
                "每周一",
                "无备注");
        PersistenceException exception4 =
                assertThrows(PersistenceException.class, () -> {
            int result4 = courseDao.addCourse(course4);
        }); //断言出现PersistenceException异常
        System.out.println("Test Case 4 :");
        System.out.println(exception4.getMessage());

        //TC5:teacherId无效时测试
        Course course5 = new Course("testId5",
                "testName5",
                1.0f,
                "123456789",
                "lisi",
                "每周一",
                "无备注");
        PersistenceException exception5 =
                assertThrows(PersistenceException.class, () -> {
            int result5 = courseDao.addCourse(course5);
        }); //断言出现PersistenceException异常
        System.out.println("Test Case 5 :");
        System.out.println(exception5.getMessage());

        //TC6:空值测试
        Course course6 = null;
        PersistenceException exception6 =
                assertThrows(PersistenceException.class, () -> {
            int result6 = courseDao.addCourse(course6);
        });
        System.out.println("Test Case 6 :");
        System.out.println(exception6.getMessage());

        //TC7:异常测试(违反数据库完整性约束)
        Course course7 = new Course("A00001", //已存在的主码
                "testName7",
                1.0f,
                "00210001",
                "lisi",
                "每周一",
                "无备注");
        PersistenceException exception7 =
                assertThrows(PersistenceException.class, () -> {
            int result7 = courseDao.addCourse(course7);
        });
        System.out.println("Test Case 7 :");
        System.out.println(exception7.getMessage());

        //sqlSession将事务回滚并关闭事务
        sqlSession.rollback();
        sqlSession.close();
    }

    @Test
    public void updateCourse() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        courseDao = sqlSession.getMapper(CourseDao.class);

        //TC1:正常修改课程测试(课程ID存在)
        Course course1 = new Course("A00015",
                "update",
                2.0f,
                "00210002",
                "Lisa",
                "(update)每周",
                "(update)无备注");
        int result1 = courseDao.updateCourse(course1);
        assertEquals(1, result1); //result为受影响行数
        assertEquals("update",
                courseDao.findCourseByCid("A00015").getCname());

        //TC2:正常修改课程测试(课程ID不存在)
        Course course2 = new Course("A11111",
                "update",
                2.0f,
                "00210002",
                "Lisa",
                "(update)每周",
                "(update)无备注");
        int result2 = courseDao.updateCourse(course2);
        assertEquals(0, result2); //result为受影响行数

        //TC3:空值测试
        Course course3 = null;
        int result3 = courseDao.updateCourse(course3);
        assertEquals(0, result3);

        //TC4:SQL注入处理测试
        Course course4 = new Course(
                "''; DROP TABLE course;", //注入
                "update",
                2.0f,
                "00210002",
                "Lisa",
                "(update)每周",
                "(update)无备注" );
        int result4 = courseDao.updateCourse(course4);
        assertEquals(0, result4);

        //TC5:边界值测试
        Course course5 = new Course("A00015",
                "updateupdateupdateupdateupdateupdateupdateupdateupdateupdateupdateupdateupdateupdate",
                2.0f,
                "00210002",
                "Lisa",
                "(update)每周",
                "(update)无备注");
        PersistenceException exception5 =
                assertThrows(PersistenceException.class, () -> {
                    int result5 = courseDao.updateCourse(course5);
                }); //断言出现PersistenceException异常
        System.out.println("Test Case 5 :");
        System.out.println(exception5.getMessage());

        //sqlSession将事务回滚并关闭事务
        sqlSession.rollback();
        sqlSession.close();
    }

    @Test
    public void deleteCourseByCid() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        courseDao = sqlSession.getMapper(CourseDao.class);

        //TC1:正常删除课程测试(课程ID存在)
        String cid1 = "123123";
        int result1 = courseDao.deleteCourseByCid(cid1);
        assertEquals(1, result1);
        assertEquals(15, courseDao.findAllCourse().size());

        //TC2:正常删除课程测试(课程ID不存在)
        String cid2 = "aaaaaa";
        int result2 = courseDao.deleteCourseByCid(cid2);
        assertEquals(0, result2);
        assertEquals(15, courseDao.findAllCourse().size());

        //TC3:cid边界值测试
        String cid3 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        int result3 = courseDao.deleteCourseByCid(cid3);
        assertEquals(0, result3);
        assertEquals(15, courseDao.findAllCourse().size());

        //TC4:违反完整性约束的删除测试(外键约束)
        String cid4 = "A00001"; //作为被引外键的一个ID
        PersistenceException exception4 =
                assertThrows(PersistenceException.class, () -> {
                    int result4 = courseDao.deleteCourseByCid(cid4);
                }); //断言出现PersistenceException异常
        System.out.println("Test Case 4 :");
        System.out.println(exception4.getMessage());

        //TC5:空值测试
        int result5 = courseDao.deleteCourseByCid(null);
        assertEquals(0, result5);
        assertEquals(15, courseDao.findAllCourse().size());

        //TC6:SQL注入处理测试
        String cid6 = "'A00001'; DROP TABLE course;";
        int result6 = courseDao.deleteCourseByCid(cid6);
        assertEquals(0, result6);
        assertEquals(15, courseDao.findAllCourse().size());


        //sqlSession将事务回滚并关闭事务
        sqlSession.rollback();
        sqlSession.close();
    }
}