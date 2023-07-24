package interfaces;

import java.util.ArrayList;

// mapira asembler kod u masinski kod
public interface Asembler_interpreter_i {
	// =========VARIJABLE=================
	// varijabla fajl; 
	// varibala symbol_table; koja je matrica 2xn ili 2 niza
	// 			ona sluzi za mapiranje sta znaci koja labela, posto labela pretstavlja neki broj (adresu);
	//				znaci bice matrica oblika string x int
	//
	// ================POMOCNE FUNKCIJE (PRIVATNE)====================================
	// LABELE
	// da se ne barata sa matricom, ovde posaljes labelu vrati ti broj(adresu) 
	// abstract int get_symbol(String label); 
	
	// READ
	// ucitava .asm fajl u program tj u varijablu fajl
	abstract void read_asm_file(String path_to_file);
	
	//================PREDPROCESORSKE FUNKCIJE (PROFESORICA SRPKOG)===================================
	// sve greske se javljaju korisniku zajedno sa mjestom u fajlu gdje se nalaze
	
	// LEXER
	// pretvara kod u tokene. = niz tokena = niz stringova
	// cita tekst slovo po slovo i odlucuje da li je to znacajno za nas ili ne ( ne gleda sta komande rade i ima li to smisla- to radi parser)
	//
	// npr kljucne rijeci kao ADD SUB MOV su znacajne, brojevi su znacajni, komentari nisu.. zarezi nisu.. razmaci, tabovi i sl nisu osim ako ne uticu na kod
	// vraca listu stringova koje ce da obradjuje parser npr: 
	//	PUSH 1
	//	POP 
	// ; kraj programa
	//	HLT 
	// ->> return {push, 1, pop, hlt}
	//
	// negdje u tom procesu treba i da provjerava da li postoji neka greska od strane programera koji je pisao asm datoteku npr. umjesto MOV, MLV ili ako pise
	// MOV2 umjesto MOV 2 (sa razmakom) i sl
	//
	// u sustini treba da se ponasa kao profesorica srpkog jezika koja ocjenjuje pismenu (pravopisni dio) :)
	abstract ArrayList<String> lexer();
	
	// PARSER
	// provjerava znacenje teksta da li je dobra kombinacija da ne ide PUSH MOV nego PUSH broj[znak za novi red]MOV broj i sl
	// tj da li ono sto je napisano ima smisla po standardima koje smo zadali (po gramatici koju smo definisali za nas asembler)
	// uz to popunjava niz niz_adresa_naredbi sa adresama za naredbe
	//
	// vraca isto sto i lexer, niz sa naredbama..
	// 
	// u sustini treba da se ponasa kao profesorica srpkog jezika koja ocjenjuje pismenu (gramaticki/smisleni dio) :)
	// abstract ArrayList<String> parser(); 

	//====================MASINSKI KOD/IZVRSAVANJE (OVO SE UVEZUJE SA SHELLOM)==============================
	// ASEMBLE
	// napravi .txt fajl sa masinskim kodom 
	//
	// uzima ono sto parser salje i mapira u masinski jezk (BINARNO)
	public void asemble(String path_to_file, String name_of_new_file);
	
	
}
