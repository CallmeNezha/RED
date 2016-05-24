/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table.setting.views;

import java.util.List;

import org.rf.ide.core.testdata.model.table.setting.SuiteTeardown;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;

public class SuiteTeardownView extends SuiteTeardown {

    private final List<SuiteTeardown> teardowns;

    private final boolean changeForceRebuild;

    public SuiteTeardownView(final List<SuiteTeardown> teardowns) {
        this(teardowns, false);
    }

    public SuiteTeardownView(final List<SuiteTeardown> teardowns, final boolean changeForceRebuild) {
        super(teardowns.get(0).getDeclaration());
        this.teardowns = teardowns;
        this.changeForceRebuild = changeForceRebuild;

        // join setup for this view
        final SuiteTeardown teardown = new SuiteTeardown(getDeclaration());
        OneSettingJoinerHelper.joinKeywordBase(teardown, teardowns);
        copyWithoutJoinIfNeededExecution(teardown);
    }

    protected boolean isForceRebuild() {
        return changeForceRebuild;
    }

    private void copyWithoutJoinIfNeededExecution(final SuiteTeardown teardown) {
        super.setKeywordName(teardown.getKeywordName());
        for (final RobotToken arg : teardown.getArguments()) {
            super.addArgument(arg);
        }

        for (final RobotToken commentText : teardown.getComment()) {
            super.addCommentPart(commentText);
        }
    }

    @Override
    public void setKeywordName(final String keywordName) {
        if (isForceRebuild()) {
            joinIfNeeded();
        }
        super.setKeywordName(keywordName);
    }

    @Override
    public void setKeywordName(final RobotToken keywordName) {
        if (isForceRebuild()) {
            joinIfNeeded();
        }
        super.setKeywordName(keywordName);
    }

    @Override
    public void addArgument(final String argument) {
        joinIfNeeded();
        super.addArgument(argument);
    }

    @Override
    public void addArgument(final RobotToken argument) {
        joinIfNeeded();
        super.addArgument(argument);
    }

    @Override
    public void setArgument(final int index, final String argument) {
        if (super.getArguments().size() <= index || isForceRebuild()) {
            joinIfNeeded();
        }
        super.setArgument(index, argument);
    }

    @Override
    public void setArgument(final int index, final RobotToken argument) {
        if (super.getArguments().size() <= index || isForceRebuild()) {
            joinIfNeeded();
        }
        super.setArgument(index, argument);
    }

    @Override
    public void setComment(final String comment) {
        joinIfNeeded();
        super.setComment(comment);
    }

    @Override
    public void setComment(final RobotToken rt) {
        joinIfNeeded();
        super.setComment(rt);
    }

    @Override
    public void addCommentPart(final RobotToken rt) {
        joinIfNeeded();
        super.addCommentPart(rt);
    }

    private synchronized void joinIfNeeded() {
        if (teardowns.size() > 1) {
            SuiteTeardown joined = new SuiteTeardown(getDeclaration());
            OneSettingJoinerHelper.joinKeywordBase(joined, teardowns);
            teardowns.clear();
            teardowns.add(joined);
        }
    }
}
