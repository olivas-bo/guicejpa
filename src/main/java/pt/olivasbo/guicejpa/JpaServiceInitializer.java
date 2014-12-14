package pt.olivasbo.guicejpa;

import javax.inject.Inject;

import com.google.inject.persist.PersistService;

public class JpaServiceInitializer {

	@Inject
	public JpaServiceInitializer(final PersistService service) {
		service.start();
	}

}
