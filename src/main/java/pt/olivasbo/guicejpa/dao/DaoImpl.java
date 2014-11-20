package pt.olivasbo.guicejpa.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
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
		this.clazz = (Class<T>) checkNotNull(type, "type cannot be null.").getClass();
		this.emp = checkNotNull(emp, "emp cannot be null.");
		this.emfp = checkNotNull(emfp, "emfp cannot be null.");
	}

	@Transactional
	@Override
	public T save(T t) {
		checkNotNull(t, "t cannot be null.");
		emp.get().persist(t);
		return t;
	}

	@Transactional
	@Override
	public T saveOrUpdate(T t) {
		checkNotNull(t, "t cannot be null.");
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
	public List<T> findAll(Optional<Integer> offset, Optional<Integer> limit) {
		TypedQuery<T> query = emp.get().createQuery(
				"select e from " + clazz.getSimpleName() + " e", clazz);
		setQueryLimits(query, offset, limit);
		return query.getResultList();
	}

	@Override
	public <K> T find(K id) {
		checkNotNull(id, "id cannot be null.");
		return emp.get().find(clazz, id);
	}

	@Transactional
	@Override
	public void remove(T t) {
		checkNotNull(t, "t cannot be null.");
		emp.get().remove(t);
		emp.get().flush();
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value) {
		checkNotNull(property, "properties cannot be null.");
		checkNotNull(value, "properties cannot be null.");
		return find(property, value, Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Order>absent());
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value,
			Optional<Integer> offset, Optional<Integer> limit) {
		checkNotNull(property, "properties cannot be null.");
		checkNotNull(value, "properties cannot be null.");
		checkNotNull(offset, "offset cannot be null.");
		checkNotNull(limit, "limit cannot be null");
		return find(property, value, offset, limit, Optional.<Order>absent());
	}

	@Override
	public <K> List<T> find(SingularAttribute<T, K> property, K value,
			Optional<Integer> offset, Optional<Integer> limit, Optional<Order> order) {
		checkNotNull(property, "properties cannot be null.");
		checkNotNull(value, "properties cannot be null.");
		checkNotNull(offset, "offset cannot be null.");
		checkNotNull(limit, "limit cannot be null");
		checkNotNull(order, "order cannot be null");
		Map<SingularAttribute<T, K>, K> properties = ImmutableMap.<SingularAttribute<T, K>, K>builder()
				.put(property, value)
				.build();
		TypedQuery<T> query = getTypedQuery(properties, order);
		setQueryLimits(query, offset, limit);
		return query.getResultList();
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties) {
		checkNotNull(properties, "properties cannot be null.");
		return find(properties, Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Order>absent());
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties,
			Optional<Integer> offset, Optional<Integer> limit) {
		checkNotNull(properties, "properties cannot be null.");
		checkNotNull(offset, "offset cannot be null.");
		checkNotNull(limit, "limit cannot be null");
		return find(properties, offset, limit, Optional.<Order>absent());
	}

	@Override
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties,
			Optional<Integer> offset, Optional<Integer> limit, Optional<Order> order) {
		checkNotNull(properties, "properties cannot be null.");
		checkNotNull(offset, "offset cannot be null.");
		checkNotNull(limit, "limit cannot be null");
		checkNotNull(order, "order cannot be null");
		TypedQuery<T> query = getTypedQuery(properties, order);
		setQueryLimits(query, offset, limit);
		return query.getResultList();
	}

	@Override
	public <K> T findUnique(SingularAttribute<T, K> property, K value) {
		checkNotNull(property, "property cannot be null.");
		checkNotNull(value, "value cannot be null.");
		Map<SingularAttribute<T, K>, K> properties = ImmutableMap.<SingularAttribute<T, K>, K>builder()
				.put(property, value)
				.build();
		return findUnique(properties);
	}

	@Override
	public <K> T findUnique(Map<SingularAttribute<T, K>, K> properties) {
		checkNotNull(properties, "properties cannot be null.");
		TypedQuery<T> query = getTypedQuery(properties, Optional.<Order>absent());
		query.setMaxResults(1);
		return query.getSingleResult();
	}
	
	private <Y> TypedQuery<T> getTypedQuery(Map<SingularAttribute<T, Y>, Y> properties, Optional<Order> order){
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(this.clazz);
		Root<T> from = cq.from(this.clazz);
		for(SingularAttribute<T, Y> property: properties.keySet()){
			cq.where(cb.equal(from.get(property), properties.get(property)));
		}
		if(order.isPresent()){
			cq.orderBy(order.get());
		}
		TypedQuery<T> tQuery = emp.get().createQuery(cq);
		return tQuery;
	}
	
	private void setQueryLimits(Query q, Optional<Integer> offset, Optional<Integer> count){
		if(offset.isPresent()){
			q.setFirstResult(offset.get());
		}
		if(count.isPresent()){
			q.setMaxResults(count.get());
		}
	}

}
