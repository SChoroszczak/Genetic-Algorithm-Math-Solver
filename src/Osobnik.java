import java.util.Random;

public class Osobnik {
    int[] genotyp;

    public Osobnik(int bits, Boolean oneBitTrue){

        genotyp = new int[bits];
        Random random = new Random();
        if (oneBitTrue){
            int rand = random.nextInt(genotyp.length);
            for(int i = 0; i < genotyp.length; i++){
                if (i == rand){
                    genotyp[i] = 1;
                }else{
                    genotyp[i] = 0;
                }
            }
        }else {
            for(int i = 0; i < genotyp.length; i++){
                genotyp[i] = random.nextInt(2);
            }
        }


    }
}
