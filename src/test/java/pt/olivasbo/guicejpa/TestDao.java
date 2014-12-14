package pt.olivasbo.guicejpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.olivasbo.guicejpa.dao.Dao;
import pt.olivasbo.guicejpa.model.User;
import pt.olivasbo.guicejpa.model.User_;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class TestDao {
	
	private static final Logger log = LoggerFactory.getLogger(TestDao.class);
	
	private static final String PERSISTENCE_MODULE = "guicejpa-test";
	private static SecureRandom random = new SecureRandom();
	
	private static Injector injector;
	@Inject
	private Provider<EntityManager> emp;
	@Inject
	private Dao<User> userDao;
	private static EntityManager em;
	
	@BeforeClass
	public static void setup(){
		log.debug("Setting up database for testing.");
		injector = Guice.createInjector(new JpaDaoModule(PERSISTENCE_MODULE, new Properties(), true, "pt.olivasbo.guicejpa.model"));
	}
	
	@Before
	public void setupTest(){
		injector.injectMembers(this);
		assertNotNull(emp);
		assertNotNull(userDao);
		em = emp.get();
		em.getTransaction().begin();
	}
	
	@After
	public void tearDown() throws Exception {
		if(em.getTransaction().isActive()){
			em.getTransaction().rollback();
		}
	}
	
	private static User getUser(){
		User u = new User();
		u.setUsername(new BigInteger(130, random).toString(15));
		u.setPassword(new BigInteger(130, random).toString(20));
		u.setEmail(new BigInteger(130, random).toString(32));
		return u;
	}
	
	@Test
	public void testSave(){
		log.info("Saving new user.");
		User u = getUser();
		userDao.save(u);
		log.debug("UserId: {}", u.getUserId());
	}
	
	@Test
	public void testFindAll(){
		//Add 2 users to the database
		User u1 = getUser();
		userDao.save(u1);
		User u2 = getUser();
		userDao.save(u2);
		List<User> users = userDao.findAll();
		log.debug("testFindAll Users size: {}", users.size());
		log.debug("testFindAll: {}", Arrays.toString(users.toArray()));
		assertEquals(users.size(), 2);
	}
	
	@Test
	public void testFind(){
		User u = getUser();
		userDao.save(u);
		User foundUser = userDao.find(u.getUserId());
		log.debug("testFindUniqueByProperty: {}", foundUser.toString());
		assertNotNull(foundUser);
	}
	
	@Test
	public void testFindUniqueByProperty(){
		User u = getUser();
		userDao.save(u);
		User foundUser = userDao.findUnique(User_.username, u.getUsername());
		log.debug("testFindUniqueByProperty: {}", foundUser.toString());
		assertNotNull(foundUser);
	}

}
