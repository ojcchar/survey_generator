/*******************************************************************************
 * Copyright (c) 2019, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package output;

import graph.AppGuiComponent;
import graph.AppStep;
import graph.GraphTransition;
import org.apache.commons.lang3.StringUtils;
import s2rquality.NLAction;

import java.util.HashMap;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 */
public class UtilReporter {

    static HashMap<String, String> COMPONENT_TRANSLATIONS = new HashMap<>();
    static {
        COMPONENT_TRANSLATIONS.put("Edit Text", "Text Field");
        COMPONENT_TRANSLATIONS.put("Relative Layout", "View");
        COMPONENT_TRANSLATIONS.put("Linear Layout", "View");
        COMPONENT_TRANSLATIONS.put("Spinner", "Drop Down List");
    }
    
    public static String getNLStep(AppStep step) {
        Integer action = step.getAction();

        if (DeviceHelper.OPEN_APP == action) {
            return "Open the application";
        }

        StringBuilder builder = new StringBuilder();

        AppGuiComponent component = step.getComponent();
        if (component != null) {

            builder.append("On the \"");
            String windowString = GraphTransition.getWindowString(component.getActivity(),
                    component.getCurrentWindow());
            final int LIMIT_WINDOW_TEXT = 37;
            if (windowString.length() > LIMIT_WINDOW_TEXT) {
                windowString = windowString.substring(0, LIMIT_WINDOW_TEXT) + "...";
            }
            builder.append(windowString);
            builder.append("\" screen, ");

        }

        // action
        builder.append(GraphTransition.getAction(action));
        builder.append(" ");

        // the action text
        String stepText = step.getText();
        if (GraphTransition.isTypeAction(action)) {

            builder.append("a valid input");
            builder.append(" ");
            if (!stepText.trim().isEmpty()) {
                builder.append("(e.g., ");
                builder.append(stepText);
                builder.append(")");
                builder.append(" ");
            }
        } else {

            if (stepText != null && !stepText.trim().isEmpty()) {
                builder.append(stepText);
                builder.append(" ");
            }
        }

        if (component != null) {

            builder.append("on the ");

            // -----------------------------------

            final String componentDescription = getComponentDescription(component);
            builder.append(componentDescription);

        }

        return builder.toString().trim();
    }


    public static String getComponentDescription(AppGuiComponent component) {
        StringBuilder builder =new StringBuilder();
        String compName = getComponentName(component);
        if (compName != null && !compName.trim().isEmpty()) {
            builder.append(compName);
            builder.append(" ");
        }

        builder.append(translateComponent(GraphTransition.getComponentString(component.getType())).toLowerCase());
        return builder.toString();
    }

    public static String getComponentName(AppGuiComponent component) {

        if (component==null){
            return "";
        }

        StringBuilder builder = new StringBuilder();

        String compText = component.getText();
        if (StringUtils.isEmpty(compText)) {
            compText = component.getContentDescription();
            if (StringUtils.isEmpty(compText)) {
                compText = "";
            }
        }

        final int LIMIT_COMPONENT_TEXT = 47;
        if (compText.length() > LIMIT_COMPONENT_TEXT) {
            compText = compText.substring(0, LIMIT_COMPONENT_TEXT) + "...";
        }

        compText = compText.replace("\n", " ").replace("\r", " ");

        // -----------------------------------

        String idXml = component.getLastPartOfIdXml();

        if (idXml != null && !idXml.trim().isEmpty()) {

            builder.append("\"");
            builder.append(GraphTransition.splitAndSpecialRemoveChars(idXml));

            if (!compText.isEmpty()) {
                builder.append(" (");
                builder.append(compText);
                builder.append(")");
            }
            builder.append("\"");

        } else {

            if (!compText.isEmpty()) {
                builder.append("\"");
                builder.append(compText);
                builder.append("\"");
            }
        }

        return builder.toString();
    }
    
    public static String translateComponent(String componentString) {
        String transComp = COMPONENT_TRANSLATIONS.get(componentString);
        if (transComp == null) {
            return componentString;
        }
        return transComp;
    }
    
    /**
     * By default the subject is included
     * 
     * @param nlAction
     * @param attachType
     * @param includePrefix
     * @return
     */
    public static String getActionString(NLAction nlAction, boolean attachType, boolean includePrefix) {
        return getActionString(nlAction, attachType, includePrefix, true);
    }
    
    public static String getActionString(NLAction nlAction, boolean attachType, boolean includePrefix, boolean includeSubject) {
        StringBuilder builder = new StringBuilder();

        if (includeSubject || !"user".equalsIgnoreCase(nlAction.getSubject())) {
            builder.append(nlAction.getSubject());
            builder.append(" ");
        }
        if (nlAction.isActionNegated() != null && nlAction.isActionNegated()) {
            builder.append("not");
            builder.append(" ");
        }
        builder.append(nlAction.getAction());
        builder.append(" ");
        if (nlAction.getObject() != null && !nlAction.getObject().isEmpty()) {
            builder.append(nlAction.getObject());
            builder.append(" ");
        }
        if (nlAction.getPreposition() != null && !nlAction.getPreposition().isEmpty()) {
            builder.append(nlAction.getPreposition());
            builder.append(" ");
        }
        if (nlAction.getObject2() != null && !nlAction.getObject2().isEmpty()) {
            builder.append(nlAction.getObject2());
            builder.append(" ");
        }

        if (attachType) {

            builder.append("[");
            builder.append(nlAction.isSRAction() ? "user step" : "app behavior");
            builder.append("]");
            builder.append(" ");
        }

        String prefix = "&nbsp;&nbsp;&nbsp;";
        return (includePrefix ? prefix : "")
                //+ nlAction.getSequence() + ". "
                + StringUtils.capitalize(builder.toString());
    }
}
