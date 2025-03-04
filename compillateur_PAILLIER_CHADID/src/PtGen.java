/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libClass_Projet)     *
 *       complement à l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compile, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valEnt = valeur du dernier nombre entier lu (item nbentier)   *
 *     int UtilLex.numIdCourant = code du dernier identificateur lu (item ident) *
 *                                                                               *
 *                                                                               *
 *   methodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arret compilation       *
 *     String UtilLex.chaineIdent(int numId) delivre l'ident de codage numId     *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

/**
 * classe de mise en oeuvre du compilateur
 * =======================================
 * (verifications semantiques + production du code objet)
 * 
 * @author Girard, Masson, Perraudeau
 *
 */

public class PtGen {
    

    // constantes manipulees par le compilateur
    // ----------------------------------------

	private static final int 
	
	// taille max de la table des symboles
	MAXSYMB=300,

	// codes MAPILE :
	RESERVER=1,EMPILER=2,CONTENUG=3,AFFECTERG=4,OU=5,ET=6,NON=7,INF=8,
	INFEG=9,SUP=10,SUPEG=11,EG=12,DIFF=13,ADD=14,SOUS=15,MUL=16,DIV=17,
	BSIFAUX=18,BINCOND=19,LIRENT=20,LIREBOOL=21,ECRENT=22,ECRBOOL=23,
	ARRET=24,EMPILERADG=25,EMPILERADL=26,CONTENUL=27,AFFECTERL=28,
	APPEL=29,RETOUR=30,

	// codes des valeurs vrai/faux
	VRAI=1, FAUX=0,

    // types permis :
	ENT=1,BOOL=2,NEUTRE=3,

	// categories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

    //valeurs possible du vecteur de translation 
    TRANSDON=1,TRANSCODE=2,REFEXT=3;

	private static int inddervars;

    // utilitaires de controle de type
    // -------------------------------
    /**
     * verification du type entier de l'expression en cours de compilation 
     * (arret de la compilation sinon)
     */
	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression entiere attendue");
	}
	/**
	 * verification du type booleen de l'expression en cours de compilation 
	 * (arret de la compilation sinon)
	 */
	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression booleenne attendue");
	}

    // pile pour gerer les chaines de reprise et les branchements en avant
    // -------------------------------------------------------------------

    private static TPileRep pileRep;  


    // production du code objet en memoire
    // -----------------------------------

    private static ProgObjet po;
    
    
    // COMPILATION SEPAREE 
    // -------------------
    //
    /** 
     * modification du vecteur de translation associe au code produit 
     * + incrementation attribut nbTransExt du descripteur
     *  NB: effectue uniquement si c'est une reference externe ou si on compile un module
     * @param valeur : TRANSDON, TRANSCODE ou REFEXT
     */
    private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}    
    // descripteur associe a un programme objet (compilation separee)
    private static Descripteur desc;

     
    // autres variables fournies
    // -------------------------
    
 // MERCI de renseigner ici un nom pour le trinome, constitue EXCLUSIVEMENT DE LETTRES
    public static String trinome="PAILLIER Alan et CHAHID Hafsa"; 	//TODO 
    
    private static int tCour; // type de l'expression compilee
    private static int vCour; // sert uniquement lors de la compilation d'une valeur (entiere ou boolenne)
  
   
    // TABLE DES SYMBOLES
    // ------------------
    //
    private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];
    
    // it = indice de remplissage de tabSymb
    // bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;
	
	/** 
	 * utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numIdCourant) dans tabSymb
	 * 
	 * @param borneInf : recherche de l'indice it vers borneInf (=1 si recherche dans tout tabSymb)
	 * @return : indice de l'ident courant (de code UtilLex.numIdCourant) dans tabSymb (O si absence)
	 */
	private static int presentIdent(int borneInf) {
		int i = it;
		while (i >= borneInf && tabSymb[i].code != UtilLex.numIdCourant)
			i--;
		if (i >= borneInf)
			return i;
		else
			return 0;
	}

	/**
	 * utilitaire de placement des caracteristiques d'un nouvel ident dans tabSymb
	 * 
	 * @param code : UtilLex.numIdCourant de l'ident
	 * @param cat : categorie de l'ident parmi CONSTANTE, VARGLOBALE, PROC, etc.
	 * @param type : ENT, BOOL ou NEUTRE
	 * @param info : valeur pour une constante, ad d'exécution pour une variable, etc.
	 */
	private static void placeIdent(int code, int cat, int type, int info) {
		if (it == MAXSYMB)
			UtilLex.messErr("debordement de la table des symboles");
		it = it + 1;
		tabSymb[it] = new EltTabSymb(code, cat, type, info);
	}

	/**
	 *  utilitaire d'affichage de la table des symboles
	 */
	private static void afftabSymb() { 
		System.out.println("       code           categorie      type    info");
		System.out.println("      |--------------|--------------|-------|----");
		for (int i = 1; i <= it; i++) {
			if (i == bc) {
				System.out.print("bc=");
				Ecriture.ecrireInt(i, 3);
			} else if (i == it) {
				System.out.print("it=");
				Ecriture.ecrireInt(i, 3);
			} else
				Ecriture.ecrireInt(i, 6);
			if (tabSymb[i] == null)
				System.out.println(" reference NULL");
			else
				System.out.println(" " + tabSymb[i]);
		}
		System.out.println();
	}
    

	/**
	 *  initialisations A COMPLETER SI BESOIN
	 *  -------------------------------------
	 */
	public static void initialisations() {
	
		// indices de gestion de la table des symboles
		it = 0;
		bc = 1;
		
		// pile des reprises pour compilation des branchements en avant
		pileRep = new TPileRep(); 
		// programme objet = code Mapile de l'unite en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unite en cours de compilation
		desc = new Descripteur();
		
		// initialisation necessaire aux attributs lexicaux
		UtilLex.initialisation();
	
		// initialisation du type de l'expression courante
		tCour = NEUTRE;

		inddervars =0;

	} // initialisations

	/**
	 *  code des points de generation A COMPLETER
	 *  -----------------------------------------
	 * @param numGen : numero du point de generation a executer
	 */
	public static void pt(int numGen) {
	
		switch (numGen) {
		case 0:
			initialisations();
			break;
		case 1:
			po.produire(EMPILER);
			po.produire(UtilLex.valEnt);
			break;
		case 2:
			tCour = ENT;
			break;		
		case 3:
			tCour = BOOL;
			break;
		case 4:
			po.produire(CONTENUG);
			po.produire(presentIdent(1));
			break;
		case 5://gestion de l'addition
			po.produire(ADD);
			break;
		case 6://gestion de la soustraction
			po.produire(SOUS);
			break;
		case 7://gestion de la Multiplication
			po.produire(MUL);
			break;
		case 8://gestion de la division
			po.produire(DIV);
			break;
		case 9://gestion du =
			po.produire(EG);
			tCour=BOOL;//typecourant devient bool
			break;
		case 10://gestion du <>
			po.produire(DIFF);
			tCour=BOOL;//typecourant devient bool
			break;
		case 11://gestion du >
			po.produire(SUP);
			tCour=BOOL;//typecourant devient bool
			break;
		case 12://gestion du >=
			po.produire(SUPEG);
			tCour=BOOL;//typecourant devient bool
			break;
		case 13://gestion du <
			po.produire(INF);
			tCour=BOOL;//typecourant devient bool
			break;
		case 14://gestion du <= 
			po.produire(INFEG);
			tCour=BOOL;//typecourant devient bool
			break;
		case 15://verification d'un entier
			verifEnt();
			break;
		case 16://verification d'un boolean
			verifBool();
			break;
		case 17://gestion du OU
			po.produire(OU);
			break;
		case 18://gestion du ET
			po.produire(ET);
			break;
		case 19://gestion du NON
			po.produire(NON);
			break;
		case 20://gestion de lecture
			int tmp=presentIdent(1);
			if(tabSymb[tmp].categorie==CONSTANTE){
				UtilLex.messErr("une constante ne pas être modifier");
			}else if(tabSymb[tmp].type == ENT ){
				po.produire(LIRENT);
			}else {
				po.produire(LIREBOOL);
			}
			po.produire(AFFECTERG);
			po.produire(tmp);
			break;
		case 21://gestion de ecriture
			if(tCour == ENT){
				po.produire(ECRENT);
			}else{
				po.produire(ECRBOOL);
			}
			po.produire(vCour);
			break;
		case 22://gestion des constantes
			if(presentIdent(1)!=0){
				UtilLex.messErr(UtilLex.numIdCourant+"est déja présent dans tabsymbole");
			}else{
				
				placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, UtilLex.valEnt);
			}
			break;
		case 23:
			if(presentIdent(1)!=0){
				UtilLex.messErr(UtilLex.numIdCourant+"est déja présent dans tabsymbole");
			}else{
				placeIdent(UtilLex.numIdCourant, VARGLOBALE, tCour, inddervars);
				inddervars ++;
			}
			break;
		case 24:
			po.produire(RESERVER);
			po.produire(inddervars+1);
			break;
		case 25://gestion du si [] alors [] sinon [] fsi
			po.produire(BSIFAUX);
			po.produire(-1);
			pileRep.empiler(po.getIpo());
			break;
		case 26://bsifaux
			po.modifier(pileRep.depiler(), po.getIpo()+3);
			po.produire(BINCOND);
			po.produire(-1);
			pileRep.empiler(po.getIpo());
			break;
		case 27://bincond
			po.modifier(pileRep.depiler(),po.getIpo()+1);
			break;
		case 28://gestion du ttq [] faire [] fait
			pileRep.empiler(po.getIpo());
			po.produire(BSIFAUX);
			po.produire(-1);
			break;
		case 29:
			pileRep.empiler(po.getIpo());
			po.produire(BINCOND);
			po.produire(-1);
		break;
		//gestion du cond
		case 255 : 
			afftabSymb(); // affichage de la table des symboles en fin de compilation
			break;

		
		default:
			System.out.println("Point de generation non prevu dans votre liste");
			break;

		}
	}
}
    
    
    
    
    
    
    
    
    
    
    
    
    
 
