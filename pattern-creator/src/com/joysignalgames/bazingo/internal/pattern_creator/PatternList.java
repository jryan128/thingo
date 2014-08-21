package com.joysignalgames.bazingo.internal.pattern_creator;

import javax.swing.*;
import java.io.IOException;

final class PatternList extends JList<Pattern> {
    PatternList() throws IOException, PatternFileLoader.PatternFileParseException {
        super(new PatternListModel());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    static class PatternListModel extends DefaultListModel<Pattern> {
        PatternListModel() throws IOException, PatternFileLoader.PatternFileParseException {
            for (Pattern pattern : PatternFileLoader.loadPatternsFromFile()) {
                addElement(pattern);
            }
        }

        public void fireContentsChanged(int index) {
            super.fireContentsChanged(this, index, index);
        }
    }

    @Override
    public void setModel(ListModel<Pattern> model) {
        if (!(model instanceof PatternListModel)) {
            throw new IllegalArgumentException("PatternList model must be a PatternListModel.");
        }
        super.setModel(model);
    }

    PatternListModel getPatternListModel() {
        return (PatternListModel) getModel();
    }
}
