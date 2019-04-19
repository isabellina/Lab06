package it.polito.tdp.meteo;

import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private double  best = 1000000000;
	private List<Citta> bestPercorso = new LinkedList<Citta>();
	private List<Citta> city =  new LinkedList<Citta>();
	
	private MeteoDAO dao;

	public Model() {
		this.dao = new MeteoDAO();
		
	}

	public String getUmiditaMedia(int mese) {
		String [] localita = {"Genova", "Torino", "Milano"};

		String ret = "";
		for(String l : localita) {
			ret += l + ": " + String.valueOf(this.dao.getAvgRilevamentiLocalitaMese(mese, l)) + "\n";
		}
		return ret;
	}

	public String trovaSequenza(int mese) {
		String sol="";
		this.city = this.dao.getAllRilevamentiMese(mese);
		this.recursive(1, (new LinkedList<Citta>()));
		System.out.println("Finito:");
		System.out.println(this.bestPercorso.size());
		for(Citta c : this.bestPercorso) {
			System.out.println(c.getNome());
		}
		for(Citta c: this.bestPercorso) {
			sol+=c.getNome()+"\n";
		}
		return sol;
		
	}
	
	private void recursive(int livello, List<Citta> listaCitta){
		if(livello==15) {
			if(controllaParziale(listaCitta)&&(this.bestPercorso == null || punteggioSoluzione(listaCitta)<=best)) {
				for(Citta c : listaCitta) {
					System.out.println("Città: " + c.getNome());
				}
				this.bestPercorso = listaCitta;
				this.best = punteggioSoluzione(listaCitta);
				System.out.println("Best price: " + Double.toString(this.best));
			}
		}
		else {
			for(Citta c: city) {
				listaCitta.add(c);
				this.recursive(livello+1, listaCitta);
				listaCitta.remove(c);
			}
		}
	}

	private Double punteggioSoluzione(List<Citta> soluzioneCandidata) {
		
		double score = 0.0;
		for(int i=0; i<soluzioneCandidata.size(); i++) {
			if(soluzioneCandidata.get(i).getRilevamenti() != null) {
				for(Rilevamento r : soluzioneCandidata.get(i).getRilevamenti()) {
					if(r.getDayNumber() == i+1) {
						score += r.getUmidita();
					}
				}
			}
			if(i != 0) {
				if(!soluzioneCandidata.get(i).getNome().equals(soluzioneCandidata.get(i-1).getNome())) {
					score += 100;
				}
			}
		}
		
		return score;
	}

	private boolean controllaParziale(List<Citta> parziale) {
	int c = 0;
	if(parziale.size() == 0)
		return false;
	for(int i=0; i<(NUMERO_GIORNI_TOTALI - 1); i++) {
		if(i != 0) {
			if(!parziale.get(i).equals(parziale.get(i-1))) {
				  if(c < 3) {
					  return false;
				  }
				  else {
					  c = 0;
				  }
			}
			else {
				c++;
			}
		}
	}
	int ct = 0;
	int cg = 0;
	int cm = 0;
	for(Citta ci : parziale) {
		if(ci.getNome().toLowerCase().equals("torino")) {
			ct++;
		}
		else if(ci.getNome().toLowerCase().equals("genova")) {
			cg++;
		}
		else {
			cm++;
		}
	}
	if(ct > 5 && cg > 5 && cm > 5) {
		return false;
	}
	return true;
	}

}
