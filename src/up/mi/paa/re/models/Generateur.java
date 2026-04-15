package up.mi.paa.re.models;

//==============================
// class Generateur
//==============================
// Permet de creer des generateurs avec un nom et une capacite max

public class Generateur {
	private String nom;
	private double capacite;
	
	public Generateur(String nom, double capacite) {
		setNom(nom);
		setCapacite(capacite);
	}
	
	public String getNom() {
		return this.nom;
	}
	public double getCapacite() {
		return this.capacite;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setCapacite(double capacite) {
		this.capacite = capacite;
	}
	@Override
	public String toString() {
		return "Generateur "+this.getNom()+" ("+this.getCapacite()+") ";
	}
}
