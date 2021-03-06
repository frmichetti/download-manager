/*
 * 
 */
package br.com.codecode.downloadmanager.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Observable;
import javax.swing.JOptionPane;

import br.com.codecode.downloadmanager.console.DownloadManager;

// TODO: Auto-generated Javadoc
/**
 * The Class Download.
 */
public class Download extends Observable implements Runnable, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2765560011215318296L;

    /** The Constant MAX_BUFFER_SIZE. */
    private transient static final int MAX_BUFFER_SIZE = 1024;

    /** The url. */
    private final URL url;
    
    /** The size. */
    private int size;
    
    /** The downloaded. */
    private int downloaded;
    
    /** The status. */
    private Status status;
    
    /** The path. */
    private transient final Path path;

    /**
     * Instantiates a new download.
     *
     * @param url the url
     * @param path the path
     */
    public Download(URL url, Path path) {
	this.url = url;
	this.size = -1;
	this.downloaded = 0;
	this.status = Status.Adicionado;
	this.path = path;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
	return url.toString();
    }

    /**
     * Gets the progress.
     *
     * @return the progress
     */
    public float getProgress() {
	return ((float) downloaded / size) * 100;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
	return size;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public Status getStatus() {
	return status;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public Path getPath() {
	return path;
    }

    /**
     * Download.
     */
    public void download() {
	Thread thread = new Thread(DownloadManager.tgroup, this);
	thread.start();
    }

    /**
     * Pause.
     */
    public void pause() {
	status = Status.Pausado;
	stateChanged();
    }

    /**
     * Resume.
     */
    public void resume() {
	status = Status.Baixando;
	stateChanged();
	download();
    }

    /**
     * Cancel.
     */
    public void cancel() {
	status = Status.Cancelado;
	stateChanged();
    }

    /**
     * Error.
     *
     * @param error the error
     * @return the string
     */
    public String error(String error) {
	status = Status.Falha;
	stateChanged();
	JOptionPane.showMessageDialog(null, error, "Ocorreu um Erro", JOptionPane.ERROR_MESSAGE);
	return error;
    }

    /**
     * Restart.
     */
    public void restart() {
	cancel();
	resume();
    }

    /**
     * Gets the file name.
     *
     * @param url the url
     * @return the file name
     */
    private String getFileName(URL url) {
	String fileName = url.getFile();
	return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    /**
     * State changed.
     */
    private void stateChanged() {
	setChanged();
	notifyObservers();
    }

    /**
     * Download task.
     */
    private void downloadTask() {
	//STEP CONNECTION --------------------------------------     
	HttpURLConnection connection = null;
	try {

	    connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
	    //  connection.setConnectTimeout(15);

	    System.out.println("TIMEOUT : " + connection.getConnectTimeout());
	    System.out.println("Range" + "bytes=" + downloaded);
	    System.out.println("");
	    connection.connect();

	    System.out.println("RESPONSE MESSAGE : " + connection.getResponseMessage());
	    System.out.println("RESPONSE CODE : " + connection.getResponseCode());
	    System.out.println("RESPONSE CONTENT TYPE : " + connection.getContentType());
	    System.out.println("RESPONSE CONTENT LENGHT : " + connection.getContentLength());
	    System.out.println("");

	    if (connection.getResponseCode() / 100 != 2) {
		error("Arquivo não Encontrado");
		System.out.println("Arquivo não Encontrado");
	    }

	    int contentLength = connection.getContentLength();

	    if (contentLength < 1) {
		error("Arquivo de Tamanho 0");
		System.out.println("Arquivo de Tamanho 0");
	    }

	    if (size == -1) {
		size = contentLength;
		stateChanged();
	    }

	} catch (IOException ex) {
	    error("Erro de IO : " + ex.getMessage());
	    System.err.println("Erro de IO : " + ex.getMessage());
	}
	//STEP CONNECTION --------------------------------------

	File f = new File(getPath().toFile(), getFileName(url));

	if (f.exists()) {

	    System.out.println("ARQUIVO JA EXISTE");
	    JOptionPane.showMessageDialog(null, "Download já existente , retomando...", "Download Retomado", JOptionPane.INFORMATION_MESSAGE);

	} else {

	    try {

		System.out.println("ARQUIVO NAO EXISTE");
		System.out.println(f.createNewFile());

	    } catch (IOException ex) {

		error("Erro de IO : " + ex.getMessage());
		System.err.println("Erro de IO : " + ex.getMessage());
	    }

	}

	System.out.println("EXECUTE ? " + f.canExecute());
	System.out.println("READ ? " + f.canRead());
	System.out.println("WRITE ? " + f.canWrite());
	System.out.println("Diretório do Temp: " + f.getPath());

	try (RandomAccessFile raf = new RandomAccessFile(f, "rw");
		InputStream stream = connection.getInputStream()) {

	    byte[] buffer;

	    raf.seek(downloaded);

	    while (status == Status.Baixando) {

		if ((size - downloaded) > MAX_BUFFER_SIZE) {

		    buffer = new byte[MAX_BUFFER_SIZE];

		} else {

		    buffer = new byte[size - downloaded];

		}

		int read = stream.read(buffer);

		if (read == -1) {
		    break;
		}

		raf.write(buffer, 0, read);
		downloaded += read;
		stateChanged();
	    }

	    if (status == Status.Baixando) {
		status = Status.Completo;
		stateChanged();
		Files.copy(f.toPath(), new File((getPath().toString() + FileSystems.getDefault().getSeparator()
			+ getFileName(url))).toPath(), REPLACE_EXISTING);
	    }

	} catch (FileNotFoundException ex) {
	    error("Não foi possível mover ou criar o arquivo : " + ex.getMessage());
	    System.err.println("Não foi possível mover ou criar o arquivo : " + ex.getMessage());

	} catch (IOException ex) {
	    error("Erro de IO : " + ex);
	    System.err.println("Erro de IO : " + ex);
	}

    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	downloadTask();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Download{" + "url=" + url + ", size=" + size + ", downloaded=" + downloaded + ", status=" + status + ", path=" + path + '}';
    }

}
