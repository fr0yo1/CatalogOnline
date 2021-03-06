package uiInterfaces;
import javax.swing.JFrame;
import com.mysql.jdbc.Connection;

import entities.Absenta;
import entities.Elev;
import entities.Nota;
import utilities.UserFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.List;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;


public class StudentFrame extends UserFrame{
	private Connection connection;
	private Elev elev;
	private JComboBox<String> selectorMaterie;
	private List list;
	private JTextField medieTF;
	private JTextField nrAbsenteTF;
	private JTextField medieGeneralaTF;
	private JButton btnOverview;
	private JComboBox comboBox;
	private JTextArea textArea;
	public StudentFrame(String id,String nume,String prenume,Connection connection) {
		elev=new Elev(id,nume,prenume);
		this.connection=connection;
		initialize();
	}
	protected void initialize() {
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setBounds(100, 100, 704, 530);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
			
		
		addLables();
		addTextFieldsforRapoarte();
		addSelectorMaterie();
		addButtonGetAbsente();
		addButtonGetNote();
		addListaRezultat();
		addOverview();
		addComments();
	}

	private void addComments() {
		JButton btnSubmit = new JButton("Submit");
		comboBox = new JComboBox();
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String comentariu = textArea.getText();
				String numeProf = comboBox.getSelectedItem().toString();
				String idProf = elev.getIdProfesor(numeProf, connection);
				elev.addComment(elev.getId(), idProf, comentariu, connection);
				textArea.removeAll();
			}
		});
		btnSubmit.setBounds(468, 415, 89, 23);
		frame.getContentPane().add(btnSubmit);
		
		
		comboBox.setBounds(416, 187, 177, 22);
		frame.getContentPane().add(comboBox);
		
		textArea = new JTextArea();
		textArea.setBounds(374, 216, 256, 165);
		frame.getContentPane().add(textArea);
		ArrayList<String> profi = elev.getProfesori(elev.getId(),connection);
		for(String x:profi)
		comboBox.addItem(x);
		
	}
	public String getClasaByIdElev(int idElev) throws SQLException {
		Statement stmt = connection.createStatement();
		String sql = "SELECT clasa\n" +
				     "FROM Elev\n" +
				     "WHERE id_elev = " + idElev +";";
		ResultSet rs = stmt.executeQuery(sql);
		String numeClasa = "";
		while(rs.next()) {
			numeClasa = numeClasa + rs.getString("clasa");
		}

		rs.close();
		return numeClasa;
	}

	public double medieClasa (String numeClasa) throws SQLException {
		//trebuie luate toate notele elevilor dintr-o clasa
		//STEP 4: Execute a query
		Statement stmt = connection.createStatement();
		String sql = "SELECT AVG(n.nota)\n" +
				     "FROM Note n JOIN Elev e\n" +
				     "ON e.id_elev = n.id_elev\n" +
				     "WHERE e.clasa = '" + numeClasa + "';" ;
		ResultSet rs = stmt.executeQuery(sql);
		double medieClasa = 0;
		while(rs.next()) {
			medieClasa = rs.getInt("AVG(n.nota)");
		}

		rs.close();
		return medieClasa;
	}

	public double getMedieElevByID(int ID_ELEV) throws SQLException {
		//metoda returneaza media + notele
		//cautam in tabela de note doar notele care au la ID_ELEV elevul dorit, si facem media cu ele
		Statement stmt = connection.createStatement();
		String sql = "SELECT nota, id_elev FROM Note;";
		ResultSet rs = stmt.executeQuery(sql);
		//in acelasi timp calculam si media
		int numarNote = 0;
		int sumaNote = 0;

		while(rs.next()){
			//Retrieve by column name
			int nota  = rs.getInt("nota");
			int idElev = rs.getInt("ID_ELEV");

			if(idElev == ID_ELEV) {
				numarNote ++;
				sumaNote += nota;
			}
		}
		rs.close();

		double medie = 0;
		if(numarNote != 0) {
			medie = sumaNote/numarNote;
		}
		return medie;
	}

	private void addOverview() {
		btnOverview = new JButton("Overview");
		btnOverview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//aici vreau sa fie calculate: media clasei in care este elevul
				//+ media elevului => am nevoie de id-ul elevului
				double medieElev = 0, medieClasaElev = 0;
				try {
					medieElev = getMedieElevByID(Integer.parseInt(elev.getId()));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					medieClasaElev = medieClasa(getClasaByIdElev(Integer.parseInt(elev.getId())));
				} catch (SQLException e) {
					e.printStackTrace();
				}

				//System.out.println(medieElev + "*" + medieClasaElev);
				System.out.println(elev.getNume());
				OverviewFrame overviewFrm = new OverviewFrame(medieElev,medieClasaElev,elev.getPrenume());
				overviewFrm.setVisible(true);
			}
		});
		btnOverview.setBounds(99, 444, 89, 23);
		frame.getContentPane().add(btnOverview);
		
	}

	private void addTextFieldsforRapoarte() {
		medieTF = new JTextField();
		medieTF.setEditable(false);
		medieTF.setBounds(135, 115, 37, 20);
		frame.getContentPane().add(medieTF);
		medieTF.setColumns(10);
		
		nrAbsenteTF = new JTextField();
		nrAbsenteTF.setEditable(false);
		nrAbsenteTF.setColumns(10);
		nrAbsenteTF.setBounds(135, 155, 37, 20);
		frame.getContentPane().add(nrAbsenteTF);
		
		medieGeneralaTF = new JTextField();
		medieGeneralaTF.setEditable(false);
		medieGeneralaTF.setBounds(175, 416, 51, 18);
		frame.getContentPane().add(medieGeneralaTF);
		medieGeneralaTF.setColumns(10);
		medieGeneralaTF.setText(Float.toString(elev.getMedieGenerala(elev.getId(), connection)));
		
	}
	private void addLables() {
		JLabel lblName = new JLabel("Elev: " + elev.getNume() + " " + elev.getPrenume());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblName.setBounds(12, 23, 250, 24);
		frame.getContentPane().add(lblName);

		JLabel lblNote = new JLabel("Alege materia:");
		lblNote.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNote.setBounds(12, 43, 194, 24);
		frame.getContentPane().add(lblNote);
		
		JLabel lblMedie = new JLabel("Medie:");
		lblMedie.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblMedie.setBounds(12, 115, 66, 20);
		frame.getContentPane().add(lblMedie);
		
		JLabel lblNumarAbsente = new JLabel("Numar absente:");
		lblNumarAbsente.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNumarAbsente.setBounds(10, 158, 115, 14);
		frame.getContentPane().add(lblNumarAbsente);
		
		JLabel lblMediaGenerala = new JLabel("Media generala :");
		lblMediaGenerala.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblMediaGenerala.setBounds(35, 416, 130, 17);
		frame.getContentPane().add(lblMediaGenerala);
	}
	private void addListaRezultat()
	{
		list = new List();
		list.setFont(new Font("Dialog", Font.BOLD, 16));
		list.setBounds(12, 216, 275, 165);
		frame.getContentPane().add(list);
		
	}
	private void addButtonGetNote()
	{
		JButton buttonGetNote = new JButton("Afiseaza Note");
		buttonGetNote.setBounds(12, 186, 130, 24);
		frame.getContentPane().add(buttonGetNote);
		
		buttonGetNote.addActionListener((ActionListener) new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	Elev.note=elev.getNote(elev.getId(),(String) selectorMaterie.getSelectedItem(),connection);
            	list.removeAll();
            	for(Nota x:Elev.note)
				{
				list.add(x.toString());
				
				}
			}
		});
	}
	private void addSelectorMaterie()
	{
		selectorMaterie = new JComboBox<String>();
		ArrayList<String> materii = elev.getMaterii(elev.getId(),connection);
		for(String x:materii)
		selectorMaterie.addItem(x);
		selectorMaterie.setBounds(12, 78, 130, 22);
		frame.getContentPane().add(selectorMaterie);
		
		selectorMaterie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					medieTF.setText(Float.toString(elev.getMedie(elev.getId(), connection, (String) selectorMaterie.getSelectedItem())));
					nrAbsenteTF.setText(Integer.toString(elev.getNrAbsente(elev.getId(), (String) selectorMaterie.getSelectedItem(), connection)));
				}
			
		});
	}
	private void addButtonGetAbsente()
	{
		JButton buttonGetAbsente = new JButton("Afiseaza Absente");
		buttonGetAbsente.setBounds(152, 186, 130, 24);
		frame.getContentPane().add(buttonGetAbsente);
		
		buttonGetAbsente.addActionListener((ActionListener) new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	ArrayList <Absenta> absente=new ArrayList<>();
            	absente=elev.getAbsente(elev.getId(),(String) selectorMaterie.getSelectedItem(),connection);
            	list.removeAll();
            	for(Absenta x:absente)
				{
				list.add(x.toString());
				
				}
			}
		});
	}
}
