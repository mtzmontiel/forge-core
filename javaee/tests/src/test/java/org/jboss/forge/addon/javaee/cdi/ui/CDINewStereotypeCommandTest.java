/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_CDI_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
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
import org.jboss.forge.roaster.model.JavaAnnotation;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CDINewStereotypeCommandTest
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
      projectHelper.installCDI_1_0(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewStereotypeCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDINewStereotypeCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: New Stereotype", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(7, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("alternative"));
         assertTrue(controller.hasInput("withNamed"));
         assertTrue(controller.hasInput("inherited"));
         assertTrue(controller.hasInput("targetTypes"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_CDI_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("cdi-new-stereotype --named Dummy", 10, TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testCreateNewStereotype() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewStereotypeCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyStereotype");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("targetTypes", Arrays.asList(ElementType.TYPE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyStereotype");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));
      JavaAnnotation<?> ann = javaResource.getJavaType();
      Assert.assertTrue(ann.hasAnnotation(Stereotype.class));
      Assert.assertFalse(ann.hasAnnotation(Inherited.class));
      Assert.assertFalse(ann.hasAnnotation(Named.class));
      Assert.assertFalse(ann.hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewInheritedStereotype() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewStereotypeCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyStereotype");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("inherited", Boolean.TRUE);
         controller.setValueFor("targetTypes", Arrays.asList(ElementType.TYPE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyStereotype");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));
      JavaAnnotation<?> ann = javaResource.getJavaType();
      Assert.assertTrue(ann.hasAnnotation(Stereotype.class));
      Assert.assertTrue(ann.hasAnnotation(Inherited.class));
      Assert.assertFalse(ann.hasAnnotation(Named.class));
      Assert.assertFalse(ann.hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewNamedStereotype() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewStereotypeCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyStereotype");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("withNamed", Boolean.TRUE);
         controller.setValueFor("targetTypes", Arrays.asList(ElementType.TYPE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyStereotype");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));
      JavaAnnotation<?> ann = javaResource.getJavaType();
      Assert.assertTrue(ann.hasAnnotation(Stereotype.class));
      Assert.assertFalse(ann.hasAnnotation(Inherited.class));
      Assert.assertTrue(ann.hasAnnotation(Named.class));
      Assert.assertFalse(ann.hasAnnotation(Alternative.class));
   }

   @Test
   public void testCreateNewAlternativeStereotype() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewStereotypeCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyStereotype");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("alternative", Boolean.TRUE);
         controller.setValueFor("targetTypes", Arrays.asList(ElementType.TYPE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyStereotype");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaAnnotation.class)));
      JavaAnnotation<?> ann = javaResource.getJavaType();
      Assert.assertTrue(ann.hasAnnotation(Stereotype.class));
      Assert.assertFalse(ann.hasAnnotation(Inherited.class));
      Assert.assertFalse(ann.hasAnnotation(Named.class));
      Assert.assertTrue(ann.hasAnnotation(Alternative.class));
   }
}