:: commandes d'execution de antlr sur une grammaire contenue dans un fichier de suffixe .g
:: appel par g2java nom-de-votre-grammaire-suffixe-par-g
:: exemple: ./g2java TP1.g

:: Commande si antlr-3.5.2-complete.jar est copie a la racine de votre projet
::    -> pensez alors a indiquer le chemin correct  
java -cp ..\antlr-3.5.2-complete.jar org.antlr.Tool %*

:: SI VSCODE 
:: java -cp ..\lib\antlr-3.5.2-complete.jar org.antlr.Tool %*

