GuiJpa
============

Jpa Module to use with Guice that provides generic type safe DAOs for all classes annotated with @Entity.
This is still a WIP module.

Current Features:

 - Integration with Guice providing bindings for Typed Daos for all @Entity classes.
 
#Usage

You can checkout the repo and do a mvn clean package. Checkout the test cases to understand the features.

Instantiate the module with the persistenceUnit

		Injector injector = Guice.createInjector(
			new JpaDaoModule(PERSISTENCE_MODULE_NAME, properties, true, "package")
		);