package up.mi.paa.re.models;

//=============================
// enum Conso
//=============================
// enum de la consomation des maisons sur 3 niveaux 


public enum Conso {
	BASSE(10),NORMAL(20),FORTE(40);
	
	private double value;
	
	private Conso(double value) {
		setValue(value);
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

    // Nouvelle méthode : retrouver l'enum à partir d'une valeur
    public static Conso fromValue(double value) {
        for (Conso c : Conso.values()) {
            if (c.getValue() == value) return c;
        }
        throw new IllegalArgumentException("Valeur de consommation inconnue : " + value);
    }
}
