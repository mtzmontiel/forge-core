/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui.repositories;

import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.UICommand;

/**
 * Responsible for removing {@link DependencyRepository} entries from a {@link Project}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RemoveRepositoryCommand extends UICommand
{
}