package br.com.codecode.downloadmanager.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import br.com.codecode.downloadmanager.logic.Download;
import br.com.codecode.downloadmanager.logic.DownloadsTableModel;
import br.com.codecode.downloadmanager.logic.ProgressRenderer;
import br.com.codecode.downloadmanager.logic.Serialize;
import br.com.codecode.downloadmanager.util.CheckUrl;
import br.com.codecode.downloadmanager.util.Size;


public class DownloadManager extends JFrame implements Observer, Runnable {

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

	private JMenuItem MenuItem_Recuperar_lista;

	private JMenuItem MenuItem_Sair;

	private JMenuItem MenuItem_Salvar_Lista;

	private JButton addButton;

	private JTextField addTextField;

	private JMenu arquivoJMenu;

	private JButton buttonAlterarPath;

	private JPanel buttonsPanel;

	private JButton cancelButton;

	private JButton clearButton;

	private JTable downloadsTable;

	private JMenu editarJMenu;

	private JFileChooser fileChooser;

	private JPanel gridPanel;

	private JLabel jLabel1;

	private JMenuBar jMenuBar1;

	private JScrollPane jScrollPane1;

	private JLabel lbl_Salvar;

	private JLabel lbl_Url;

	private JPanel pathPanel;

	private JTextField pathTextField;

	private JButton pauseButton;

	private JButton restartButton;

	private JButton resumeButton;


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

		if(System.getProperty("os.name").contains("Windows")){

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

		fileChooser = new JFileChooser();

		pathPanel = new JPanel();

		addTextField = new JTextField();

		pathTextField = new JTextField();

		addButton = new JButton();

		buttonAlterarPath = new JButton();

		lbl_Salvar = new JLabel();

		lbl_Url = new JLabel();

		buttonsPanel = new JPanel();

		pauseButton = new JButton();

		resumeButton = new JButton();

		cancelButton = new JButton();

		clearButton = new JButton();

		restartButton = new JButton();

		gridPanel = new JPanel();

		jScrollPane1 = new JScrollPane();

		downloadsTable = new JTable();

		jLabel1 = new JLabel();

		jMenuBar1 = new JMenuBar();

		arquivoJMenu = new JMenu();

		MenuItem_Sair = new JMenuItem();

		editarJMenu = new JMenu();

		MenuItem_Recuperar_lista = new JMenuItem();

		MenuItem_Salvar_Lista = new JMenuItem();

		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setDialogTitle("Selecione um diretório para Salvar os Downloads");

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileChooser.setMaximumSize(new Dimension(100, 100));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setTitle("Gerenciador de Downloads[Demo]");

		setLocation(new Point(0, 0));

		setMaximizedBounds(new Rectangle(0, 0, 0, 0));

		setMaximumSize(new Dimension(0, 0));

		setName("frameDM");

		setPreferredSize(new Dimension(800, 600));

		setResizable(false);

		pathPanel.setDoubleBuffered(false);

		addTextField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				addTextFieldFocusLost(evt);
			}
		});

		addTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addTextFieldActionPerformed(evt);
			}
		});

		pathTextField.setEditable(false);

		pathTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pathTextFieldActionPerformed(evt);
			}
		});

		addButton.setFont(new Font("Tahoma", 1, 11));

		addButton.setForeground(new Color(51, 153, 255));	

		addButton.setText("Adicionar Download");

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		buttonAlterarPath.setFont(new Font("Tahoma", 1, 9));

		buttonAlterarPath.setForeground(new Color(51, 153, 255));	

		buttonAlterarPath.setText("Alterar");

		buttonAlterarPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonAlterarPathActionPerformed(evt);
			}
		});

		lbl_Salvar.setFont(new Font("Tahoma", 1, 11));

		lbl_Salvar.setForeground(new Color(51, 153, 255));

		lbl_Salvar.setText("Salvar Downloads em.:");

		lbl_Url.setFont(new Font("Tahoma", 1, 11));

		lbl_Url.setForeground(new Color(51, 153, 255));

		lbl_Url.setText("URL.:");

		GroupLayout pathPanelLayout = new GroupLayout(pathPanel);

		pathPanel.setLayout(pathPanelLayout);

		pathPanelLayout.setHorizontalGroup(
				pathPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pathPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(pathPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(pathPanelLayout.createSequentialGroup()
										.addComponent(lbl_Salvar)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(pathTextField))
								.addGroup(pathPanelLayout.createSequentialGroup()
										.addComponent(lbl_Url)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addTextField)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(pathPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(buttonAlterarPath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap())
				);
		pathPanelLayout.setVerticalGroup(
				pathPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pathPanelLayout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addGroup(pathPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(pathTextField, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
								.addComponent(lbl_Salvar)
								.addComponent(buttonAlterarPath, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(pathPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(addTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(addButton)
								.addComponent(lbl_Url))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);

		buttonsPanel.setDoubleBuffered(false);

		buttonsPanel.setLayout(new GridLayout(1, 0));

		pauseButton.setFont(new Font("Tahoma", 1, 11));

		pauseButton.setForeground(new Color(51, 153, 255));

		pauseButton.setText("Pausar");

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pauseButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(pauseButton);

		resumeButton.setFont(new Font("Tahoma", 1, 11));

		resumeButton.setForeground(new Color(51, 153, 255));

		resumeButton.setText("Continuar");

		resumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				resumeButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(resumeButton);

		cancelButton.setFont(new Font("Tahoma", 1, 11));

		cancelButton.setForeground(new Color(51, 153, 255));	

		cancelButton.setText("Cancelar");

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(cancelButton);

		clearButton.setFont(new Font("Tahoma", 1, 11));

		clearButton.setForeground(new Color(51, 153, 255));	

		clearButton.setText("Apagar");

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(clearButton);

		restartButton.setFont(new Font("Tahoma", 1, 11));

		restartButton.setForeground(new Color(51, 153, 255));	

		restartButton.setText("Reiniciar");

		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				restartButtonActionPerformed(evt);
			}
		});

		buttonsPanel.add(restartButton);

		gridPanel.setBorder(BorderFactory.createTitledBorder(null, "Fila de Downloads", TitledBorder.DEFAULT_JUSTIFICATION, 
				TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 11), new Color(102, 153, 255)));

		gridPanel.setDoubleBuffered(false);

		gridPanel.setLayout(new BorderLayout());

		downloadsTable.setAutoCreateRowSorter(true);

		downloadsTable.setFont(new Font("Tahoma", 0, 10));

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

		gridPanel.add(jScrollPane1, BorderLayout.CENTER);

		jLabel1.setFont(new Font("Tahoma", 1, 10));

		jLabel1.setForeground(new Color(0, 153, 204));

		jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

		jLabel1.setText("by Felipe Rodrigues Michetti - (is Hiring) ? \"Mail Me - feliperm@gmail.com\" : \"Visit : http://www.codecode.com.br/\"");

		arquivoJMenu.setText("Arquivo");

		MenuItem_Sair.setText("Sair");

		MenuItem_Sair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MenuItem_SairActionPerformed(evt);
			}
		});

		arquivoJMenu.add(MenuItem_Sair);

		jMenuBar1.add(arquivoJMenu);

		editarJMenu.setText("Editar");

		MenuItem_Recuperar_lista.setText("Recuperar Lista");

		MenuItem_Recuperar_lista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MenuItem_Recuperar_listaActionPerformed(evt);
			}
		});

		editarJMenu.add(MenuItem_Recuperar_lista);

		MenuItem_Salvar_Lista.setText("Salvar Lista");

		MenuItem_Salvar_Lista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MenuItem_Salvar_ListaActionPerformed(evt);
			}
		});

		editarJMenu.add(MenuItem_Salvar_Lista);

		jMenuBar1.add(editarJMenu);

		setJMenuBar(jMenuBar1);

		GroupLayout layout = new GroupLayout(getContentPane());

		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(gridPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pathPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
												.addComponent(buttonsPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
						.addContainerGap())
				);

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(pathPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(gridPanel, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(buttonsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);

		pack();
	}

	public void doLoadIcons() {	


		try {

			restartButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/control_repeat_blue.png"))));

			clearButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/cancel.png"))));

			cancelButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/control_stop_blue.png"))));

			resumeButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/control_play_blue.png"))));

			pauseButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/control_pause_blue.png"))));

			buttonAlterarPath.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/folder_link.png"))));

			addButton.setIcon(new ImageIcon(ImageIO.read(ClassLoader.class.getResourceAsStream("/add.png"))));
			
		} catch (IOException e) {

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

	private void addTextFieldFocusLost(FocusEvent evt) {

	}

	private void pauseButtonActionPerformed(ActionEvent evt) {
		actionPause();
	}

	private void resumeButtonActionPerformed(ActionEvent evt) {
		actionResume();
	}

	private void cancelButtonActionPerformed(ActionEvent evt) {
		actionCancel();
	}

	private void clearButtonActionPerformed(ActionEvent evt) {
		actionClear();
	}

	private void addButtonActionPerformed(ActionEvent evt) {

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

	private void buttonAlterarPathActionPerformed(ActionEvent evt) {

		showOpenDialog = fileChooser.showOpenDialog(this);

		if (showOpenDialog == 0) {

			pathTextField.setText(fileChooser.getSelectedFile().toString());
		}
	}

	private void restartButtonActionPerformed(ActionEvent evt) {
		actionRestart();
	}

	private void MenuItem_SairActionPerformed(ActionEvent evt) {
		actionExit();
	}

	private void pathTextFieldActionPerformed(ActionEvent evt) {

	}

	private void addTextFieldActionPerformed(ActionEvent evt) {

	}

	private void MenuItem_Recuperar_listaActionPerformed(ActionEvent evt) {
		actionRecoverDownloadList();
	}

	private void MenuItem_Salvar_ListaActionPerformed(ActionEvent evt) {
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
