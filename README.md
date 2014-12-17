GuiJpa
============

Jpa Module to use with Guice that provides generic type safe DAOs for all classes annotated with @Entity.
This is still a WIP module.

Current Features:

 - Integration with Guice providing bindings for Typed Daos for all @Entity classes.
 
#Usage

You can checkout the repo and do a mvn clean package. Checkout the test cases to understand the features.

The module can be instantiated with the persistence unit name, properties object, boolean flag to start the
JpaService and optionally package names containing the entity classes and/or specific DAOs.

		Injector injector = Guice.createInjector(
			new JpaDaoModule(PERSISTENCE_MODULE_NAME, properties, true, "package")
		);
		
With the injector in place tou can then inject Generic DAOs for free like this:

		@Inject
		Dao<User> userDao;
		
The DAO provides several common methods that almost all application will need providing type safe Queries with
JPA CriteriaAPI.
		
#Specific DAOs

If you have a specific implementation for any given model, you can annotate the class with the @SpecificDao
annotation and add the package to the JpaDaoModule constructer in order for auto binding the Dao with Juice.