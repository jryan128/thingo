package com.joysignalgames.bazingo.internal.pattern_creator;

import javax.swing.*;
import java.io.IOException;
import java.util.Vector;

class PatternList extends JList<Pattern> {
    PatternList() throws IOException, PatternFileLoader.PatternFileParseException {
        super(new Vector<>(PatternFileLoader.loadPatternsFromFile()));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
