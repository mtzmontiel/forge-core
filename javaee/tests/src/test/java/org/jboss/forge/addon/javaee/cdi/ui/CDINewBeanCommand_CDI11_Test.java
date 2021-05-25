/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_CDI_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class CDINewBeanCommand_CDI11_Test
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_1(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDINewBeanCommand);
         assertTrue(controller.getCommand() instanceof AbstractCDICommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: New Bean", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(12, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("scoped"));
         assertTrue(controller.hasInput("customScopeAnnotation"));
         assertTrue(controller.hasInput("qualifier"));
         assertTrue(controller.hasInput("alternative"));
         assertTrue(controller.hasInput("withNamed"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.hasInput("enabled"));
         assertTrue(controller.hasInput("priority"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_CDI_PACKAGE));
      }
   }

   @Test
   public void testCreateNewBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.DEPENDENT.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Dependent.class));
   }

   @Test
   public void testCreateNewBeanAlternativeEnabled() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("alternative", true);
         controller.setValueFor("priority", 500);
         controller.setValueFor("enabled", true);
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Priority.class));
      assertTrue(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
      CDIFacet_1_1 cdiFacet = project.getFacet(CDIFacet_1_1.class);
      List<String> allClazz = cdiFacet.getConfig().getOrCreateAlternatives().getAllClazz();
      Assert.assertThat(allClazz.size(), is(1));
      Assert.assertThat(allClazz.get(0), equalTo("org.jboss.forge.test.MyServiceBean"));
   }

}