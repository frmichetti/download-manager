/*
 * 
 */
package br.com.codecode.downloadmanager.logic;

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgressRenderer.
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 602350047457580451L;

    /**
     * Instantiates a new progress renderer.
     *
     * @param min the min
     * @param max the max
     */
    public ProgressRenderer(int min, int max) {

	super(min, max);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	    int row, int column) {

	setValue((int) ((Float) value).floatValue());
	return this;
    }

}
