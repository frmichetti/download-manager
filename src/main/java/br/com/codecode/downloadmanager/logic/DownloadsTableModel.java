/*
 * 
 */
package br.com.codecode.downloadmanager.logic;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import br.com.codecode.downloadmanager.util.BitsBytes;


// TODO: Auto-generated Javadoc
/**
 * The Class DownloadsTableModel.
 */
public class DownloadsTableModel extends AbstractTableModel implements Observer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2658357526299205516L;

	/** The Constant COLUMN_NAMES. */
	private static final String[] COLUMN_NAMES = {"URL", "Tamanho", "Progresso", "Estado"};

    /** The Constant COLUMN_CLASSES. */
    private static final Class<?>[] COLUMN_CLASSES = {URL.class, String.class, JProgressBar.class, Status.class};

    /** The download list. */
    private ArrayList<Download> downloadList = new ArrayList<>();

    /**
     * Adds the download.
     *
     * @param download the download
     */
    public void addDownload(Download download) {
        download.addObserver((Observer) this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    /**
     * Clear download.
     *
     * @param row the row
     */
    public void clearDownload(int row) {
        downloadList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    /**
     * Gets the download.
     *
     * @param row the row
     * @return the download
     */
    public Download getDownload(int row) {
        return downloadList.get(row);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int col) {
        return COLUMN_CLASSES[col];
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Download download = downloadList.get(rowIndex);

        switch (columnIndex) {
            case 0: {
                return download.getUrl();
            }
            case 1: {
                int size = download.getSize();

                return BitsBytes.format(size);
            }
            case 2: {
                return download.getProgress();
            }
            case 3: {
                return download.getStatus();
            }
            default: {
                return "";
            }

        }

    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);

    }

    /**
     * Gets the download list.
     *
     * @return the download list
     */
    public ArrayList<Download> getDownloadList() {
        return downloadList;

    }

    /**
     * Sets the download list.
     *
     * @param downloadList the new download list
     */
    public void setDownloadList(ArrayList<Download> downloadList) {
        this.downloadList = downloadList;
        fireTableDataChanged();
        for (Download download : downloadList) {
            System.out.println(download);
            System.out.println("");

        }
    }

}
