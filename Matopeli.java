/*
 * Kurssi: Lausekielinen ohjelmointi (Laki/TIEP1)
 * Syksy 2013.
 *
 * Koodin tekij‰: Valtteri Ylisalo
 * Opiskelijanumero: a618968
 * S‰hkˆpostiosoite: a618968@student.uta.fi
 *
 * Harjoitustyˆ 2:
 * Java-ohjelma, joka on matopelin tekstipohjainen variaatio.
 */


public class Matopeli {


  // Vakioidut madon merkit.
  public static final char PAA = 'X';
  public static final char KAULA = 'x';
  public static final char KEHO = 'o';

  // Vakioidut kent‰n merkit.
  public static final char TAUSTA = ' ';
  public static final char SEINA = '.';
  public static final char RUOKA = '+';

  // Vakioidut valinnat.
  public static final char LEFT = 'l';
  public static final char RIGHT = 'r';
  public static final char UP = 'u';
  public static final char DOWN = 'd';
  public static final char SWAP = 's';
  public static final char QUIT = 'q';


  /* P‰‰metodi. Ohjelman suoritus alkaa t‰st‰ lohkosta.
   */
  public static void main(String[] args) {
    // Tulostetaan matomerkeill‰ kehystetty viesti ohjelman k‰ynnistyess‰.
    System.out.println("~~~~~~~~~~~\n~ W O R M ~\n~~~~~~~~~~~");

    // Siemenluku, jolla ruokaa saadaan kent‰lle Automaatti-apuluokan avulla.
    int siemen = 0;
    
    // Kent‰n rivien lukum‰‰r‰.
    int riviLkm = 0;

    // Kent‰n sarakkeiden lukum‰‰r‰.
    int sarakeLkm = 0;

    // Syˆtteiden oikeellisuus. Alustavasti oletetaan niiden olevan j‰rkev‰t.
    boolean syoteOK = true;

    // Komentoriviparametrej‰ t‰ytyy olla tasan kolme.
    if (args.length == 3) {
      // Yritet‰‰n muuntaa annetut komentoriviparametrit kokonaisluvuiksi.
      try {
        siemen = Integer.parseInt(args[0]);
        riviLkm = Integer.parseInt(args[1]);
        sarakeLkm = Integer.parseInt(args[2]);
      }
      // Komentoriviparametrien tyypit olivat virheellisi‰.
      catch (NumberFormatException e) {
        syoteOK = false;
      }
    }
    // Muutoin syˆtteet eiv‰t kelpaa.
    else
      syoteOK = false;

    // Tarkistetaan viel‰, ett‰ rivej‰ ja sarakkeita ei ole liian v‰h‰n.
    if (riviLkm < 3 || sarakeLkm < 7)
      syoteOK = false;

    // Kutsutaan matopeli‰ mallintavaa metodia vain, jos syˆtteet hyv‰ksyttiin.
    if (syoteOK)
      mallinnaPelia(siemen, riviLkm, sarakeLkm);
    // Muutoin mallintamista ei edes yritet‰, ja annetaan virheest‰ ilmoitus.
    else
      System.out.println("Invalid command-line argument!");

    // Peli loppui. T‰m‰ tulostetaan aina ohjelman loppuessa.
    System.out.println("Bye, see you soon.");
  }


  /* Mallintaa matopeli‰. Ensiksi luodaan viidest‰ merkist‰ koostuva mato.
   * Sen j‰lkeen luodaan pelikentt‰, jonka rivien ja sarakkeiden lukum‰‰r‰t
   * annettiin p‰‰metodissa. Automaatti-apuluokan avulla kentt‰‰n sijoitetaan
   * tarvittaessa ruokaa. Peli‰ pelataan niin kauan kuin mato el‰‰. Mato kuolee, 
   * jos 1) valitaan lopeta-toiminto, 2) tekee seiniin nelj‰ reik‰‰, 3) tˆrm‰‰
   * itseens‰. Jokaisen siirron j‰lkeen n‰ytˆlle p‰ivitet‰‰n kuvio, jossa
   * sijaitsee mato, ruoka ja pelikentt‰. Pelin loppumisen j‰lkeen palataan
   * main-metodiin.
   */
  public static void mallinnaPelia(int siemen, int riviLkm, int sarakeLkm) {
    // Esitell‰‰n, luodaan ja alustetaan taulukko-olio, jossa on madon paikat.
    int[][] paikat = { {1, 5},
                       {1, 4},
                       {1, 3},
                       {1, 2},
                       {1, 1} };

    // Alustavasti mato on elossa. T‰m‰ apumuuttuja helpottaa pelaamista.
    boolean matoElossa = true;

    // Kutsutaan metodia, joka luo kentt‰taulukon.
    char[][] kentta = luoTaulukko(riviLkm, sarakeLkm);

    // Kutsutaan metodia, joka piirt‰‰ kuvion ennen pelin aloittamista.
    boolean piirtoOK = piirraKuvio(kentta, paikat);

    // Kutsutaan apuluokkaa, joka k‰ynnist‰‰ ruoka-automaatin.
    Automaatti.kaynnista(siemen);

    // Jotta ruokaa saadaan helpommin tarjoiltua heti kent‰lle, p‰‰tet‰‰n
    // ett‰ mato sˆi ruokaa juuri ennen pelin alkamista.
    boolean syotiinRuokaa = true;

    // Pelataan niin kauan kuin mato el‰‰.
    while (matoElossa) {

      // Jos viime kierroksella syˆtiin ruokaa, kutsutaan apuluokkaa, joka
      // sijoittaa ruokaa kent‰lle.
      if (syotiinRuokaa)
        Automaatti.tarjoile(kentta);

      // Kutsutaan metodia, joka laskee seiniin tehtyjen reikien m‰‰r‰n.
      int reikia = laskeMadonreikia(kentta);
      // Jos reiki‰ oli enemm‰n kuin kolme, madolta l‰htee henki.
      if (reikia > 3)
        matoElossa = false;

      // Jatketaan peli‰ vain, jos madolta ei l‰htenyt henki reiki‰ tehdess‰.
      if (matoElossa) {
        // Tulostetaan tilannerivi: Madon pituus ja madon tekemien reikien m‰‰r‰.
        System.out.println("Worm length: "+paikat.length+", wormholes: "+reikia+".");

        // Jos piirto onnistui viime kierroksella, jatketaan.
        if (piirtoOK) {
          // Kutsutaan metodia, joka tulostaa kent‰n n‰ytˆlle.
          tulosta(kentta);

          // P‰ivitet‰‰n mato oikeelliseksi niin, ett‰ h‰nt‰ ei veny liian pitk‰ksi.
          if (kentta[paikat[paikat.length - 1][0]][paikat[paikat.length - 1][1]] == 'o')
            kentta[paikat[paikat.length - 1][0]][paikat[paikat.length - 1][1]] = ' ';

          // Infotaan toiminnot k‰ytt‰j‰lle ja luetaan valinta In-apuluokan avulla.
          System.out.println("(l)eft, (r)ight, (u)p, (d)own, (s)wap or (q)uit?");
          char valinta = In.readChar();

          // Jos halutaan lopettaa, madolta l‰htee henki ja peli p‰‰ttyy.
          if (valinta == QUIT)
            matoElossa = false;

          // Muutoin jatketaan peli‰.
          else {
            // Tallennetaan h‰nn‰n paikkatiedot muistiin madon kasvamisen varalta.
            int hantaRivi = paikat[paikat.length - 1][0];
            int hantaSarake = paikat[paikat.length - 1][1];

            // Kutsutaan metodia, joka liikuttaa matoa.
            paikat = liikutaMatoa(paikat, valinta, kentta);

            // Kutsutaan metodia, joka tarkistaa tˆrm‰sikˆ mato itseens‰.
            matoElossa = tarkistaTormays(paikat);

            // Kutsutaan metodia, joka tarkistaa syˆtiinkˆ ruokaa.
            syotiinRuokaa = tarkistaRuoka(paikat, kentta);

            // Jos madon p‰‰ siirtyi ruokaan, kutsutaan metodia joka kasvattaa matoa.
            if (syotiinRuokaa)
              paikat = kasvataMatoa(paikat, hantaRivi, hantaSarake);

            // Jos mato on elossa, kutsutaan metodia joka piirt‰‰ pelikent‰n,
            // jossa on p‰ivitetyt kentt‰tiedot ja madon paikkatiedot.
            if (matoElossa)
              piirtoOK = piirraKuvio(kentta, paikat);
          }
        }
      }
    }
  }


  /* Luo annetun kokoisen taulukon, alustaa sen ja palauttaa viitteen.
   */
  public static char[][] luoTaulukko(int riviLkm, int sarakeLkm) {
    // Esitell‰‰n viite, varataan taulukko-oliolle muistia ja liitet‰‰n siihe viite.
    char[][] kentta = new char[riviLkm][sarakeLkm];
    // Kutsutaan metodia, joka alustaa taulukon kent‰ksi.
    alustaTaulukko(kentta, '.', ' ');
    // Palautetaan viite taulukkoon.
    return kentta;
  }


  /* Alustaa kaksiulotteisen kentt‰taulukon siten, ett‰ taulukon reunoille
   * sijoitetaan merkki a ja taulukon sis‰alkioihin sijoitetaan merkki b.
   */
  public static void alustaTaulukko(char[][] kentta, char a, char b) {
    // K‰sitell‰‰n taulukkoa vain, jos sille on varattu muistia.
    if (kentta != null) {
      // Asetetaan jompikumpi merkki kuhunkin taulukon alkioon.
      for (int rivi = 0; rivi < kentta.length; rivi++)
        for (int sarake = 0; sarake < kentta[0].length; sarake++)
         // Reuna-alkio.
         if (rivi == 0 || rivi == kentta.length - 1 || sarake == 0
             || sarake == kentta[0].length - 1)
           kentta[rivi][sarake] = a;
         // Sis‰alkio.
         else
           kentta[rivi][sarake] = b;
    }
  }


  /* Piirt‰‰ kuvion merkit annettuihin merkkitaulukon paikkoihin. Merkki
   * m‰‰r‰ytyy paikkatietojen mukaan. Paluuarvona piirt‰misen onnistuminen.
   */
  public static boolean piirraKuvio(char[][] kentta, int[][] paikat) {
    // Piirret‰‰n vain, jos molemmat taulukot ovat saatavilla.
    if (kentta != null && paikat != null) {
      // Sijoitetaan kuvion merkit kentt‰‰n lukum‰‰r‰n verran.
      for (int paikanInd = 0; paikanInd < paikat.length; paikanInd++) {
        // Kuvion merkin rivi- ja sarakeindeksit.
        int merkinRivInd = paikat[paikanInd][0];
        int merkinSarInd = paikat[paikanInd][1];
        
        // Asetetaan sopiva piirtomerkki merkkikent‰n paikkaan indeksin mukaan.
        if (paikanInd == 0)
          // Madon p‰‰.
          kentta[merkinRivInd][merkinSarInd] = PAA;
        else if (paikanInd == 1)
          // Madon kaula.
          kentta[merkinRivInd][merkinSarInd] = KAULA;
        else if (paikanInd < paikat.length)
          // Madon kehon palanen.
          kentta[merkinRivInd][merkinSarInd] = KEHO;
          // Tyhj‰‰ taustaa.
        else
          kentta[merkinRivInd][merkinSarInd] = TAUSTA;
      }
      
      // Piirto onnistui.
      return true;

    }
    // Muutoin piirto ep‰onnistuu, jos taulukoille ei oltu varattu muistia.
    else
      return false;
  }


  /* Tarkistaa, sˆikˆ mato ruokaa viime siirron j‰lkeen. Jos syˆtiin,
   * paluuarvona true. Jos ei syˆty, paluuarvona false.
   */
  public static boolean tarkistaRuoka(int[][] paikat, char[][] kentta) {
    // K‰yd‰‰n kentt‰taulukko l‰pi ja etsit‰‰n ruuan sijainti.
    for (int rivi = 0; rivi < kentta.length; rivi++) {
      for (int sarake = 0; sarake < kentta[0].length; sarake++) {
        // Jos ruoka oli samassa paikassa miss‰ madon p‰‰, ruokaa syˆtiin.
        if (kentta[rivi][sarake] == RUOKA && rivi == paikat[0][0]
            && sarake == paikat[0][1])
          return true;
      }
    }
    // Ei syˆty ruokaa.
    return false;
  }


  /* Laskee, montako reik‰‰ kent‰lle on tehty. Paluuarvo reikien lukum‰‰r‰,
   * joka on reuna-alkoiden lukum‰‰r‰n ja seinien lukum‰‰r‰n erotus.
   */
  public static int laskeMadonreikia(char[][] kentta) {
    // Apumuuttujat, joita vertaimella selvitet‰‰n reikien lukum‰‰r‰.
    int reunaAlkioidenLkm = 0;
    int seinienLkm = 0;
    
    // Lasketaan reuna-alkoiden lukum‰‰r‰. K‰yd‰‰n kentt‰taulukko l‰pi.
    for (int rivi = 0; rivi < kentta.length; rivi++) {
      for (int sarake = 0; sarake < kentta[0].length; sarake++) {
        // Lˆydettiin reuna-alkio, lis‰t‰‰n se niiden lukum‰‰r‰‰n.
        if (rivi == 0 || rivi == kentta.length - 1
            || sarake == 0 || sarake == kentta[0].length - 1)
          reunaAlkioidenLkm++;
      }
    }
    
    // Lasketaan seinien lukum‰‰r‰. K‰yd‰‰n kentt‰taulukko l‰pi.
    for (int rivi = 0; rivi < kentta.length; rivi++) {
      for (int sarake = 0; sarake < kentta[0].length; sarake++) {
        // Lˆydettiin sein‰‰n tehty reik‰, lis‰t‰‰n se niiden lukum‰‰r‰‰n.
        if (kentta[rivi][sarake] == SEINA)
          seinienLkm++;
      }
    }
    
    // V‰hennet‰‰n seinien lukum‰‰r‰ reuna-alkoiden lukum‰‰r‰st‰.
    int reikienLkm = reunaAlkioidenLkm - seinienLkm;
    
    // Palautetaan reikien lukum‰‰r‰.
    return reikienLkm;
  }


  /* Tulostaa kaksiulotteisen taulukon kentt‰alkiot n‰ytˆlle.
   */
  public static void tulosta(char[][] kentta) {
    // Tulostetaan vain, jos taulukolle on varattu muistia.
    if (kentta != null) {
      // Tulostetaan taulukon alkiot.
      for (int rivi = 0; rivi < kentta.length; rivi++) {
        for (int sarake = 0; sarake < kentta[0].length; sarake++)
          System.out.print(kentta[rivi][sarake]);
        // Rivin lopussa vaihdetaan rivi‰.
        System.out.println();
      }
    }
  }


  /* Antaa madolle liikkumisohje valintasyˆtteen mukaan. Madon paikkatiedot
   * p‰ivitet‰‰n valintaa vastaavaksi paikkataulukon arvoja muuttamalla. Madon
   * peruuttaminen ei ole sallittua. Paluuarvona on p‰ivitetyt paikkatiedot.
   */
  public static int[][] liikutaMatoa(int[][] paikat, char valinta, char[][] kentta) {

    // Haluttiin liikkua johonkin suuntaan, eik‰ yritetty peruuttaa. Tehd‰‰n
    // perusteellinen tarkistus joka suunnalle.
    if ((valinta == LEFT && paikat[0][1] - 1 != paikat[1][1] &&
          paikat[1][1] - kentta[0].length + 1 != paikat[0][1]) ||
        (valinta == RIGHT && paikat[0][1] + 1 != paikat[1][1] &&
          paikat[0][1] - kentta[0].length + 1 != paikat[1][1]) ||
        (valinta == DOWN && paikat[0][0] + 1 != paikat[1][0] &&
          paikat[1][0] + kentta.length - 1 != paikat[0][0]) ||
        (valinta == UP && paikat[0][0] - 1 != paikat[1][0] &&
          paikat[0][0] + kentta.length - 1 != paikat[1][0])) {
      // Siirret‰‰n kaikkia madon osia paitsi p‰‰t‰ edell‰ olevaan paikkaan.
      for (int i = paikat.length - 1; i > 0; i--) {
        paikat[i][0] = paikat[i-1][0];
        paikat[i][1] = paikat[i-1][1];
      }

      // Siirret‰‰n madon p‰‰t‰ vasemmalle.
      if (valinta == LEFT) {
        // Jos p‰‰ menee yli vasemman laidan, se hypp‰‰ oikeaan laitaan.
        if (paikat[0][1] == 0)
          paikat[0][1] = kentta[0].length - 1;
        // Muutoin mato liikuu yhden askeleen vasemmalle.
        else
          paikat[0][1]--;
      }

      // Siirret‰‰n madon p‰‰t‰ oikealle.
      else if (valinta == RIGHT) {
        // Jos p‰‰ menee yli oikean laidan, se hypp‰‰ vasemaan laitaan.
        if (paikat[0][1] == kentta[0].length - 1)
          paikat[0][1] = 0;
        // Muutoin mato liikkuu yhden askeleen oikealle.
        else
          paikat[0][1]++;
      }

      // Siirret‰‰n madon p‰‰t‰ alasp‰in.
      else if (valinta == DOWN) {
        // Jos p‰‰n menee yli alalaidan, se hypp‰‰ yl‰laitaan.
        if (paikat[0][0] == kentta.length - 1)
          paikat[0][0] = 0;
        // Muutoin mato liikkuu yhden askeleen alas.
        else
          paikat[0][0]++;
      }

      // Siirret‰‰n madon p‰‰t‰ ylˆsp‰in.
      else if (valinta == UP) {
        // Jos p‰‰ menee yli yl‰laidan, se hypp‰‰ alalaitaan.
        if (paikat[0][0] == 0)
          paikat[0][0] = kentta.length - 1;
        // Muutoin mato liikkuu yhden askeleen ylˆs.
        else
          paikat[0][0]--;
      }
    }

    // Jos valinta oli suunnan vaihtaminen, muutetaan paikkatiedot k‰‰nteisiksi.
    if (valinta == SWAP) {
      // Muutetaan paikkatietoja sek‰ lopusta ett‰ alusta, l‰hestyen keskikohtaa.
      for (int i = 0; i < paikat.length / 2; i++) {
        // Tallennetaan lopusta l‰htev‰n palan paikkatiedot.
        int lopustaRivi = paikat[i][0];
        int lopustaSarake = paikat[i][1];
        // Sijoitetaan lopusta l‰htev‰lle palalle alusta l‰htev‰n palan paikkatiedot.
        paikat[i][0] = paikat[paikat.length - i - 1][0];
        paikat[i][1] = paikat[paikat.length - i - 1][1];
        // Sijoitetaan alusta l‰htev‰lle palalle lopusta l‰htev‰n palan paikkatiedot.
        paikat[paikat.length - i - 1][0] = lopustaRivi;
        paikat[paikat.length - i - 1][1] = lopustaSarake;
      }
    }

    // Palautetaan p‰ivitetty paikkataulukko main-metodiin.
    return paikat;
  }


  /* Tarkistaa, tˆrm‰sikˆ mato itseens‰ viime toiminnon j‰lkeen. Paluuarvo
   * on true jos ei tˆrm‰nnyt, false mik‰li tˆrm‰si.
   */
  public static boolean tarkistaTormays (int[][] paikat) {
    // K‰yd‰‰n madon muiden palasten paikkatiedot l‰pi ja verrataan niit‰ p‰‰h‰n.
    for (int paikanInd = 1; paikanInd < paikat.length; paikanInd++) {
      // Madon palasen paikkatiedot.
      int riviInd = paikat[paikanInd][0];
      int sarakeInd = paikat[paikanInd][1];
      
      // Jos palasella on samat paikkatiedot kuin p‰‰ll‰, mato kuolee.
      if (riviInd == paikat[0][0] && sarakeInd == paikat[0][1])
        return false;
    }

    // Ei tˆrm‰tty. Mato selvisi hengiss‰.
    return true;
  }


  /* Kasvattaa matoa, mik‰li mato sˆi viime siirrolla ruokaa. Luodaan uusi
   * paikkataulukko, jossa on yksi rivi enemm‰n paikkatietoja. Annetaan uudelle
   * riville h‰nn‰n entisen sijainnin paikkatiedot. Paluuarvona paikkatiedot.
   */
  public static int[][] kasvataMatoa (int[][] paikat, int hantaRivi, int hantaSarake) {
    // Luodaan uusi, yht‰ rivi‰ pidempi, taulukko.
    int[][] pidennettyPaikat = new int[paikat.length + 1][2];
    
    // Kopioidaan vanhan paikkataulukon tiedot uuteen, pidemp‰‰n paikkataulukkoon.
    for (int rivi = 0; rivi < paikat.length; rivi++) {
      for (int sarake = 0; sarake < paikat[0].length; sarake++)
        pidennettyPaikat[rivi][sarake] = paikat[rivi][sarake];
    }
    
    // Annetaan pidemm‰lle paikkataulukolle h‰nn‰n edelliset paikkatiedot.
    pidennettyPaikat[paikat.length] = new int[] { hantaRivi, hantaSarake };
    
    // Palautetaan pidennetty mato main-metodiin.
    return pidennettyPaikat;
  }
}

