/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package br.com.frmichetti.downloadmanager.console;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import br.com.frmichetti.downloadmanager.logic.Download;
import br.com.frmichetti.downloadmanager.logic.DownloadsTableModel;
import br.com.frmichetti.downloadmanager.logic.ProgressRenderer;
import br.com.frmichetti.downloadmanager.logic.Serialize;
import br.com.frmichetti.downloadmanager.util.CheckUrl;
import br.com.frmichetti.downloadmanager.util.Size;


public class DownloadManager extends javax.swing.JFrame implements Observer, Runnable {

	private static final long serialVersionUID = -5806855878789902475L;

	private Thread t;

	public static ThreadGroup tgroup = new ThreadGroup("Downloads");

	private DownloadsTableModel dtm = new DownloadsTableModel();

	private Download selectedDownload;

	private boolean clearing;

	private int showOpenDialog;

	private String initPath;

	private String fs;

	private File initDir;

	private javax.swing.JMenuItem MenuItem_Recuperar_lista;

	private javax.swing.JMenuItem MenuItem_Sair;

	private javax.swing.JMenuItem MenuItem_Salvar_Lista;

	private javax.swing.JButton addButton;

	private javax.swing.JTextField addTextField;

	private javax.swing.JMenu arquivoJMenu;

	private javax.swing.JButton buttonAlterarPath;

	private javax.swing.JPanel buttonsPanel;

	private javax.swing.JButton cancelButton;

	private javax.swing.JButton clearButton;

	private javax.swing.JTable downloadsTable;

	private javax.swing.JMenu editarJMenu;

	private javax.swing.JFileChooser fileChooser;

	private javax.swing.JPanel gridPanel;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JMenuBar jMenuBar1;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JLabel lbl_Salvar;

	private javax.swing.JLabel lbl_Url;

	private javax.swing.JPanel pathPanel;

	private javax.swing.JTextField pathTextField;

	private javax.swing.JButton pauseButton;

	private javax.swing.JButton restartButton;

	private javax.swing.JButton resumeButton;


	public DownloadManager() {

		t = new Thread(this, "Thread Download Manager");

		doInitPath();

		doInitComponents();

		doLoadIcons();	

		super.setLocationRelativeTo(null);

		super.setLocation((Size.MAX_WIDTH - super.getWidth()) / 2, (Size.MAX_HEIGHT - super.getHeight()) / 2);

		super.setVisible(true);

		doConfigureComponents();

		t.start();

	}

	private void doInitPath() {

		//TODO FIXME check S.O

		initPath = System.getProperty("user.home");

		fs = System.getProperty("file.separator");   
		
		System.out.println(System.getProperty("os.name"));

		if(System.getProperty("os.name").contains("windows")){

			initPath += fs + "Desktop/Downloads";

		}else{
			
			initPath += fs + "/Downloads";

		}	

		initDir = new File(initPath);

		if (!initDir.exists()) {

			initDir.mkdir();

			JOptionPane.showMessageDialog(this, "Diretorio Downloads Criado em " + initDir.getParent());
		}

	}

	private void doConfigureComponents() {
		fileChooser.setSelectedFile(initDir);
		fileChooser.setCurrentDirectory(initDir);
		actionRecoverDownloadList();
	}

	private void actionAdd() {

		URL verifiedURL = null;

		try {

			verifiedURL = CheckUrl.verifyURL(addTextField.getText());

		} catch (MalformedURLException ex) {

			System.err.println("URL Mal Formatada : " + ex);
		}
		if (verifiedURL != null) {

			dtm.addDownload(new Download(verifiedURL, fileChooser.getSelectedFile().toPath()));

			addTextField.setText("");

		} else {

			JOptionPane.showMessageDialog(this, "Url do Download Inválida", "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void actionPause() {
		selectedDownload.pause();
		doUpdateButtons();
	}

	private void actionResume() {
		selectedDownload.resume();
		doUpdateButtons();

	}

	private void actionCancel() {
		selectedDownload.cancel();
		doUpdateButtons();
	}

	private void actionClear() {
		if (dtm.getRowCount() > 0) {
			clearing = true;
			dtm.clearDownload(downloadsTable.getSelectedRow());
			clearing = false;
			selectedDownload = null;
			doUpdateButtons();
		} else {
			JOptionPane.showMessageDialog(this, "Lista de Downloads Vazia", "Lista de Downloads Vazia", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Lista de Downloads Vazia");
		}

	}

	private void actionRestart() {
		URL verifiedURL = null;

		try {

			verifiedURL = CheckUrl.verifyURL(selectedDownload.getUrl());

		} catch (MalformedURLException ex) {

			System.err.println("URL Mal Formatada : " + ex);
		}
		if (verifiedURL != null) {

			dtm.addDownload(new Download(verifiedURL, fileChooser.getSelectedFile().toPath()));

			addTextField.setText("");

		} else {

			JOptionPane.showMessageDialog(this, "Url do Download Inválida", "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void tableSelectionChanged() {
		if (selectedDownload != null) {
			selectedDownload.deleteObserver(this);
		}
		if (!clearing && downloadsTable.getSelectedRow() > -1) {
			selectedDownload = dtm.getDownload(downloadsTable.getSelectedRow());
			selectedDownload.addObserver(this);
			doUpdateButtons();
		}

	}

	private void doUpdateButtons() {

		if (selectedDownload != null) {

			switch (selectedDownload.getStatus()) {

			case Baixando:
				pauseButton.setEnabled(true);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				restartButton.setEnabled(false);
				break;
			case Pausado:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				restartButton.setEnabled(false);
				break;
			case Completo:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
				restartButton.setEnabled(true);
				break;
			case Cancelado:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
				restartButton.setEnabled(true);
				break;
			case Falha:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(true);
				restartButton.setEnabled(true);
				break;
			case Adicionado:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
				restartButton.setEnabled(false);
				break;
			default: {
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(false);
				restartButton.setEnabled(false);
			}
			}
		} else {

			pauseButton.setEnabled(false);
			resumeButton.setEnabled(false);
			cancelButton.setEnabled(false);
			clearButton.setEnabled(false);
			restartButton.setEnabled(false);
		}

		if (fileChooser.getSelectedFile() != null) {
			pathTextField.setText(fileChooser.getSelectedFile().toString());
		}

		actionSaveDownloadList();

	}

	private void doInitComponents() {

		fileChooser = new javax.swing.JFileChooser();

		pathPanel = new javax.swing.JPanel();

		addTextField = new javax.swing.JTextField();

		pathTextField = new javax.swing.JTextField();

		addButton = new javax.swing.JButton();

		buttonAlterarPath = new javax.swing.JButton();

		lbl_Salvar = new javax.swing.JLabel();

		lbl_Url = new javax.swing.JLabel();

		buttonsPanel = new javax.swing.JPanel();

		pauseButton = new javax.swing.JButton();

		resumeButton = new javax.swing.JButton();

		cancelButton = new javax.swing.JButton();

		clearButton = new javax.swing.JButton();

		restartButton = new javax.swing.JButton();

		gridPanel = new javax.swing.JPanel();

		jScrollPane1 = new javax.swing.JScrollPane();

		downloadsTable = new javax.swing.JTable();

		jLabel1 = new javax.swing.JLabel();

		jMenuBar1 = new javax.swing.JMenuBar();

		arquivoJMenu = new javax.swing.JMenu();

		MenuItem_Sair = new javax.swing.JMenuItem();

		editarJMenu = new javax.swing.JMenu();

		MenuItem_Recuperar_lista = new javax.swing.JMenuItem();

		MenuItem_Salvar_Lista = new javax.swing.JMenuItem();

		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setDialogTitle("Selecione um diretório para Salvar os Downloads");

		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

		fileChooser.setMaximumSize(new java.awt.Dimension(100, 100));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		setTitle("Gerenciador de Downloads[Demo]");

		setLocation(new java.awt.Point(0, 0));

		setMaximizedBounds(new java.awt.Rectangle(0, 0, 0, 0));

		setMaximumSize(new java.awt.Dimension(0, 0));

		setName("frameDM");

		setPreferredSize(new java.awt.Dimension(800, 600));

		setResizable(false);

		pathPanel.setDoubleBuffered(false);

		addTextField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				addTextFieldFocusLost(evt);
			}
		});

		addTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTextFieldActionPerformed(evt);
			}
		});

		pathTextField.setEditable(false);

		pathTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pathTextFieldActionPerformed(evt);
			}
		});

		addButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		addButton.setForeground(new java.awt.Color(51, 153, 255));	

		addButton.setText("Adicionar Download");

		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		buttonAlterarPath.setFont(new java.awt.Font("Tahoma", 1, 9));

		buttonAlterarPath.setForeground(new java.awt.Color(51, 153, 255));	

		buttonAlterarPath.setText("Alterar");

		buttonAlterarPath.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonAlterarPathActionPerformed(evt);
			}
		});

		lbl_Salvar.setFont(new java.awt.Font("Tahoma", 1, 11));

		lbl_Salvar.setForeground(new java.awt.Color(51, 153, 255));

		lbl_Salvar.setText("Salvar Downloads em.:");

		lbl_Url.setFont(new java.awt.Font("Tahoma", 1, 11));

		lbl_Url.setForeground(new java.awt.Color(51, 153, 255));

		lbl_Url.setText("URL.:");

		javax.swing.GroupLayout pathPanelLayout = new javax.swing.GroupLayout(pathPanel);

		pathPanel.setLayout(pathPanelLayout);

		pathPanelLayout.setHorizontalGroup(
				pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pathPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(pathPanelLayout.createSequentialGroup()
										.addComponent(lbl_Salvar)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(pathTextField))
								.addGroup(pathPanelLayout.createSequentialGroup()
										.addComponent(lbl_Url)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addTextField)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(buttonAlterarPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap())
				);
		pathPanelLayout.setVerticalGroup(
				pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pathPanelLayout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lbl_Salvar)
								.addComponent(buttonAlterarPath, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(addTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(addButton)
								.addComponent(lbl_Url))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);

		buttonsPanel.setDoubleBuffered(false);

		buttonsPanel.setLayout(new java.awt.GridLayout(1, 0));

		pauseButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		pauseButton.setForeground(new java.awt.Color(51, 153, 255));

		pauseButton.setText("Pausar");

		pauseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pauseButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(pauseButton);

		resumeButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		resumeButton.setForeground(new java.awt.Color(51, 153, 255));

		resumeButton.setText("Continuar");

		resumeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resumeButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(resumeButton);

		cancelButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		cancelButton.setForeground(new java.awt.Color(51, 153, 255));	

		cancelButton.setText("Cancelar");

		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(cancelButton);

		clearButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		clearButton.setForeground(new java.awt.Color(51, 153, 255));	

		clearButton.setText("Apagar");

		clearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(clearButton);

		restartButton.setFont(new java.awt.Font("Tahoma", 1, 11));

		restartButton.setForeground(new java.awt.Color(51, 153, 255));	

		restartButton.setText("Reiniciar");

		restartButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				restartButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(restartButton);

		gridPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fila de Downloads", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(102, 153, 255)));

		gridPanel.setDoubleBuffered(false);

		gridPanel.setLayout(new java.awt.BorderLayout());

		downloadsTable.setAutoCreateRowSorter(true);

		downloadsTable.setFont(new java.awt.Font("Tahoma", 0, 10));

		downloadsTable.setModel(dtm);

		downloadsTable.getSelectionModel().addListSelectionListener((e) -> {
			tableSelectionChanged();
		});

		downloadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ProgressRenderer renderer = new ProgressRenderer(0, 100);

		renderer.setStringPainted(true);

		downloadsTable.setDefaultRenderer(JProgressBar.class, renderer);

		downloadsTable.setRowHeight((int) renderer.getPreferredSize().getHeight());

		jScrollPane1.setViewportView(downloadsTable);

		gridPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 10));

		jLabel1.setForeground(new java.awt.Color(0, 153, 204));

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		jLabel1.setText("by Felipe Rodrigues Michetti - (is Hiring) ? \"Mail Me - feliperm@gmail.com\" : \"Visit : http://www.codecode.com.br/\"");

		arquivoJMenu.setText("Arquivo");

		MenuItem_Sair.setText("Sair");

		MenuItem_Sair.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MenuItem_SairActionPerformed(evt);
			}
		});

		arquivoJMenu.add(MenuItem_Sair);

		jMenuBar1.add(arquivoJMenu);

		editarJMenu.setText("Editar");

		MenuItem_Recuperar_lista.setText("Recuperar Lista");

		MenuItem_Recuperar_lista.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MenuItem_Recuperar_listaActionPerformed(evt);
			}
		});

		editarJMenu.add(MenuItem_Recuperar_lista);

		MenuItem_Salvar_Lista.setText("Salvar Lista");

		MenuItem_Salvar_Lista.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MenuItem_Salvar_ListaActionPerformed(evt);
			}
		});

		editarJMenu.add(MenuItem_Salvar_Lista);

		jMenuBar1.add(editarJMenu);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(gridPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pathPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
												.addComponent(buttonsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
						.addContainerGap())
				);

		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(pathPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);

		pack();
	}

	public void doLoadIcons() {

		URL url =getClass().getResource(".");

		try {

			URI uri = url.toURI();

			System.out.println(uri);

		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}




		try {

			restartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/control_repeat_blue.png")));

			clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/cancel.png")));

			cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/control_stop_blue.png")));

			resumeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/control_play_blue.png")));

			pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/control_pause_blue.png")));

			buttonAlterarPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/folder_link.png")));

			addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("./icons/add.png")));
			
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(this, "Não foi possível carregar os ícones " + e);
		}


	}

	private byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
		{
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}

	private void addTextFieldFocusLost(java.awt.event.FocusEvent evt) {

	}

	private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		actionPause();
	}

	private void resumeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		actionResume();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		actionCancel();
	}

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
		actionClear();
	}

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {

		if (fileChooser.getSelectedFile() != null) {
			actionAdd();
			doUpdateButtons();
		} else {
			JOptionPane.showMessageDialog(this, "Você precisa Indicar um Diretório para Salvar ");
			showOpenDialog = fileChooser.showOpenDialog(this);

			if (showOpenDialog == 0) {

				pathTextField.setText(fileChooser.getSelectedFile().toString());
			}
		}


	}

	private void buttonAlterarPathActionPerformed(java.awt.event.ActionEvent evt) {

		showOpenDialog = fileChooser.showOpenDialog(this);

		if (showOpenDialog == 0) {

			pathTextField.setText(fileChooser.getSelectedFile().toString());
		}
	}

	private void restartButtonActionPerformed(java.awt.event.ActionEvent evt) {
		actionRestart();
	}

	private void MenuItem_SairActionPerformed(java.awt.event.ActionEvent evt) {
		actionExit();
	}

	private void pathTextFieldActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void addTextFieldActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void MenuItem_Recuperar_listaActionPerformed(java.awt.event.ActionEvent evt) {
		actionRecoverDownloadList();
	}

	private void MenuItem_Salvar_ListaActionPerformed(java.awt.event.ActionEvent evt) {
		actionSaveDownloadList();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (selectedDownload != null && selectedDownload.equals(0)) {
			SwingUtilities.invokeLater(() -> {
				doUpdateButtons();
			});

		}
	}

	@Override
	public void run() {
		while (true) {
			doUpdateButtons();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				System.err.println("Erro de Interrupção da Thread : " + ex.getMessage());
			}

		}
	}

	private void actionExit() {
		System.exit(0);
	}

	private void actionSaveDownloadList() {

		Serialize.writeobject(dtm.getDownloadList(), new File(fileChooser.getSelectedFile(), "download_list"));

		/*if (Serialize.writeobject(dtm.getDownloadList(), new File(fileChooser.getSelectedFile(), "download_list"))) {
            JOptionPane.showMessageDialog(this, "Lista de Downloads Salva", "Salvar Lista de Downloads", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao Salvar a Lista", "Salvar Lista de Downloads", JOptionPane.ERROR_MESSAGE);
        }*/
	}

	private void actionRecoverDownloadList() {

		ArrayList<Download> recoverList = Serialize.readObject(new File(fileChooser.getSelectedFile(), "download_list"));
		if (recoverList != null) {
			dtm.setDownloadList(recoverList);
			if (recoverList.size() > 0) {
				dtm.fireTableRowsUpdated(0, recoverList.size() - 1);
			}

		}

	}

}
