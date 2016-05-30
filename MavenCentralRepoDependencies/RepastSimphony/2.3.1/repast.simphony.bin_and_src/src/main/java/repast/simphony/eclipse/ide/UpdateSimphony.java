/*
 * Copyright (c) 2003, Alexander Greif All rights reserved. (Adapted by Michael
 * J. North for Use in Repast Simphony from Alexander Greif?s Flow4J-Eclipse
 * (http://flow4jeclipse.sourceforge.net/docs/index.html), with Thanks to the
 * Original Author) (Michael J. North?s Modifications are Copyright 2007 Under
 * the Repast Simphony License, All Rights Reserved)
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * Neither the name of the
 * Flow4J-Eclipse project nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package repast.simphony.eclipse.ide;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;

import repast.simphony.eclipse.RepastSimphonyNature;
import repast.simphony.eclipse.RepastSimphonyPlugin;
import repast.simphony.eclipse.action.FileSystemSelectionAction;

/**
 * Updates a previous version of Simphony with the current nature.
 * 
 */
public class UpdateSimphony extends FileSystemSelectionAction {

  /**
   * Constructs this action instance and specifies that the selected objects
   * should be of type <code>IProject</code>
   */
  public UpdateSimphony() {
    super(IProject.class);
  }

  /**
   * Runs the action for the selected objects. Updates the Repast Simphony nature
   * on the selected projects.
   * 
   * @param selectedProjects
   *          the selected projects
   * @see repast.simphony.agents.designer.ui.actions.FileSystemSelectionAction#run(org.eclipse.jface.action.IAction,
   *      java.util.List)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void run(IAction action, List selectedProjects) {
    for (Iterator iter = selectedProjects.iterator(); iter.hasNext();) {
      try {
        RepastSimphonyNature nature = new RepastSimphonyNature();
        IProject project = (IProject) iter.next();
        nature.setProject(project);
        nature.configure();
      } catch (CoreException ex) {
        RepastSimphonyPlugin.getInstance().log(ex);
      }
    }
  }

}
