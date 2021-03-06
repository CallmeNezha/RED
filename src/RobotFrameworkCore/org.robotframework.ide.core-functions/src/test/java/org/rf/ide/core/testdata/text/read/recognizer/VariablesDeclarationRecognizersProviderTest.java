/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.text.read.recognizer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.rf.ide.core.testdata.text.read.recognizer.variables.DictionaryVariableDeclarationRecognizer;
import org.rf.ide.core.testdata.text.read.recognizer.variables.EnvironmentVariableDeclarationRecognizer;
import org.rf.ide.core.testdata.text.read.recognizer.variables.ListVariableDeclarationRecognizer;
import org.rf.ide.core.testdata.text.read.recognizer.variables.ScalarVariableDeclarationRecognizer;

/**
 * @author lwlodarc
 *
 */
public class VariablesDeclarationRecognizersProviderTest {

    @Test
    public void getRecognizers_providesProperVariableRecognizers() {
        final List<ATokenRecognizer> recognizers = new VariablesDeclarationRecognizersProvider().getRecognizers();

        assertThat(recognizers).hasSize(4);
        assertThat(recognizers).hasAtLeastOneElementOfType(ScalarVariableDeclarationRecognizer.class);
        assertThat(recognizers).hasAtLeastOneElementOfType(ListVariableDeclarationRecognizer.class);
        assertThat(recognizers).hasAtLeastOneElementOfType(DictionaryVariableDeclarationRecognizer.class);
        assertThat(recognizers).hasAtLeastOneElementOfType(EnvironmentVariableDeclarationRecognizer.class);
    }

}
