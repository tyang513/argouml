// $Id$
// Copyright (c) 1996-2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.ui;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ArgoDiagram;
import org.argouml.uml.diagram.activity.layout.ActivityDiagramLayouter;
import org.argouml.uml.diagram.activity.ui.UMLActivityDiagram;
import org.argouml.uml.diagram.layout.Layouter;
import org.argouml.uml.diagram.static_structure.layout.ClassdiagramLayouter;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.SelectionManager;

/**
 * Action to automatically lay out a diagram.
 *
 */
public class ActionLayout extends UMLAction {

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * The constructor.
     */
    public ActionLayout() {
        super("action.layout", true, NO_ICON);
    }

    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * Check whether we deal with a supported diagram type
     * (currently only UMLClassDiagram).
     * @see org.argouml.ui.ProjectBrowser
     */
    public boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        }
        Project p = ProjectManager.getManager().getCurrentProject();
        if (p == null) {
            return false;
        }
        ArgoDiagram d = p.getActiveDiagram();
        if (d instanceof UMLClassDiagram 
                || d instanceof UMLActivityDiagram) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This action performs the layout and triggers a redraw of the editor pane.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
        ArgoDiagram diagram = ProjectManager.getManager()
                .getCurrentProject().getActiveDiagram();
        Layouter layouter;
        if (diagram instanceof UMLClassDiagram) {
             layouter = new ClassdiagramLayouter((UMLClassDiagram) diagram);
        } else if (diagram instanceof UMLActivityDiagram) {
             layouter = 
                 new ActivityDiagramLayouter((UMLActivityDiagram) diagram);
        } else {
            return;
        }

        // Using the selection manager to force a repaint seems like a
        // heavyweight way to do this - tfm
        
        // Create a selection containing all figures in diagram
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        Collection nodes =
            ((UMLDiagram) ProjectManager.getManager().getCurrentProject()
                    .getActiveDiagram())
                    .getLayer().getContents();                    
        sm.select(nodes);

        // Rearrange the diagram layout
        layouter.layout();
        
        // Tell the selection manager we're done and deselect everything
        // This will force a repaint.
        sm.endTrans(); 
        sm.deselectAll();
    }
} /* end class ActionLayout */
