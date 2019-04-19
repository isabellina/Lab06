package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		String m="";
		final String sql = "SELECT Data, Umidita, Localita "
				+ "FROM situazione WHERE substring(situazione.data,1,7) = ? "
				+ "AND (CAST(substring(situazione.data, 8, 10)*-1) AS INT) <= 15 "
				+ "AND situazione.Localita = ? "
				+ "ORDER BY situazione.data";
		
		if(mese < 10) {
			 m = "0" + String.valueOf(mese);
		}
		else {
			 m = String.valueOf(mese);
		}
		
		List<Rilevamento> listaRilevamenti = new LinkedList<Rilevamento>();
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, "2013-".concat(m));
			st.setString(2, localita);
					ResultSet rs = st.executeQuery();
					while (rs.next()) {

						Rilevamento r = new Rilevamento(localita, rs.getDate("Data"), rs.getInt("Umidita"));
						listaRilevamenti.add(r);
					}
					conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return listaRilevamenti;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {
		String m="";
		final String sql = "SELECT AVG(situazione.Umidita) as average "
				+ "FROM situazione WHERE substring(situazione.data,1,7) = ? "
				+ "AND situazione.Localita = ?";
		
		if(mese < 10) {
			 m = "0" + String.valueOf(mese);
		}
		else {
			 m = String.valueOf(mese);
		}
		
		double avg = 0.0;
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, "2013-".concat(m));
			st.setString(2, localita);
					ResultSet rs = st.executeQuery();
					if(rs.next()) {
						avg = rs.getDouble("average");
					}
					conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return avg;
	}

	public List<Citta> getAllRilevamentiMese(int mese) {
		String m="";
		final String sql = "SELECT Data, Umidita, Localita "
				+ "FROM situazione WHERE substring(situazione.data,1,7) = ? "
				+ "AND CAST(substring(situazione.data, 8, 10)*-1 AS INT) <= 15 "
				+ "ORDER BY situazione.data";
		
		if(mese < 10) {
			 m = "0" + String.valueOf(mese);
		}
		else {
			 m = String.valueOf(mese);
		}
		
		List<Citta> listaCitta = new LinkedList<Citta>();
		listaCitta.add(new Citta("Torino"));
		listaCitta.add(new Citta("Genova"));
		listaCitta.add(new Citta("Milano"));
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, "2013-".concat(m));
					ResultSet rs = st.executeQuery();
					while (rs.next()) {

						Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
						for(Citta c : listaCitta) {
							if(c.getNome().toLowerCase().equals(rs.getString("Localita").toLowerCase())) {
								c.addRilevamento(r);
							}
						}
					}
					conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return listaCitta;
	}

}
