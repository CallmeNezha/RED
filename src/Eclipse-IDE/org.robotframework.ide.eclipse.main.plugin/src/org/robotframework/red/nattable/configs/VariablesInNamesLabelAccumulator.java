/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.red.nattable.configs;

import java.util.function.Supplier;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.rf.ide.core.environment.RobotVersion;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.cases.CasesElementsLabelAccumulator;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.KeywordsElementsLabelAccumulator;

/**
 * @author lwlodarc
 *
 */
public class VariablesInNamesLabelAccumulator implements IConfigLabelAccumulator {

    public static final String POSSIBLE_VARIABLES_IN_NAMES_CONFIG_LABEL = "POSSIBLE_VARIABLES_IN_NAMES_INSIDE";

    private final Supplier<RobotVersion> versionSupplier;

    public VariablesInNamesLabelAccumulator(final Supplier<RobotVersion> versionSupplier) {
        this.versionSupplier = versionSupplier;
    }

    @Override
    public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
        if (configLabels.hasLabel(ActionNamesLabelAccumulator.ACTION_NAME_CONFIG_LABEL)
                || configLabels.hasLabel(ActionNamesLabelAccumulator.ACTION_FROM_LIB_NAME_CONFIG_LABEL)
                || configLabels.hasLabel(KeywordsElementsLabelAccumulator.KEYWORD_DEFINITION_CONFIG_LABEL)
                || (configLabels.hasLabel(CasesElementsLabelAccumulator.CASE_CONFIG_LABEL)
                        && versionSupplier.get().isNewerOrEqualTo(new RobotVersion(3, 2)))) {
            configLabels.addLabel(POSSIBLE_VARIABLES_IN_NAMES_CONFIG_LABEL);
        }
    }
}