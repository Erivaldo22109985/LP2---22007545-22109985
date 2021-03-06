package pt.ulusofona.lp2.deisiGreatGame;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class GameManager implements Serializable {

    private ArrayList<Programmer> jogadores;
    private int boardSize;
    private int currentPlayer;
    private int nturnos;
    private AbyssesAndTools at;
    private String atMsg = null;
    private HashMap<Integer,Integer> boardCountHistory;
    private HashMap<Abysses,Integer> abyssesCountHistory;

    public GameManager(){
    }

    public void createInitialBoard(String[][] playerInfo,
                                      int worldSize) throws InvalidInitialBoardException
    {
        this.createInitialBoard(playerInfo,worldSize,null);
    }


    public void createInitialBoard(String[][] playerInfo,
                                      int worldSize,
                                      String[][] abyssesAndTools
                                      ) throws InvalidInitialBoardException{
        int boardSize = worldSize;

        //Verificacao de numero jogadores
        if (playerInfo.length < 2 || playerInfo.length > 4) {
            throw new InvalidInitialBoardException("Player Info Length");
        }


        //Verificacao do tabuleiro tamanho
        if (boardSize < playerInfo.length * 2) {
            throw new InvalidInitialBoardException("Boadsize limits");
        }

        this.jogadores = new ArrayList<Programmer>();
        this.boardSize = boardSize;
        this.boardCountHistory = new HashMap<>();
        for(int i = 1; i<= this.boardSize; i++){
            this.boardCountHistory.put(i,0);
        }

        this.abyssesCountHistory = new HashMap<>();
        for(int i = 0; i< Abysses.values().length; i++){
            this.abyssesCountHistory.put(Abysses.values()[i],0);
        }

        this.currentPlayer = 0;
        this.nturnos = 1;

        for (int i = 0; i < playerInfo.length; i++) {
            int id_jogador = -1;
            try{
                id_jogador = Integer.parseInt(playerInfo[i][0]);
            }catch(Exception e)   {
                throw new InvalidInitialBoardException("ID jogador");
            }
            String nome = playerInfo[i][1];
            String lista_linguagens = playerInfo[i][2];
            String cor = playerInfo[i][3];

            //id numero inteiro positivo
            if (id_jogador < 0) {
                throw new InvalidInitialBoardException("ID jogador lower than 0");
            }

            for (int j = 0; j < playerInfo.length; j++) {

                //ids programadores repetidos
                if (j != i && Integer.parseInt(playerInfo[j][0]) == id_jogador) {
                    throw new InvalidInitialBoardException("Programmers IDs same");
                }

                //Nao podem haver cores iguais
                if (j != i && playerInfo[j][3] == cor) {
                    throw new InvalidInitialBoardException("Same colours");
                }
            }

            //os nomes dos porgramadaores
            if (nome == null || nome == "") {
                throw new InvalidInitialBoardException("Programmer names");
            }

            //cor dos jogadores
            if (cor != "Purple" && cor != "Green" && cor != "Brown" && cor != "Blue") {
                throw new InvalidInitialBoardException("Player Colours");
            }

            //Adicionar jogador
            this.jogadores.add(new Programmer(id_jogador, nome, cor, lista_linguagens));
        }


        this.at = new AbyssesAndTools();
        this.at.init(abyssesAndTools, boardSize);

        Collections.sort(this.jogadores);
    }

    public List<Map.Entry<Integer,Integer>> getBoardCountHistory(){
        List<Map.Entry<Integer,Integer>> l =
                new LinkedList<Map.Entry<Integer,Integer>> (this.boardCountHistory.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Integer,Integer>>(){
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
                }

        );
        return l;
    }

    public List<Map.Entry<Abysses,Integer>> getAbyssesCountHistory(){
        List<Map.Entry<Abysses,Integer>> l =
                new LinkedList<Map.Entry<Abysses,Integer>> (this.abyssesCountHistory.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<Abysses,Integer>>(){
                    public int compare(Map.Entry<Abysses, Integer> o1,
                                       Map.Entry<Abysses, Integer> o2)
                    {
                        return (o2.getValue()).compareTo(o1.getValue());
                    }
                }

        );
        return l;
    }

    public ArrayList<Programmer> sortProgrammerBynLanguage(ArrayList<Programmer> l){

        Collections.sort(l, new Comparator<Programmer>(){
                    public int compare(Programmer o1,
                                       Programmer o2)
                    {
                         if(o2.getLinguagens().length == o1.getLinguagens().length){
                             return 0;
                         }
                        if(o2.getLinguagens().length < o1.getLinguagens().length){
                            return 1;
                        }
                        return -1;
                    }
                }

        );
        return l;
    }

    public String getImagePng(int position){
        if(position >= this.boardSize || position < 0){
            return null;
        }

        if(position == this.boardSize - 1){
            return "glory.png";
        }

        return this.at.getImagePng(position);
    }

    public String getTitle(int position){
        return this.at.getTitle(position);
    }

    public List<Programmer> getProgrammers(){
        return this.jogadores;
    }

    public String getProgrammersInfo(){
        String ret = "";
        for(int i= 0; i < this.getProgrammers().size(); i++){
            Programmer x = this.getProgrammers().get(i);

            ret += x.getName() + " : ";

            ret += x.ferramentasToString();

            if( i != this.getProgrammers().size() - 1){
                ret += " | ";
            }
        }

        return ret;
    }
    public List<Programmer> getProgrammers(boolean includeDefeated){
        ArrayList<Programmer> ret = new ArrayList<Programmer>();

        if(includeDefeated == true) {
            return this.getProgrammers() == null ? ret : this.getProgrammers();
        }
        for(Programmer x : this.jogadores){
            if(x.getEstado() != "Derrotado") {
                ret.add(x);
            }
        }

        return ret;
    }
    public List<Programmer> getProgrammers(int position){
        ArrayList<Programmer> k = new ArrayList<Programmer>();

        for(Programmer x : this.jogadores){
            if(x.getPos() == position) {
                k.add(x);
            }
        }

        return k;
    }
    public List<ProgrammerSimple> getProgrammersSimple(int position){
        ArrayList<ProgrammerSimple> k = new ArrayList<ProgrammerSimple>();

        for(Programmer x : this.jogadores){
            if(x.getPos() == position) {
                k.add(new ProgrammerSimple(x.getName(),x.getPos()));
            }
        }

        Collections.sort(k);

        return k;
    }

    public int getCurrentPlayerID(){
        return this.jogadores.get(this.currentPlayer).getId();
    }

    public int getCurrentPlayer(){
        return this.currentPlayer;
    }

    public boolean moveCurrentPlayer(int nrPositions){
        int prox_casa = 0;

        if(nrPositions < 1 || nrPositions > 6){
            return false;
        }

        Programmer x = this.jogadores.get(this.currentPlayer);

        if(x.getBlocked() == true|| x.getEstado() == "Derrotado"){
            this.atMsg = "Bloqueado!!";
            return false;
        }


        if(x.getPos()+nrPositions < this.boardSize) {
            prox_casa = x.getPos() + nrPositions;
        } else {
            prox_casa = this.boardSize - (nrPositions - (this.boardSize - x.getPos()) );
        }

        x.setNewPos(x.getPos());
        x.setPos(prox_casa,false);
        if(this.boardCountHistory != null) {
            if (this.boardCountHistory.containsKey(prox_casa) == true) {
                this.boardCountHistory.put(prox_casa, this.boardCountHistory.get(prox_casa) + 1);
            } else {
                this.boardCountHistory.put(prox_casa, 1);
            }
        }
        return true;
    }



    public String reactToAbyssOrTool(){
        Programmer x = this.jogadores.get(this.currentPlayer);
        int casa_atual = x.getNewPos();
        int prox_casa = x.getPos();

        if(x.getBlocked() == true|| x.getEstado() == "Derrotado"){
            return null;
        }

        x.setPos(casa_atual,false);
        x.setNewPos(prox_casa);

        this.atMsg = null;

        if(this.at.isAbysse(prox_casa) == true){
            if(this.abyssesCountHistory != null  && this.abyssesCountHistory.containsKey(this.at.getAbysse(prox_casa)) == true) {
                this.abyssesCountHistory.put(this.at.getAbysse(prox_casa), 1 + this.abyssesCountHistory.get(this.at.getAbysse(prox_casa)));
            }
            prox_casa = this.playAbysse(prox_casa, x);
        }else if(this.at.isTool(prox_casa) == true){
            x.setActiveTool(Tools.values()[at.getATPosition(prox_casa)[1]]);
            this.atMsg = "Apanhada ferramenta: " + Tools.values()[at.getATPosition(prox_casa)[1]];
        }
        x.setPos(prox_casa,true);

        this.currentPlayer++;this.nturnos++;
        if(this.currentPlayer>=this.jogadores.size()){
            for(int i=0; i<this.jogadores.size();i++){
                if(this.jogadores.get(i).getEstado() != "Derrotado" || this.jogadores.get(i).getBlocked() == false){
                    this.currentPlayer = i;
                }
            }
            this.currentPlayer=0;
        }

        return this.atMsg;
    }

    public String replaceEmptyLines(String in){
        return in.trim();
    }

    private int countPlayersSamePlace(int pos){
        int n = 0;
        for(Programmer x: this.getProgrammers()){
            if(x.getPos() == pos) {
                n++;
            }
        }

        return n;
    }

    private void updatePlayersState(boolean estado, int pos){
        int n = 0;
        for(Programmer x: this.getProgrammers()){
            if(x.getPos() == pos) {
                x.setBlocked(estado);
            }
        }

    }

    private void updatePlayersPlace(int pos, int n_casas){

        for(Programmer x: this.getProgrammers()){
            if(x.getPos() == pos)
            {
                x.setPos(pos + n_casas,true);
            }
        }

    }

    public int playAbysse(int new_pos, Programmer x){
        int at_pos = x.getPos();
        int ant_pos = x.getPosAnt();
        int numDado = new_pos - at_pos;
        int count_players_same_place = this.countPlayersSamePlace(at_pos);
        int count_players_new_place = this.countPlayersSamePlace(new_pos);

        //no caso de nao estar em jogo
        if(at_pos <= 0 || x.getBlocked() == true) {
            return at_pos;
        }

        //posicao nova
        switch(Abysses.values()[at.getATPosition(new_pos)[1]]){
            case syntax:

                this.atMsg = "Abismo de Sintaxe. Anda uma posicao para traz!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.ajuda_professor,
                                Tools.IDE
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta!";
                        return new_pos;
                }

                return new_pos - 1;
            case logic:
                this.atMsg = "Abismo de logica. Anda " + numDado/2 + " para traz!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.ajuda_professor,
                                Tools.unit_tests
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta!";
                        return new_pos;
                }

                if(numDado == 1){
                    numDado = 2;
                }
                return (int) Math.floor(new_pos - ( numDado/ 2));
            case exception:
                this.atMsg = "Abismo de excecao. Anda para traz 2 posicoes!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.catch0,
                                Tools.ajuda_professor
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                return new_pos - 2;
            case file_not_found_exception:
                this.atMsg = "Abismo de File Not Found. Anda para traz 3 posicoes!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.catch0,
                                Tools.ajuda_professor
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }


                return new_pos - 3;
            case crash:
                this.atMsg = "Abismo de Crash. Volta para casa inicial!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.functional
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                return 1;
            case duplicated_code:
                this.atMsg = "Abismo de Duplicated code. Volta para casa de onde jogou!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.inheritance
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                return at_pos;
            case secondary_effects:
                this.atMsg = "Abismo de Efeitos Secundarios. Volta para a casa de duas jogadas atras!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.functional
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                return ant_pos;
            case bsod:

                this.atMsg = "Abismo de BSOD. Perdeu o Jogo!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.catch0
                        }) == true){
                    this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                x.setEstado("Derrotado");

                return new_pos;
            case infinite_loop:
                this.atMsg = "Abismo de Loop infinito. Bloqueado ate outro programador jogar!";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.inheritance,
                                Tools.unit_tests
                        }) == true){
                        this.atMsg = "\nSalvo por ferramenta";
                        return new_pos;
                }

                if(count_players_new_place >= 1){
                    this.updatePlayersState(false, new_pos);
                }

                x.setBlocked(true);

                return new_pos;
            case core_dumped:
                this.atMsg = "Abismo Core Dumped.";
                if(x.hasAtLeastOneTool_andRemoveit(
                        new Tools[]{
                                Tools.functional
                        }) == true){
                        this.atMsg += "\nSalvo por ferramenta";
                        return new_pos;
                }

                if(count_players_new_place >= 1){
                    this.atMsg += "\nTodos os programadores nesta casa perdem 3 casas!";
                    this.updatePlayersPlace(new_pos,-3);
                    return new_pos - 3;
                }else{
                    this.atMsg += "\nNada acontece";
                }

                return new_pos;


        }

        return new_pos;
    }

    public boolean gameIsOver(){
        if(this.getProgrammers(this.boardSize).size() >= 1){

            for(Programmer k : this.getProgrammers()){

                if (k.getPos() != this.boardSize){
                    k.setEstado("Derrotado");
                }
                else{
                    k.setEstado("Ganhou");
                }
            }
            return true;
        }


        for(Programmer k : this.getProgrammers()){
            if(k.getBlocked() == false && k.getEstado() != "Derrotado"){
                return false;
            }
        }

        return true;
    }

    public List<String> getGameResults(){
        ArrayList<String> ret = new ArrayList<String>();

        if(this.gameIsOver() == false){
            return ret;
        }

        ret.add("O GRANDE JOGO DO DEISI");
        ret.add("");
        ret.add("NR. DE TURNOS");
        ret.add(Integer.toString(this.nturnos));
        ret.add("");
        ret.add("VENCEDOR");
        ret.add(this.getProgrammers(this.boardSize).get(0).getName());
        ret.add("");
        ret.add("RESTANTES");

        //int pos = 2;
        for(int i = this.boardSize-1; i>= 0; i--){
            for(ProgrammerSimple x: this.getProgrammersSimple(i)){
                ret.add(x.getName() + " " + Integer.toString(x.getPos()));
                //pos++;
            }
        }


        return ret;
    }

    public JPanel getAuthorsPanel(){
        return null;
    }

    public boolean saveGame(File file){
        try{
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(this);

            o.close();
            f.close();

        }catch (Exception e){
            return false;
        }

        return true;
    }

    public int getBoardSize(){
        return this.boardSize;
    }
    public int getNturnos(){
        return this.nturnos;
    }

    public AbyssesAndTools getAt(){
        return this.at;
    }

    public String getAtMsg(){
        return this.atMsg;
    }
    public void loadGame(GameManager g){
        this.jogadores = (ArrayList<Programmer>) g.getProgrammers();
        this.boardSize = g.getBoardSize();
        this.currentPlayer = g.getCurrentPlayer();
        this.nturnos = g.getNturnos();
        this.at = g.getAt();
        this.atMsg = g.getAtMsg();
    }
    public boolean loadGame(File file){
        try{
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);

            this.loadGame((GameManager) o.readObject());

            o.close();
            f.close();

        }catch (Exception e){
            return false;
        }

        return true;
    }

}
