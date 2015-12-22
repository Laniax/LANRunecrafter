package scripts.LANRunecrafter.UI;

import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LANRunecrafter.LANRunecrafter;
import scripts.LanAPI.Game.Persistance.Variables;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	Point start_drag, start_loc;

	public GUI() {

		setTitle("LAN Runecrafter - Settings");
		setResizable(false);
		setUndecorated(true);
		setBackground(new Color(1.0f,1.0f,1.0f,0.0f));

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBounds(new Rectangle(0, 0, 293, 267));
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setPreferredSize(new Dimension(293, 267));
		getContentPane().setLayout(null);

		altarCombobox.setOpaque(false);
		for (AbstractAltar altar : LANRunecrafter.ActiveAltars) {
			altarCombobox.addItem(altar);
		}
		altarCombobox.setBounds(130, 70, 100, 27);
		getContentPane().add(altarCombobox);

		chkPureEss.setOpaque(false);
		chkPureEss.setBounds(45, 108, 252, 61);
		getContentPane().add(chkPureEss);

		btnSave.setText("");
		btnSave.setOpaque(false);
		btnSave.setBorderPainted(false);
		btnSave.addActionListener(evt -> btnSaveSettingsClicked(evt));
		btnSave.setBounds(25, 205, 252, 61);
		getContentPane().add(btnSave);

		btnClose.setText("");
		btnClose.setOpaque(false);
		btnClose.setBorderPainted(false);
		btnClose.addActionListener(evt -> dispose());
		btnClose.setBounds(222, 17, 71, 44);
		getContentPane().add(btnClose);
		
		try {
			ImageIcon save = new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/save.png"));
			ImageIcon saveHover = new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/save-hover.png"));
			ImageIcon close = new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/close.png"));
			ImageIcon closeHover = new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/close-hover.png"));
			
			btnSave.setIcon(save);
			btnSave.setRolloverIcon(saveHover);
			btnClose.setIcon(close);
			btnClose.setRolloverIcon(closeHover);

			backgroundLabel.setIcon(new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/runecrafter_settings.png")));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		backgroundLabel.setText("Failed to load background :(");
		backgroundLabel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) { 
				backgroundMousePressed(evt);
			}
		});
		backgroundLabel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evt) {
				backgroundMouseDragged(evt);
			}
		});
		getContentPane().add(backgroundLabel);
		backgroundLabel.setBounds(0, 0, 293, 267);

		this.setLocationRelativeTo(null);
		this.toFront();
	}

	protected void backgroundMousePressed(MouseEvent evt) {
		this.start_drag = this.getScreenLocation(evt);
		this.start_loc = this.getLocation();
	}

	Point getScreenLocation(MouseEvent e) {
		Point cursor = e.getPoint();
		Point target_location = this.getLocationOnScreen();
		return new Point((int) (target_location.getX() + cursor.getX()), (int) (target_location.getY() + cursor.getY()));
	}

	protected void backgroundMouseDragged(MouseEvent evt) {
		Point current = this.getScreenLocation(evt);
		Point offset = new Point((int) current.getX() - (int) start_drag.getX(), (int) current.getY() - (int) start_drag.getY());
		Point new_location = new Point((int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc.getY() + offset.getY()));
		this.setLocation(new_location);
	}

	protected void btnSaveSettingsClicked(ActionEvent evt) {

		Variables.getInstance().addOrUpdate("altar", altarCombobox.getSelectedItem());
		Variables.getInstance().addOrUpdate("pureEssenceFallback", chkPureEss.isSelected());

		this.setVisible(false);
	}

	private JButton btnSave = new JButton();
	private JButton btnClose = new JButton();
	private JCheckBox chkPureEss = new JCheckBox();
	private JLabel backgroundLabel = new JLabel();
	private JComboBox<AbstractAltar> altarCombobox = new JComboBox<AbstractAltar>();
}
