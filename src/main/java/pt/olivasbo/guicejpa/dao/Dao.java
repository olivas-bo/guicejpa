package pt.olivasbo.guicejpa.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Order;
import javax.persistence.metamodel.SingularAttribute;

import com.google.common.base.Optional;

public interface Dao<T> {
	
	public T save(T t);
	
	public T saveOrUpdate(T t);
	
	public List<T> findAll();
	
	public <K> T find(K id);
	
	public void remove(T t);
	
	public <K> List<T> find(SingularAttribute<T, K> property, K value);

	public <K> List<T> find(SingularAttribute<T, K> property, K value, Optional<Integer> offset, Optional<Integer> count);
	
	public <K> List<T> find(SingularAttribute<T, K> property, K value, Optional<Integer> offset, Optional<Integer> count, Order order);
	
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties);
	
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties, Optional<Integer> offset, Optional<Integer> count);
	
	public <K> List<T> find(Map<SingularAttribute<T, K>, K> properties, Optional<Integer> offset, Optional<Integer> count, Order order);
	
	public <K> T findUnique(SingularAttribute<T, K> property, K value);
	
	public <K> T findUnique(Map<SingularAttribute<T, K>, K> properties);

}
