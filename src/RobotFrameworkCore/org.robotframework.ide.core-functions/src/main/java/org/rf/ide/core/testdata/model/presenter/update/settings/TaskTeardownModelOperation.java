/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.presenter.update.settings;

import java.util.List;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.presenter.update.ISettingTableElementOperation;
import org.rf.ide.core.testdata.model.table.SettingTable;
import org.rf.ide.core.testdata.model.table.setting.TaskTeardown;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class TaskTeardownModelOperation extends KeywordBaseModelOperations implements ISettingTableElementOperation {

    @Override
    public boolean isApplicable(final IRobotTokenType elementType) {
        return elementType == RobotTokenType.SETTING_TASK_TEARDOWN_DECLARATION;
    }

    @Override
    public boolean isApplicable(final ModelType elementType) {
        return elementType == ModelType.SUITE_TASK_TEARDOWN;
    }

    @Override
    public AModelElement<?> create(final SettingTable settingsTable, final int tableIndex, final List<String> args,
            final String comment) {
        return super.create(settingsTable.newTaskTeardown(), args, comment);
    }

    @Override
    public void update(final AModelElement<?> modelElement, final int index, final String value) {
        super.update((TaskTeardown) modelElement, index, value);
    }
    
    @Override
    public void insert(final SettingTable settingsTable, final int index, final AModelElement<?> modelElement) {
        settingsTable.addTaskTeardown((TaskTeardown) modelElement);
    }

    @Override
    public void remove(final SettingTable settingsTable, final AModelElement<?> modelElements) {
        settingsTable.removeTaskTeardown();
    }
}
