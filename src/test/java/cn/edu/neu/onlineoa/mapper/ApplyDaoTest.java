package cn.edu.neu.onlineoa.mapper;

import cn.edu.neu.onlineoa.bean.Apply;

import cn.edu.neu.onlineoa.utils.MybatisUtils;
import junit.framework.TestCase;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Float.NaN;
import static org.junit.Assert.*;


public class ApplyDaoTest {
    SqlSessionFactory sqlSessionFactory = MybatisUtils.getSessionFactoryInstance();
    ApplyDao applyDao;

    @Before
    public void init() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        applyDao = sqlSession.getMapper(ApplyDao.class);
    }

    @Test
    public void testFindApplyWithMultiCondition() {
        Apply apply = new Apply();
        apply.setStatus(1);
        apply.setCourseId("A00001");
        apply.setStudentId("20210015");
        apply.setStudentName("zhangsan");
        apply.setCourseName("JavaWeb");
        List<Apply> applyList = applyDao.findApplyWithMultiCondition(apply, "", "");
        assertNotNull(applyList);
    }

    @Test
    public void testFindApplyWithMultiCondition2() {
        Boolean confirmValue = true;
        Apply apply = new Apply();
        apply.setStatus(1);
        List<Apply> applyList = applyDao.findApplyWithMultiCondition(apply, "", "");
        assertNotNull(applyList);
    }

    @Test
    public void testFindApplyWithMultiCondition3() {
        Apply apply = new Apply();
        List<Apply> nullList = new ArrayList<>();
        List<Apply> applyList = applyDao.findApplyWithMultiCondition(apply, "", "");
        assertEquals(nullList, applyList);
    }

    @Test
    public void testFindApplyWithMultiCondition4() {
        Boolean confirmValue = false;
        Apply apply = new Apply();
        apply.setStudentName("zhangsan");
        apply.setStatus(1);
        apply.setConfirm(confirmValue);
        List<Apply> applyList = applyDao.findApplyWithMultiCondition(apply, "", "");
        assertNotNull(applyList);
    }
}