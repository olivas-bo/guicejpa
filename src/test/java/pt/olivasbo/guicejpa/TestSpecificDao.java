package pt.olivasbo.guicejpa;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.olivasbo.guicejpa.dao.UserDao;
import pt.olivasbo.guicejpa.model.User;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class TestSpecificDao {
		
	private static final String PERSISTENCE_MODULE = "guicejpa-test";
	
	private static Injector injector;
	@Inject
	private Provider<EntityManager> emp;
	@Inject
	private UserDao userDao;
	private static EntityManager em;
	
	@BeforeClass
	public static void setup(){
		injector = Guice.createInjector(new JpaDaoModule(PERSISTENCE_MODULE, new Properties(), true, "pt.olivasbo.guicejpa.model", "pt.olivasbo.guicejpa.dao"));
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
	
	@Test
	public void testCreateNewUser(){
		User u = userDao.createNewUser();
		assertNotNull(u);
		assertNotNull(u.getUserId());
	}

}
