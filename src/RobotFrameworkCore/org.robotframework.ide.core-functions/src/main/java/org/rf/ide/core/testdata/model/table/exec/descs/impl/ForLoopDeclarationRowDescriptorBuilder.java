/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table.exec.descs.impl;

import java.util.List;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.FileRegion;
import org.rf.ide.core.testdata.model.RobotFileOutput;
import org.rf.ide.core.testdata.model.RobotFileOutput.BuildMessage;
import org.rf.ide.core.testdata.model.table.ARobotSectionTable;
import org.rf.ide.core.testdata.model.table.IExecutableStepsHolder;
import org.rf.ide.core.testdata.model.table.RobotExecutableRow;
import org.rf.ide.core.testdata.model.table.exec.descs.ForDescriptorInfo;
import org.rf.ide.core.testdata.model.table.exec.descs.IExecutableRowDescriptor;
import org.rf.ide.core.testdata.model.table.exec.descs.IRowDescriptorBuilder;
import org.rf.ide.core.testdata.model.table.exec.descs.VariableExtractor;
import org.rf.ide.core.testdata.model.table.exec.descs.ast.mapping.MappingResult;
import org.rf.ide.core.testdata.model.table.exec.descs.ast.mapping.VariableDeclaration;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class ForLoopDeclarationRowDescriptorBuilder implements IRowDescriptorBuilder {

    @Override
    public <T> boolean isAcceptable(final RobotExecutableRow<T> execRowLine) {
        return execRowLine.getAction().getTypes().contains(RobotTokenType.FOR_TOKEN)
                && execRowLine.getParent() instanceof IExecutableStepsHolder<?>;
    }

    @Override
    public <T> IExecutableRowDescriptor<T> buildDescription(final RobotExecutableRow<T> execRowLine) {
        final ForLoopDeclarationRowDescriptor<T> loopDescriptor = new ForLoopDeclarationRowDescriptor<>(execRowLine);

        final RobotFileOutput rfo = getFileOutput(execRowLine);
        final String fileName = rfo.getProcessedFile().getAbsolutePath();

        final VariableExtractor varExtractor = new VariableExtractor();
        final List<RobotToken> lineElements = execRowLine.getElementTokens();
        boolean wasFor = false;
        boolean wasIn = false;
        boolean wasElementsToIterate = false;
        for (final RobotToken elem : lineElements) {
            final MappingResult mappingResult = elem.getTypes().contains(RobotTokenType.START_HASH_COMMENT)
                    || elem.getTypes().contains(RobotTokenType.COMMENT_CONTINUE) ? new MappingResult(null, null)
                            : varExtractor.extract(elem, fileName);

            loopDescriptor.addMessages(mappingResult.getMessages());

            // value is keyword if is on the first place and have in it nested
            // variables and when contains text on the beginning or end of field
            final List<VariableDeclaration> correctVariables = mappingResult.getCorrectVariables();

            if (wasFor) {
                if (wasIn) {
                    loopDescriptor.addUsedVariables(correctVariables);
                    loopDescriptor.addTextParameters(mappingResult.getTextElements());
                    wasElementsToIterate = true;
                } else {
                    if (ForDescriptorInfo.isInToken(elem)) {
                        loopDescriptor.setInAction(elem.copy());
                        wasIn = true;
                    } else {
                        loopDescriptor.addCreatedVariables(correctVariables);

                        if (!mappingResult.getTextElements().isEmpty() || correctVariables.size() > 1) {
                            final FilePosition startFilePosition = elem.getFilePosition();
                            final FilePosition end = new FilePosition(startFilePosition.getLine(), elem.getEndColumn(),
                                    elem.getStartOffset() + elem.getText().length());
                            final BuildMessage errorMessage = BuildMessage.createErrorMessage(
                                    "Invalid FOR loop variable \'" + elem.getText().toString() + "\'", fileName,
                                    new FileRegion(startFilePosition, end));
                            loopDescriptor.addMessage(errorMessage);
                        }
                    }
                }
            } else {
                if (elem.getTypes().contains(RobotTokenType.FOR_TOKEN)) {
                    loopDescriptor.setAction(elem.copy());
                    wasFor = true;
                } else {
                    throw new IllegalStateException("Internal problem - FOR should be the first token.");
                }
            }
        }

        if (!wasIn || !wasElementsToIterate) {
            final RobotToken forToken = loopDescriptor.getAction();
            final FilePosition startFilePosition = forToken.getFilePosition();
            final FilePosition endFilePosition = new FilePosition(startFilePosition.getLine(), forToken.getEndColumn(),
                    forToken.getStartOffset() + forToken.getText().length());
            final BuildMessage errorMessage = BuildMessage.createErrorMessage(
                    "Invalid FOR loop - missing values to iterate", fileName,
                    new FileRegion(startFilePosition, endFilePosition));
            loopDescriptor.addMessage(errorMessage);
        }

        return loopDescriptor;
    }

    private <T> RobotFileOutput getFileOutput(final RobotExecutableRow<T> execRowLine) {
        final AModelElement<?> execParent = (AModelElement<?>) execRowLine.getParent();
        final ARobotSectionTable table = (ARobotSectionTable) execParent.getParent();
        return table.getParent().getParent();
    }
}
