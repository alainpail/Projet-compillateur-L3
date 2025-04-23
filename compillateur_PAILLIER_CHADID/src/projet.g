// Grammaire du langage PROJET
// CMPL L3info 
// Nathalie Girard, Veronique Masson, Laurent Perraudeau
// il convient d'y inserer les appels a {PtGen.pt(k);}
// relancer Antlr apres chaque modification et raffraichir le projet Eclipse le cas echeant

// attention l'analyse est poursuivie apres erreur si l'on supprime la clause rulecatch

grammar projet;

options {
  language=Java; k=1;
 }

@header {           
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
} 


// partie syntaxique :  description de la grammaire //
// les non-terminaux doivent commencer par une minuscule


@members {

 
// variables globales et methodes utiles a placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse a la premiere erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :   unitprog {PtGen.pt(255);} EOF
      |    unitmodule  EOF
  ;
  
unitprog
  : 'programme' {PtGen.pt(53);} ident ':'  
     declarations  
     corps { System.out.println("succes, arret de la compilation "); }
  ;
  
unitmodule
  : 'module' {PtGen.pt(54);} ident ':' 
     declarations   
  ;
  
declarations
  : partiedef? partieref? consts? vars? decprocs? 
  ;
  
partiedef
  : 'def' ident {PtGen.pt(51);} (',' ident {PtGen.pt(51);})* ptvg
  ;
  
partieref: 'ref' specif {PtGen.pt(50);}(',' specif {PtGen.pt(50);} )* ptvg
  ;
  
specif  : ident {PtGen.pt(47);}  ( 'fixe' '(' type {PtGen.pt(48);} ( ',' type {PtGen.pt(48);} )* ')' )? 
                 ( 'mod'  '(' type {PtGen.pt(49);} ( ',' type {PtGen.pt(49);} )* ')' )? 
  ;
  
consts  : 'const' ( ident  '=' valeur {PtGen.pt(22);} ptvg  )+ 
  ;
  
vars  : 'var' ( type ident {PtGen.pt(23);} ( ','  ident {PtGen.pt(23);}  )* ptvg {PtGen.pt(24);} )+
  ;
  
type  : 'ent'  {PtGen.pt(2);}
  |     'bool' {PtGen.pt(3);}
  ;
  
decprocs: (decproc ptvg)+
  ;
  
decproc :  'proc'  ident {PtGen.pt(33);} parfixe? parmod? {PtGen.pt(36);} consts? vars? corps 
  ;
  
ptvg  : ';'
  | 
  ;
  
corps : 'debut'{PtGen.pt(52);} instructions 'fin'{PtGen.pt(100);}
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident {PtGen.pt(34);}  ( ',' ident {PtGen.pt(34);} )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident {PtGen.pt(35);} ( ',' ident {PtGen.pt(35);} )*
  ;
  
instructions
  : instruction ( ';' instruction)*
  ;
  
instruction
  : inssi
  | inscond
  | boucle
  | lecture
  | ecriture
  | affouappel
  |
  ;
  
inssi : 'si' expression {PtGen.pt(16);} {PtGen.pt(25);} 'alors' instructions ('sinon' {PtGen.pt(26);} instructions)? 'fsi'{PtGen.pt(27);} 
  ;
  
inscond : 'cond'   expression {PtGen.pt(16);} {PtGen.pt(30);} ':' instructions 
          (',' {PtGen.pt(31);} expression {PtGen.pt(16);}{PtGen.pt(30);} ':' instructions )* 
          ('aut' {PtGen.pt(32);}  instructions |  ) 
          'fcond' {PtGen.pt(32);}
  ;
  
boucle  : 'ttq' {PtGen.pt(28);}  expression {PtGen.pt(16);} {PtGen.pt(25);}'faire' instructions 'fait' {PtGen.pt(29);}
  ;
  
lecture: 'lire' '(' ident {PtGen.pt(20);}  ( ',' ident  {PtGen.pt(20);})* ')' 
  ;
  
ecriture: 'ecrire' '(' expression {PtGen.pt(21);} ( ',' expression {PtGen.pt(21);} )* ')'
   ;
  
affouappel
  : ident {PtGen.pt(41);} (    ':=' expression {PtGen.pt(37);}
            | {PtGen.pt(41);}  (effixes (effmods)?)?  {PtGen.pt(38);}
           )
  ;
  
effixes : '(' (expression {PtGen.pt(39);} (',' expression {PtGen.pt(39);} )*)? ')'
  ;
  
effmods :'(' (ident {PtGen.pt(40);} (',' ident {PtGen.pt(40);} )*)? ')'
  ; 
  
expression: (exp1) ({PtGen.pt(16);}'ou'  exp1{PtGen.pt(16);} {PtGen.pt(17);}  )*
  ;
  
exp1  : exp2  ({PtGen.pt(16);}'et'  exp2 {PtGen.pt(16);} {PtGen.pt(18);})* 
  ;
  
exp2  : 'non' exp2 {PtGen.pt(16);} {PtGen.pt(19);}
  | exp3
  ;
  
exp3  : exp4 
  ({PtGen.pt(15);} '='   exp4 {PtGen.pt(15);} {PtGen.pt(9);}
  |{PtGen.pt(15);} '<>'  exp4 {PtGen.pt(15);} {PtGen.pt(10);}
  |{PtGen.pt(15);} '>'   exp4 {PtGen.pt(15);} {PtGen.pt(11);}
  |{PtGen.pt(15);} '>='  exp4 {PtGen.pt(15);} {PtGen.pt(12);}
  |{PtGen.pt(15);} '<'   exp4 {PtGen.pt(15);} {PtGen.pt(13);}
  |{PtGen.pt(15);} '<='  exp4 {PtGen.pt(15);} {PtGen.pt(14);}
  ) ?
  ;
  
exp4  : exp5 
        ({PtGen.pt(15);} '+'  exp5 {PtGen.pt(15);} {PtGen.pt(5);}
        |{PtGen.pt(15);} '-'  exp5 {PtGen.pt(15);} {PtGen.pt(6);}
        )* 
  ;
  
exp5  : primaire 
        (  {PtGen.pt(15);}  '*'   primaire {PtGen.pt(15);} {PtGen.pt(7);}
          |{PtGen.pt(15);} 'div'  primaire {PtGen.pt(15);} {PtGen.pt(8);}
        )*
  ;
  
primaire: valeur {PtGen.pt(1);}
  | ident {PtGen.pt(4);}
  | '(' expression ')'
  ;
  
valeur  : nbentier {PtGen.pt(2);}
  | '+' nbentier {PtGen.pt(2);} {PtGen.pt(43);}
  | '-' nbentier {PtGen.pt(2);} {PtGen.pt(44);}
  | 'vrai' {PtGen.pt(3);} {PtGen.pt(45);}
  | 'faux' {PtGen.pt(3);} {PtGen.pt(46);}
  ;

// partie lexicale  : cette partie ne doit pas etre modifiee  //
// les unites lexicales de ANTLR doivent commencer par une majuscule
// Attention : ANTLR n'autorise pas certains traitements sur les unites lexicales, 
// il est alors ncessaire de passer par un non-terminal intermediaire 
// exemple : pour l'unit lexicale INT, le non-terminal nbentier a du etre introduit
 
      
nbentier  :   INT { UtilLex.valEnt = Integer.parseInt($INT.text);}; // mise a jour de valEnt

ident : ID { UtilLex.traiterId($ID.text); } ; // mise a jour de numIdCourant
     // tous les identificateurs seront places dans la table des identificateurs, y compris le nom du programme ou module
     // (NB: la table des symboles n'est pas geree au niveau lexical mais au niveau du compilateur)
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' |'\r')+ {skip();} ; // definition des "blocs d'espaces"
RC  :   ('\n') {UtilLex.incrementeLigne(); skip() ;} ; // definition d'un unique "passage a la ligne" et comptage des numeros de lignes

COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caracteres entouree d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractere diese sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;	   



	   
