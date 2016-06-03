/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package frmichetti.com.downloadmanager.logic;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import frmichetti.com.downloadmanager.util.BitsBytes;


public class DownloadsTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 2658357526299205516L;

	private static final String[] COLUMN_NAMES = {"URL", "Tamanho", "Progresso", "Estado"};

    private static final Class[] COLUMN_CLASSES = {URL.class, String.class, JProgressBar.class, Status.class};

    private ArrayList<Download> downloadList = new ArrayList<>();

    public void addDownload(Download download) {
        download.addObserver((Observer) this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void clearDownload(int row) {
        downloadList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public Download getDownload(int row) {
        return downloadList.get(row);
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return COLUMN_CLASSES[col];
    }

    @Override
    public int getRowCount() {
        return downloadList.size();
    }

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

    @Override
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);

    }

    public ArrayList<Download> getDownloadList() {
        return downloadList;

    }

    public void setDownloadList(ArrayList<Download> downloadList) {
        this.downloadList = downloadList;
        fireTableDataChanged();
        for (Download download : downloadList) {
            System.out.println(download);
            System.out.println("");

        }
    }

}
