/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 * Fired when a temporary {@link Resource} has been created.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TempResourceCreated extends ResourceEvent
{
   public TempResourceCreated(final Resource<?> resource)
   {
      super(resource);
   }
}
