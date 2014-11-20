package pt.olivasbo.guicejpa.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import javax.persistence.metamodel.SingularAttribute;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

public class DaoImpl<T> implements Dao<T> {
	
	private Class<T> clazz;
	private Provider<EntityManager> emp;
	private Provider<EntityManagerFactory> emfp;
	
	@SuppressWarnings("unchecked")
	@Inject
	protected DaoImpl(TypeLiteral<T> type, Provider<EntityManager> emp,
			Provider<EntityManagerFactory> emfp) {
		Preconditions.checkArgument(type != null, "type cannot be null.");
		Preconditions.checkArgument(emp != null, "emp cannot be null.");
		Preconditions.checkArgument(emfp != null, "emfp cannot be null.");
		this.clazz = (Class<T>) type.getClass();
		this.emfp = emfp;
		this.emp = emp;
	}

	@Transactional
	@Override
	public T save(T t) {
		Preconditions.checkArgument(t != null, "t cannot be null.");
		emp.get().persist(t);
		return t;
	}

	@Transactional
	@Override
	public T saveOrUpdate(T t) {
		Preconditions.checkArgument(t != null, "t cannot be null");
		Object id = emfp.get().getPersistenceUnitUtil().getIdentifier(t);
		if (id != null) {
			t = emp.get().merge(t);
		} else {
			emp.get().persist(t);
		}
		return t;
	}

	@Override
	public List<T> findAll() {
		TypedQuery<T> q = emp.get().createQuery(
				"select e from " + clazz.getSimpleName() + " e", clazz);
		return q.getResultList();
	}

	@Override
	public <K> T find(K id) {
		Preconditions.checkArgument(id != null, "id cannot be null.");
		return emp.get().find(clazz, id);
	}

	@Transactional
	@Override
	public void remove(T t) {
		Preconditions.checkArgument(t != null, "t cannot be null.");
		emp.get().remove(t);
		emp.get().flush();
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value,
			Optional<Integer> offset, Optional<Integer> count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value,
			Optional<Integer> offset, Optional<Integer> count, Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties,
			Optional<Integer> offset, Optional<Integer> count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties,
			Optional<Integer> offset, Optional<Integer> count, Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> T findUnique(SingularAttribute<T, K> property, K value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> T findUnique(Map<SingularAttribute<T, K>, K> properties) {
		// TODO Auto-generated method stub
		return null;
	}

}
