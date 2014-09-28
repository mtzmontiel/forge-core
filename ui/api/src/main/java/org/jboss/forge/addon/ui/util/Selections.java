/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.util.Lists;

/**
 * Possible {@link UISelection} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public final class Selections
{

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static <SELECTIONTYPE> UISelection<SELECTIONTYPE> from(SELECTIONTYPE... type)
   {
      if (type == null || type.length == 0 || type[0] == null)
      {
         return emptySelection();
      }
      else
      {
         if (type.length == 1 && type[0] instanceof Iterable)
         {
            return new SelectionImpl((Iterable<SELECTIONTYPE>) type[0]);
         }
         else
         {
            return new SelectionImpl(type);
         }
      }
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static <SELECTIONTYPE> UISelection<SELECTIONTYPE> from(Iterable<SELECTIONTYPE> type)
   {
      if (type == null || Lists.toList(type).isEmpty())
      {
         return emptySelection();
      }
      else
      {
         return new SelectionImpl(type);
      }
   }

   @SuppressWarnings("unchecked")
   public static <SELECTIONTYPE> UISelection<SELECTIONTYPE> emptySelection()
   {
      return (UISelection<SELECTIONTYPE>) EmptySelection.INSTANCE;
   }

   private static class SelectionImpl<SELECTIONTYPE> implements UISelection<SELECTIONTYPE>
   {
      private final List<SELECTIONTYPE> selection;

      @SuppressWarnings("unchecked")
      public SelectionImpl(SELECTIONTYPE... type)
      {
         if (type != null)
            this.selection = Arrays.asList(type);
         else
            selection = Collections.emptyList();
      }

      public SelectionImpl(Iterable<SELECTIONTYPE> type)
      {
         if (type != null)
            this.selection = Lists.toList(type);
         else
            selection = Collections.emptyList();
      }

      @Override
      public Iterator<SELECTIONTYPE> iterator()
      {
         return selection.iterator();
      }

      @Override
      public SELECTIONTYPE get()
      {
         return selection.isEmpty() ? null : selection.get(0);
      }

      @Override
      public int size()
      {
         return selection.size();
      }

      @Override
      public boolean isEmpty()
      {
         return selection.isEmpty();
      }
   }

   private enum EmptySelection implements UISelection<Object>
   {
      INSTANCE;
      @Override
      public Iterator<Object> iterator()
      {
         return Collections.emptyList().iterator();
      }

      @Override
      public Object get()
      {
         return null;
      }

      @Override
      public int size()
      {
         return 0;
      }

      @Override
      public boolean isEmpty()
      {
         return true;
      }
   }
}
