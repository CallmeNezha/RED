/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.TableConfigurationLabels;

public class KeywordsElementsLabelAccumulator implements IConfigLabelAccumulator {

    public static final String KEYWORD_DEFINITION_CONFIG_LABEL = "KEYWORD_DEFINITION";

    public static final String KEYWORD_DEFINITION_ARGUMENT_CONFIG_LABEL = "KEYWORD_DEFINITION_ARGUMENT";

    public static final String KEYWORD_DEFINITION_SETTING_CONFIG_LABEL = "KEYWORD_DEFINITION_SETTING";

    private final IRowDataProvider<Object> dataProvider;

    public KeywordsElementsLabelAccumulator(final IRowDataProvider<Object> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
        final Object rowObject = dataProvider.getRowObject(rowPosition);

        if (columnPosition == 0) {
            if (rowObject instanceof RobotKeywordCall && ((RobotKeywordCall) rowObject).isLocalSetting()) {
                configLabels.addLabel(KEYWORD_DEFINITION_SETTING_CONFIG_LABEL);
                
            } else if (rowObject instanceof RobotKeywordDefinition) {
                configLabels.addLabel(KEYWORD_DEFINITION_CONFIG_LABEL);
                
            } else if (rowObject instanceof RobotKeywordCall) {
                final AModelElement<?> linkedElement = ((RobotKeywordCall) rowObject).getLinkedElement();
                final boolean isForLoopContinuation = linkedElement.getElementTokens()
                        .stream()
                        .findFirst()
                        .filter(token -> token.getTypes().contains(RobotTokenType.FOR_WITH_END_CONTINUATION))
                        .isPresent();
                if (isForLoopContinuation) {
                    configLabels.addLabelOnTop(TableConfigurationLabels.CELL_NOT_EDITABLE_LABEL);
                }
            }

        } else if (columnPosition > 0) {
            if (columnPosition > 1 && rowObject instanceof RobotKeywordCall
                    && ((RobotKeywordCall) rowObject).isDocumentationSetting()) {

                configLabels.addLabelOnTop(TableConfigurationLabels.CELL_NOT_EDITABLE_LABEL);

            } else if (rowObject instanceof RobotKeywordDefinition) {
                configLabels.addLabel(KEYWORD_DEFINITION_ARGUMENT_CONFIG_LABEL);
            }
        }
    }
}
