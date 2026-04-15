package up.mi.paa.re.models;

//============================= 
// class Maison
//=============================
// Permet de creer des maisons avec un nom et leurs consommations


public class Maison {
	private String nom;

	// Conso --> enum, Donne la consommation de la maison base sur 3 niveaux 
	private Conso conso;
	
	public Maison(String nom, Conso conso) {
		setNom(nom);
		setConso(conso);
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public Conso getConsoRaw() {
		return this.conso;
	}
	
	public double getConso() {
		return this.conso.getValue();
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setConso(Conso c) {
		this.conso = c;
	}
	
	@Override
	public String toString() {
		return "Maison "+this.getNom()+" ("+this.getConso()+") ";
	}
}
