package pt.olivasbo.guicejpa.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import pt.olivasbo.guicejpa.model.User;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

@SpecificDao
public class UserDao extends DaoImpl<User> {
	
	@Inject
	public UserDao(TypeLiteral<User> type, Provider<EntityManager> emp,
			Provider<EntityManagerFactory> emfp) {
		super(type, emp, emfp);
	}
	
	public User createNewUser(){
		User user = new User();
		user.setEmail("test@test.com");
		user.setUsername("test-user");
		user.setPassword("user-password");
		save(user);
		return user;
	}

}
