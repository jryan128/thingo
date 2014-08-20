package com.joysignalgames.bazingo.internal.pattern_creator;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.IOException;
import java.util.Vector;

class PatternList extends JList<Pattern> {
    PatternList() throws IOException, PatternFileLoader.PatternFileParseException {
        super(new Vector<>(PatternFileLoader.loadPatternsFromFile()));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    System.out.println(getSelectedValue());
                }
            }
        });
    }
}
