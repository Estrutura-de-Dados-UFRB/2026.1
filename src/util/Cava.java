package util;

// Cria o conjunto completo de peças do dominó e expõe o array de peças.
public class Cava {
    private Pedras2[] domino = new Pedras2[28];

    public void criarDomino(){
        int k = 0;

        // Gera as 28 combinacoes unicas do domino, de 0-0 ate 6-6.
        for(int i = 0; i < 7; i++){
            for(int j = i; j < 7; j++){
                Pedras2 p = new Pedras2();
                p.setPedra(i, j);
                domino[k] = p;
                k++;
            }
        }
    }

    public void mostraDomino(){
        for(int l = 0; l < domino.length; l++){
            System.out.print("[" + domino[l].getLadoA() + "/" + domino[l].getLadoB() + "] ");
        }
    }

    public Pedras2[] getDomino(){
        return this.domino;
    }
}
