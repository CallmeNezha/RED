/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.views.documentation.inputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robotframework.red.junit.jupiter.ProjectExtension.createFile;
import static org.robotframework.red.junit.jupiter.ProjectExtension.getFile;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rf.ide.core.environment.IRuntimeEnvironment;
import org.rf.ide.core.libraries.Documentation.DocFormat;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModel;
import org.robotframework.ide.eclipse.main.plugin.model.RobotProject;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSetting;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSettingsSection;
import org.robotframework.ide.eclipse.main.plugin.project.editor.libraries.Libraries;
import org.robotframework.red.junit.jupiter.Project;
import org.robotframework.red.junit.jupiter.ProjectExtension;


@ExtendWith(ProjectExtension.class)
public class LibraryImportSettingInputTest {

    @Project
    static IProject project;

    private RobotModel model;

    @BeforeEach
    public void beforeTest() {
        model = new RobotModel();
        final RobotProject robotProject = model.createRobotProject(project);
        robotProject.setStandardLibraries(new HashMap<>());
        robotProject.setReferencedLibraries(Libraries.createRefLib("lib", "Lib Kw 1", "Lib Kw 2"));
    }

    @AfterEach
    public void afterTest() {
        model.createSuiteFile(getFile(project, "suite.robot")).dispose();
    }

    @Test
    public void exceptionIsThrown_whenPreparingInputButLibraryDoesNotExist() throws Exception {
        final RobotSetting libImport = createLibraryImportSetting("unknown_lib");
        final LibraryImportSettingInput input = new LibraryImportSettingInput(libImport);

        assertThatExceptionOfType(DocumentationInputGenerationException.class).isThrownBy(() -> input.prepare())
                .withMessage("Library specification not found, nothing to display");
    }

    @Test
    public void properLibraryDocUriIsProvidedForInput() throws Exception {
        final RobotSetting libImport = createLibraryImportSetting("lib");
        final LibraryImportSettingInput input = new LibraryImportSettingInput(libImport);
        input.prepare();

        assertThat(input.getInputUri().toString())
                .isEqualTo("library:/LibraryImportSettingInputTest/lib?show_doc=true");
    }

    @Test
    public void theInputContainsGivenProjectAndImportSetting() throws Exception {
        final RobotSetting libImport = createLibraryImportSetting("lib");
        final LibraryImportSettingInput input = new LibraryImportSettingInput(libImport);

        assertThat(input.contains("lib")).isFalse();
        assertThat(input.contains(libImport.getSuiteFile())).isFalse();
        assertThat(input.contains(libImport)).isTrue();
        assertThat(input.contains(libImport.getSuiteFile().getFile().getProject())).isTrue();
    }

    @Test
    public void properHtmlIsReturned_forLibraryImport() throws Exception {
        final IRuntimeEnvironment env = mock(IRuntimeEnvironment.class);
        when(env.createHtmlDoc(any(String.class), eq(DocFormat.ROBOT))).thenReturn("doc");

        final RobotSetting libImport = createLibraryImportSetting("lib");
        final LibraryImportSettingInput input = new LibraryImportSettingInput(libImport);
        input.prepare();

        final String html = input.provideHtml(env);
        assertThat(html)
                .contains("<a href=\"library:/LibraryImportSettingInputTest/lib?show_source=true\">lib</a>");
        assertThat(html).contains("global");
        assertThat(html).contains("1.0");
        assertThat(html).contains("[]");
        assertThat(html).containsPattern("<h\\d.*>Introduction</h\\d>");
        assertThat(html).containsPattern("<h\\d.*>Shortcuts</h\\d>");
        assertThat(html).containsPattern("Lib Kw 1.*Lib Kw 2");
    }

    @Test
    public void properRawDocumentationIsReturned_forLibraryImport() throws Exception {
        final RobotSetting libImport = createLibraryImportSetting("lib");
        final LibraryImportSettingInput input = new LibraryImportSettingInput(libImport);
        input.prepare();

        final String raw = input.provideRawText();

        assertThat(raw).contains("Version: 1.0");
        assertThat(raw).contains("Scope: global");
        assertThat(raw).contains("Arguments: []");
        assertThat(raw).contains("library documentation");
    }

    private RobotSetting createLibraryImportSetting(final String libraryNameOrPath) throws Exception {
        final IFile file = createFile(project, "suite.robot",
                "*** Settings ***",
                "Library  " + libraryNameOrPath);
        return (RobotSetting) model.createSuiteFile(file)
                .findSection(RobotSettingsSection.class)
                .get()
                .getChildren()
                .get(0);
    }
}
