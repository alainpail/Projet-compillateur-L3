Il s'agit d'un projet de L3 en compilation,
le but était de réaliser un compilateur qui compile d'un language appelé "language PROJET" vers un language appelé "Language MAPILE".
Seul les Fichier EDL.java,PtGen.java et projet.g furent ceux sur lesquels on a travaillé,les autres nous étaient fournits déjà fait par l'équipe enseignante.
Nous étiont 2 sur le projet,
Nous avons principalement travaillé sur projet.g et PtGen.java,dont le but était de placer les points de génération dans le fichier projet.g,projet.g correspond à la grammaire du language PROJET.
et d'effectuer les traitements de chaque point de génération dans le fichier PTGen avec la fonction pt().
Ma collègue a travaillé sur EDL.java par la suite tandis que je modifié le PtGen.java pour prendre en compte les cas où le code en Language PROJET était répartit dans différents fichiers,
cela consistait a créer un fichier de description qui était récupéré par le fichier EDJ.java où celui-ci devait fusionner les fichier .obj un seul fichier .map
