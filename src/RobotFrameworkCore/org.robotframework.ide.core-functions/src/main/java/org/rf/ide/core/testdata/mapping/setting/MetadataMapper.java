/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.mapping.setting;

import java.util.List;
import java.util.Stack;

import org.rf.ide.core.testdata.mapping.table.ElementPositionResolver;
import org.rf.ide.core.testdata.mapping.table.ElementPositionResolver.PositionExpected;
import org.rf.ide.core.testdata.mapping.table.IParsingMapper;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.RobotFileOutput;
import org.rf.ide.core.testdata.model.table.SettingTable;
import org.rf.ide.core.testdata.model.table.setting.Metadata;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.ParsingState;
import org.rf.ide.core.testdata.text.read.RobotLine;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class MetadataMapper implements IParsingMapper {

    private final ElementPositionResolver positionResolver = new ElementPositionResolver();

    @Override
    public boolean checkIfCanBeMapped(final RobotFileOutput robotFileOutput, final RobotLine currentLine,
            final RobotToken rt, final String text, final Stack<ParsingState> processingState) {
        final List<IRobotTokenType> types = rt.getTypes();
        return containsOnlyMetadata(types) && positionResolver
                .isCorrectPosition(PositionExpected.SETTING_TABLE_ELEMENT_DECLARATION, currentLine, rt)
                && isIncludedInSettingTable(processingState);
    }

    private boolean containsOnlyMetadata(final List<IRobotTokenType> types) {
        boolean result = false;
        for (final IRobotTokenType type : types) {
            if (type == RobotTokenType.SETTING_METADATA_DECLARATION) {
                result = true;
            } else if (type != RobotTokenType.UNKNOWN) {
                return false;
            }
        }
        return result;
    }

    private boolean isIncludedInSettingTable(final Stack<ParsingState> processingState) {
        return !processingState.isEmpty()
                && processingState.get(processingState.size() - 1) == ParsingState.SETTING_TABLE_INSIDE;
    }

    @Override
    public RobotToken map(final RobotLine currentLine, final Stack<ParsingState> processingState,
            final RobotFileOutput robotFileOutput, final RobotToken rt, final FilePosition fp, final String text) {

        rt.setType(RobotTokenType.SETTING_METADATA_DECLARATION);
        rt.setText(text);

        final SettingTable settings = robotFileOutput.getFileModel().getSettingTable();
        settings.addMetadata(new Metadata(rt));

        processingState.push(ParsingState.SETTING_METADATA);
        return rt;
    }
}
