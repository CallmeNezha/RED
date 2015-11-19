/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.core.testData.model.table.setting.mapping.test;

import java.util.List;
import java.util.Stack;

import org.robotframework.ide.core.testData.model.FilePosition;
import org.robotframework.ide.core.testData.model.RobotFileOutput;
import org.robotframework.ide.core.testData.model.table.SettingTable;
import org.robotframework.ide.core.testData.model.table.mapping.IParsingMapper;
import org.robotframework.ide.core.testData.model.table.mapping.ParsingStateHelper;
import org.robotframework.ide.core.testData.model.table.setting.TestTemplate;
import org.robotframework.ide.core.testData.text.read.ParsingState;
import org.robotframework.ide.core.testData.text.read.RobotLine;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotToken;
import org.robotframework.ide.core.testData.text.read.recognizer.RobotTokenType;


public class TestTemplateTrashDataMapper implements IParsingMapper {

    private final ParsingStateHelper stateHelper;


    public TestTemplateTrashDataMapper() {
        this.stateHelper = new ParsingStateHelper();
    }


    @Override
    public RobotToken map(final RobotLine currentLine,
            final Stack<ParsingState> processingState,
            final RobotFileOutput robotFileOutput, final RobotToken rt, final FilePosition fp,
            final String text) {
        rt.getTypes().add(0,
                RobotTokenType.SETTING_TEST_TEMPLATE_KEYWORD_UNWANTED_ARGUMENT);
        rt.setText(text);
        rt.setRaw(text);

        final SettingTable settings = robotFileOutput.getFileModel()
                .getSettingTable();
        final List<TestTemplate> templates = settings.getTestTemplates();
        if (!templates.isEmpty()) {
            templates.get(templates.size() - 1).addUnexpectedTrashArgument(rt);
        } else {
            // FIXME: some error
        }
        processingState
                .push(ParsingState.SETTING_TEST_TEMPLATE_KEYWORD_UNWANTED_ARGUMENTS);

        return rt;
    }


    @Override
    public boolean checkIfCanBeMapped(final RobotFileOutput robotFileOutput,
            final RobotLine currentLine, final RobotToken rt, final String text,
            final Stack<ParsingState> processingState) {
        boolean result;
        if (!processingState.isEmpty()) {
            final ParsingState currentState = stateHelper
                    .getCurrentStatus(processingState);
            if (currentState == ParsingState.SETTING_TEST_TEMPLATE_KEYWORD
                    || currentState == ParsingState.SETTING_TEST_TEMPLATE_KEYWORD_UNWANTED_ARGUMENTS) {
                result = true;
            } else if (currentState == ParsingState.SETTING_TEST_TEMPLATE) {
                final List<TestTemplate> testTemplates = robotFileOutput
                        .getFileModel().getSettingTable().getTestTemplates();
                if (testTemplates.size() == 1) {
                    final TestTemplate template = testTemplates.get(0);
                    result = (template.getKeywordName() != null);
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }

}
