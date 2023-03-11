package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import connection.ConnectionFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
	private final Class<T> type;
	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	/**
	 * Prin intermediul unui obiect de tipul StringBuilder metoda creeaza si returneaza string corespunzator
	 * unei interogari de selectie a tabelului asociat clasei obiectului din care a fost apelata metoda si
	 * seteaza clauza where in functie de denumirea coloneai field care a fost primita ca parametru.
	 * @param field
	 * @return string interogarea
	 */
	private String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}
	/**
	 * Prin intermediul unui obiect de tipul StringBuilder metoda creeaza si returneaza string corespunzator
	 * unei interogari de selectie a tabelului asociat clasei obiectului din care a fost apelata metoda
	 * @return string interogarea
	 */
	private String createSelectAllQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(type.getSimpleName());
		return sb.toString();
	}
	/**
	 * In cadrul unri bucle foreach se parcurg toate campurile clasei obiectului cu care s-a apelat metoda si
	 * salveaza intr-un vector denumirea acestora. Vectorul rezultat reprezentant randul antent.
	 * Se creeaza un nou obiect de tipul Vector<Vector<Object>> in care se vor salva toate obiectele corespunzatoare fiecarei
	 * celule din tabel.
	 * Ulterior, tot in cadrul unei bucle foreach se parcurg toate obiectele din lista. Pentru fiecare obiect
	 * se parcurg toate denumirile coloanelor definite in randul antent, prin intermediul unui obiect de tipul
	 * PropertyDescriptor se invoca metoda accesor a campului respectiv si se salveaza intr-un vector
	 * valoarea acestuia. La finalul buclei se adauga la vectorul creat anterior.
	 * Se returneaza un nou obiect de tipul JTable.
	 * @param list
	 * @return JTable tabelul corespunzator listei de obiecte
	 */
	public JTable convertListToTable(List<T> list) {
		Vector<String> columnNames = new Vector<>();
		for (Field field : type.getDeclaredFields()) {
			String fieldName = field.getName();
			columnNames.add(fieldName);
		}
		Vector<Vector<Object>> data = new Vector<>();
		for(T t:list){
			Vector<Object> vector = new Vector<>();
			for(String name:columnNames) {
				try {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, type);
					Method method = propertyDescriptor.getReadMethod();
					vector.add(method.invoke(t));
				} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			data.add(vector);
		}
		return new JTable(new DefaultTableModel(data, columnNames));
	}

	/**
	 *
	 * In cadrul unui try block se creeaza o conexiune la baza de date si se executa interogarea returnata de
	 * metoda createSelectAllQuery() salvandu-se rezultatul obtinut in resultSet.
	 * In cazul in care nu a fost aruncata nicio exceptie se returneaza rezultatul obtinut prin apelarea
	 * metodei createObjects(resultSet) care creeaza o lista de obiecte fiecare reprezentand o tupla din resulSet.
	 * In caz contra returneaza null.
	 * @return lista de obiecte generice T
	 */
	public List<T> findAll(){
		// TODO:
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectAllQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();

			return createObjects(resultSet);

		} catch (SQLException e) {
		LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
	} finally {
		ConnectionFactory.close(resultSet);
		ConnectionFactory.close(statement);
		ConnectionFactory.close(connection);
	}
		return null;
	}

	/**
	 * <p> Se apeleaza metoda createSelectQuery("id") care returneaza interogarea generica pentru
	 * clasa obiectului din care a fost apelata metoda.In cadrul unui block try creeaza o conexiune la baza de date,
	 * ulterior seteaza id-ul primit ca parametru si executa interogarea, rezultatele fiind salvate
	 * intr-un obiect de tipul ResulSet.
	 * In cazul in care in resultSet nu se afla nicio tupla sau a fost aruncata o exceptie metoda retuneaza null.
	 * In caz contrar, se apeleaza metoda createObjects si returneaza primul obiect din lista obtinuta.
	 * </p>
	 * @param id ID-ul tuplei din tabel
	 * @return generic object T
	 */
	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if(!resultSet.isBeforeFirst())
				return null;
			return createObjects(resultSet).get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * <p>Itereaza in cadrul unui for prin toti constructorii clasei obiectului din care care a fost
	 * apelata metoda si salveaza in cadrul obiectului ctor constructorul care nu are niciun parametru.
	 * In try block in interiorul unei bucle while se acceseaza fiecare tupla din resultSet.
	 * In interiorul buclei prin: ctor.setAccessible(true); se asigura ca se poate accesa constructorul,
	 * ulterior se creaaza o instanta de tipul generic "instance" al obiectului.
	 * In cadrul unei bucle foreach se parcurg toate campurile clasei.In interiorul buclei foreach
	 * se preia din tupla obiectul corespunzator campului de la pasul curent "value", iar prin intermediul
	 * unui Obiect de tipul PropertyDescriptor se invoca metoda care acceseaza metoda mutator
	 * pentru instance de tip T "instance" cu parametrul "value".
	 * La finalul buclei while se adauga instance in lista care se returneaza la finalul metodei.
	 *
	 * </p>
	 * @param resultSet
	 * @return lista de obiecte generice
	 */
	private List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<T>();
		Constructor[] ctors = type.getDeclaredConstructors();
		Constructor ctor = null;
		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}
		try {
			while (resultSet.next()) {
				ctor.setAccessible(true);
				T instance = (T)ctor.newInstance();
				for (Field field : type.getDeclaredFields()) {
					String fieldName = field.getName();
					Object value = resultSet.getObject(fieldName);
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
			}
		} catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException | SQLException | IntrospectionException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Prin intermediul unui obiect "sb" de tipul StringBuilder se genereaza interogarea specifica pentru
	 * stergerea unei tuple.Se seteaza valoare cheii primare utilizand un obiect PropertyDescriptor
	 * cu ajutorul caruia se apeleaza metoda accesor a campului.
	 * La finalul try block-ului se apeleaza metoda executeQuery care primeste ca parametru string-ul creat anterior.
	 * @param t
	 * @return obiectul T primit ca parametru in caz de succes, respectiv null in cazul in care a fost aruncata o exceptie
	 * si instructiunile au fost executate cu succes
	 */
	public T delete(T t){ //DELETE FROM table_name WHERE condition;
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE ID = ");

		try {
			PropertyDescriptor propertyDescriptor = new PropertyDescriptor("id", type);
			Method method = propertyDescriptor.getReadMethod();
			sb.append(method.invoke(t));
			sb.append(";");
			executeQuery(sb.toString());
		} catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		return t;

	}

	/**
	 * Creeaza prin intermediul a doua obiecte, "sb" si "sbValues", de tipul StringBuilder interogarea corespunzatoare
	 * operatii de inserare a unei noi tuple in tabel.
	 * Se parcurg toate campurile corespunzatoare clasei obiectului generic T primit ca parametru( fara cheia primara
	 * ID care se autoincrementeaza) si se adauga pt fiecare denumirea campului in sb, respectiv valoarea acestuia( prin
	 * intermediul unui obiect de tipul PropertyDescriptor cu ajutorul caruia se invoca metoda accesor a campului).
	 * La final se concateneaza cele doua siruri de caractere creata si se apeleaza metoda executeQuery pentru noul string rezultat.
	 * @param t
	 * @return
	 */
	public T insert(T t) { //insert into TableName(cname , colName) values (value1,value2);
		StringBuilder sb = new StringBuilder();
		StringBuilder sbValues = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(type.getSimpleName());
		sb.append("(");
		for (Field field : type.getDeclaredFields()) {
			if(field.getName().equals("id"))
				continue;
			String fieldName = field.getName();
			sb.append(fieldName); // pt coloane
			sb.append(","); //
			try {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
				Method method = propertyDescriptor.getReadMethod();
				if(field.getGenericType().getTypeName().equals("java.lang.String")){
					sbValues.append("'");
					sbValues.append(method.invoke(t));
					sbValues.append("'");
				}
				else
					sbValues.append(method.invoke(t));
				sbValues.append(",");
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		sbValues.deleteCharAt(sbValues.length()-1);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") VALUES (");
		sb.append(sbValues);
		sb.append(");");
		executeQuery(sb.toString());
		return t;
	}
	/**
	 * In interiorul unui try block creeaza creeaza o noua conexiune la baza de date si executa interogarea
	 * primita ca si parametru sub forma de String.
	 * @param query
	 */
	public void executeQuery(String query){
		Connection connection = null;
		Statement statement = null;
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
	}
	/**
	 * Creeaza prin intermediul obiectului "sb" de tipul StringBuilder interogarea corespunzatoare
	 * operatii de update a unei tuple din tabel. In cadrul unei bucle foreach se parcurg toate campurile
	 * clasei obiectului generic T primit ca si parametru si se adauga la "sb" denumirea acestuia, respectiv
	 * valoare campului preluata prin intermediul unui obiect de tipul PropertyDescriptor cu ajutorul caruia
	 * se invoca metoda accesor a acestuia.
	 * La final se adauga la "sb" clauza where pentru indentificarea tuplei corespunzatoare cheii primare
	 * a obiectului si se apeleaza apeleaza metoda executeQuery pentru sirul de caractere rezultat.
	 * @param t
	 * @return
	 */
	public T update(T t) {
		// UPDATE Cont_curent SET Suma = Suma + @a WHERE IBAN = IBAN1;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(type.getSimpleName());
		sb.append(" SET ");
		for (Field field : type.getDeclaredFields()) {
			String fieldName = field.getName();
			sb.append(fieldName); // pt coloane
			sb.append(" = "); //
			try {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
				Method method = propertyDescriptor.getReadMethod();
				if(field.getGenericType().getTypeName().equals("java.lang.String")){
					sb.append("'");
					sb.append(method.invoke(t));
					sb.append("'");
				}
				else
					sb.append(method.invoke(t));
				sb.append(" ,");
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("WHERE id = ");
		try {

			PropertyDescriptor propertyDescriptor = new PropertyDescriptor("id", type);
			Method method = propertyDescriptor.getReadMethod();
			sb.append(method.invoke(t));
			sb.append(";");
			executeQuery(sb.toString());
		} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return t;
	}
}
