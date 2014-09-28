/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class GlassFish4Container extends JavaEEDefaultContainer
{
   private static final String DEFAULT_DS = "jdbc/__default";

   @Override
   public DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.DERBY;
   }

   @Override
   public String getDefaultDataSource()
   {
      return DEFAULT_DS;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "Oracle Glassfish 4.x" : "GLASSFISH_4";
   }
}
