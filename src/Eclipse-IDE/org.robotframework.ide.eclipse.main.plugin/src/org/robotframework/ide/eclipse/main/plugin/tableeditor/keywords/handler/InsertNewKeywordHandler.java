/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordsSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorCommandsStack;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorSources;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.code.handler.E4InsertNewCodeHolderHandler;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler.InsertNewKeywordHandler.E4InsertNewKeywordHandler;
import org.robotframework.red.commands.DIParameterizedHandler;
import org.robotframework.red.viewers.Selections;

public class InsertNewKeywordHandler extends DIParameterizedHandler<E4InsertNewKeywordHandler> {

    public InsertNewKeywordHandler() {
        super(E4InsertNewKeywordHandler.class);
    }

    public static class E4InsertNewKeywordHandler extends E4InsertNewCodeHolderHandler {

        @Execute
        public void insertNewUserDefinedKeyword(
                @Named(RobotEditorSources.SUITE_FILE_MODEL) final RobotSuiteFile fileModel,
                @Named(Selections.SELECTION) final IStructuredSelection selection,
                final RobotEditorCommandsStack stack) {

            insertNewHolder(fileModel, selection, stack, RobotKeywordsSection.class);
        }
    }
}
