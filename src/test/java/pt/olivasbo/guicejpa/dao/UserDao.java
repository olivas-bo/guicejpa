package pt.olivasbo.guicejpa.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.olivasbo.guicejpa.model.User;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

@SpecificDao
public class UserDao extends DaoImpl<User> {
	
	private static final Logger log = LoggerFactory.getLogger(UserDao.class);

	@Inject
	public UserDao(TypeLiteral<User> type, Provider<EntityManager> emp,
			Provider<EntityManagerFactory> emfp) {
		super(type, emp, emfp);
	}
	
	public void testMethod(){
		log.debug("This is a test method. Cool!");
	}

}
