# Gestion et Optimisation d'un Réseau Électrique

Ce projet a été réalisé en binôme dans le cadre de l'unité d'enseignement Programmation Avancée et Applications (PAA). Il consiste en une application Java permettant de modéliser, de gérer et d'optimiser les connexions entre des générateurs et des habitations au sein d'un réseau électrique. L'objectif principal est de minimiser le coût total du réseau tout en respectant les contraintes de capacité des générateurs.

## Exécution du Programme

La classe principale est : `up.mi.paa.re.Main`

### Avec un fichier d'entrée
Pour charger un réseau existant (par exemple reseau.txt) :
```bash
java -cp bin up.mi.paa.re.Main reseau.txt
```

### Sans fichier d'entrée
Pour démarrer un réseau vide :
```bash
java -cp bin up.mi.paa.re.Main
```

## Organisation du Code

L'architecture suit une séparation stricte des responsabilités :

* **up.mi.paa.re** : Point d'entrée de l'application.
* **up.mi.paa.re.models** : Objets métiers (ReseauElectrique, Maison, Generateur, Conso).
* **up.mi.paa.re.io** : Gestion des entrées/sorties (chargement et sauvegarde de fichiers texte).
* **up.mi.paa.re.solver** : Algorithmes d'optimisation (Heuristique Constructive, Iterated Local Search).
* **up.mi.paa.re.ui** : Interface utilisateur en mode console.

## Algorithmes de Résolution

Le projet implémente deux stratégies pour optimiser le coût du réseau :

1. **Heuristique Constructive (Best Fit Decreasing)** : Trie les maisons par consommation et les affecte au générateur ayant l'espace disponible le plus proche de la demande pour limiter la fragmentation.
2. **Iterated Local Search (ILS)** : Part d'une solution initiale et tente de l'améliorer par itérations successives. L'algorithme applique des perturbations (déplacement de maisons de générateurs surchargés vers des générateurs sous-utilisés) pour échapper aux optimums locaux.

## Fonctionnalités Implémentées

* Chargement et lecture de fichiers structurés (générateurs, maisons, connexions).
* Détection d'erreurs de syntaxe et contrôle de la cohérence des données.
* Calcul automatique du coût total du réseau selon les formules définies.
* Interface interactive permettant la modification manuelle des connexions.
* Export du réseau optimisé vers un fichier texte au format valide.
* Vérification systématique de l'intégrité du réseau (chaque maison doit être connectée à un unique générateur).
```
