package pt.olivasbo.guicejpa;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.Set;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.olivasbo.guicejpa.dao.Dao;
import pt.olivasbo.guicejpa.dao.DaoImpl;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.util.Types;


public class JpaDaoModule extends AbstractModule {
	
	private static final Logger log = LoggerFactory.getLogger(JpaDaoModule.class);
	
	private String persistenceUnit;
	private Properties persistenceProperties;
	private boolean startService;
	private String[] entityPackages;

	public JpaDaoModule(String persistenceUnit, Properties persistenceProperties, boolean startService, String...entityPackages) {
		this.persistenceUnit = checkNotNull(persistenceUnit, "persistenceUnit cannot be null.");
		this.persistenceProperties = checkNotNull(persistenceProperties, "persistenceProperties cannot be null");
		this.startService = startService;
		this.entityPackages = entityPackages;
	}
	
	public static Reflections getReflectionsForPackage(String pckg){
    	ConfigurationBuilder cfgBldr = new ConfigurationBuilder();
		FilterBuilder filterBuilder = new FilterBuilder();
		cfgBldr.addUrls(ClasspathHelper.forPackage(pckg));
		filterBuilder.include(FilterBuilder.prefix(pckg));
		cfgBldr.filterInputsBy(filterBuilder).setScanners(
				new SubTypesScanner(), new TypeAnnotationsScanner());
		return new Reflections(cfgBldr);
    }

	@Override
	protected void configure() {
		JpaPersistModule jpaModule = new JpaPersistModule(persistenceUnit);
		jpaModule.properties(persistenceProperties);
		install(jpaModule);
		if(startService){
			bind(JpaServiceInitializer.class).asEagerSingleton();
		}
		bindGenericDaos();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Object> void bindGenericDaos(){
		for(String entityPackage: entityPackages){
			Reflections packageReflections = getReflectionsForPackage(entityPackage);
			Set<Class<?>> entities = packageReflections.getTypesAnnotatedWith(Entity.class);
			for(Class<?> entity: entities){
				log.debug("Bind dao to entity: {}", entity.getCanonicalName());
				TypeLiteral<T> daoTypeLiteral = (TypeLiteral<T>) TypeLiteral.get(Types.newParameterizedType(Dao.class, entity));
				TypeLiteral<T> daoImplTypeLiteral = (TypeLiteral<T>) TypeLiteral.get(Types.newParameterizedType(DaoImpl.class, entity));
				log.debug("Dao TypeLiteral: {}", daoTypeLiteral.toString());
				log.debug("DaoImpl TypeLiteral: {}", daoImplTypeLiteral.toString());
				bind(daoTypeLiteral).to(daoImplTypeLiteral);
			}
		}
	}

}
