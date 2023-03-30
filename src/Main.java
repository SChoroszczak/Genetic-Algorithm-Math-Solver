import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {

        //Settings
        int bits_Amount = 6;
        int subjects_Amount = 100;    //must be even
        int generations_Amount = 20;
        int mutationChance = 2;
        boolean oneBitTrue = false;
        boolean populationStats = false;
        // wzor funkcji w EvaluationY()
        // wzor funckcji oceny w Evaluation()


        Osobnik[] population = new Osobnik[subjects_Amount];
        for(int j = 0; j < population.length; j++){
            population[j] = new Osobnik(bits_Amount, oneBitTrue);
        }

        List<Double> averageScore = new ArrayList<>();
        List<Double> averageY = new ArrayList<>();
        List<Double> iterations = new ArrayList<>();
        List<double[]> bestOneEachGen = new ArrayList<>();

        for (int i = 0; i < generations_Amount; i++) {
            System.out.print("---New generation--[ "+ i +" ]---");
            int[] genotypInt = ToInt(population);
            double[] evaluationY = EvaluationY(genotypInt);
            double[] score = Evaluation(genotypInt, evaluationY);
            double[] bestOneThisGen = BestOneThisGen(genotypInt, score, evaluationY);
            double[] percentageCon = PercentageContribution(score);
            double[] percentageSec = PercentageSections(percentageCon);
            Osobnik[] newPopulation = Reproduction(population, percentageSec);
            int[] newPopInt = ToInt(newPopulation);
            Osobnik[] crossPopulation = CrossingGenotypes(newPopulation, bits_Amount);
            int[] crossPopInt = ToInt(crossPopulation);
            Osobnik[] mutPopulation = Mutation(crossPopulation, mutationChance);
            int[] mutPopInt = ToInt(mutPopulation);
            double averageScoreThisGen = AverageScore(score);
            double averageYThisGen = AverageY(genotypInt, evaluationY);
            averageY.add(averageYThisGen);
            System.out.println(" | Average Y [ " + averageYThisGen + " ] | Best of this gen: [ X: " + bestOneThisGen[0] + " , Y: " + bestOneThisGen[1] +" , Score: " + bestOneThisGen[2] + " ]");
            bestOneEachGen.add(bestOneThisGen);
            if (populationStats){ ShowPop(population, genotypInt, score, percentageCon, percentageSec, newPopulation, newPopInt, crossPopulation, crossPopInt, mutPopulation, mutPopInt, averageScoreThisGen, averageYThisGen); }
            iterations.add((double)i);

            population = mutPopulation;
        }

        //Avarage Y value in successive generations
        Plot plot = Plot.plot(null).series(null, Plot.data().xy(iterations, averageY), null);

        try {
            plot.save("chart", "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] ToInt(Osobnik[] population){
        int[] genotypInt = new int[population.length];
        int j = 1;
        for(int k = 0; k < population.length; k++){
            for(int i = population[k].genotyp.length-1; i >= 0; i--){
                genotypInt[k] += population[k].genotyp[i] * j;
                j *= 2;
            }
            j = 1;
        }
        return genotypInt;
    }

    private static double[] EvaluationY(int[] genotypInt){ //obliczanie y ze wzoru tutaj, aby nie pisac go pozniej 4 razy
        double[] evaluationY = new double[genotypInt.length];
        for(int i = 0; i < genotypInt.length; i++){
            //Obliczanie Y ze wzoru
            //(x-30)^2-30 | Min x=30 y=-30 | https://www.wolframalpha.com/input/?i=%28x-30%29%5E2-30
            evaluationY[i] = (  Math.pow(genotypInt[i] - 30, 2) -30  );
        }
        return evaluationY;
    }

    private static double[] Evaluation(int[] genotypInt, double[] evaluationY){
        double[] score = new double[genotypInt.length];
        for(int i = 0; i < genotypInt.length; i++){

            //Funkcja oceny
            // -1
            score[i] = ( -1 ) * evaluationY[i];
        }

        double theWorst = score[0];
        for (int i = 0; i < genotypInt.length; i++) {
            if(score[i] < theWorst){
                theWorst = score[i];
            }
        }
        for (int i = 0; i < genotypInt.length; i++) {
                score[i] -= theWorst;
        }
        return score;
    }

    private static double[] PercentageContribution(double[] score){
        double[] percentageCon = new double[score.length];
        int ovrScore = 0;

        for(int i = 0; i < score.length; i++){
            ovrScore += score[i];
        }

        for(int i = 0; i < score.length; i++){
            percentageCon[i] = score[i]/ovrScore;
        }

        return percentageCon;
    }

    private static double[] PercentageSections(double[] percentageCon){
        double[] percentageSec = new double[percentageCon.length];
        double curPerc = 0;

        for(int i = 0; i < percentageCon.length; i++){
            curPerc += percentageCon[i];
            percentageSec[i] = curPerc;
        }

        return percentageSec;
    }

    private static Osobnik[] Reproduction(Osobnik[] population, double[] percentageSec){
        Osobnik[] newPop = new Osobnik[population.length];

        //losu losu 10 osobników
        for(int i = 0; i < population.length; i++){
            newPop[i]= population[i];
            Random random = new Random();
            //search perc table
            for (int j = 0; j < percentageSec.length; j++) {
                if (random.nextDouble() < percentageSec[j]){
                    newPop[i] = population[j];
                    break;
                }
            }

        }

        return newPop;
    }

    private static Osobnik[] CrossingGenotypes(Osobnik[] newPop, int bits_amount){
        Osobnik[] crossPop = new Osobnik[newPop.length];
        //crossPop = newPop;
        for(int j = 0; j < crossPop.length; j++){
            crossPop[j] = new Osobnik(bits_amount,false);
        }

        //tworzenie nowych osobników
        for(int i = 0; i < newPop.length; i+=2){
            Random random = new Random();
            int rand = random.nextInt(newPop[i].genotyp.length-1) + 1;
            //tnij na random osobnik[i]
            for(int j = 0; j < newPop[i].genotyp.length; j++){
                if (j < rand){
                    crossPop[i].genotyp[j] = newPop[i].genotyp[j];
                } else {
                    crossPop[i].genotyp[j] = newPop[i+1].genotyp[j];
                }
            }

            for(int j = 0; j < newPop[i+1].genotyp.length; j++){
                if (j < rand){
                    crossPop[i+1].genotyp[j] = newPop[i+1].genotyp[j];
                } else {
                    crossPop[i+1].genotyp[j] = newPop[i].genotyp[j];
                }
            }
        }
        return crossPop;
    }

    private static Osobnik[] Mutation(Osobnik[] crossPop, int mutationChance){

        Osobnik[] mutPop = new Osobnik[crossPop.length];
        for(int j = 0; j < crossPop.length; j++){
            mutPop[j] = crossPop[j];
        }

        for(int i = 0; i < mutPop.length; i++){
            Random random = new Random();

            for(int j = 0; j < mutPop[i].genotyp.length; j++){
                int rand = random.nextInt(99) + 1;
                if(rand < mutationChance){
                    switch(mutPop[i].genotyp[j]) {
                        case 0:
                            mutPop[i].genotyp[j] = 1;
                            break;
                        case 1:
                            mutPop[i].genotyp[j] = 0;
                            break;
                    }
                }
            }

        }
        return mutPop;
    }

    private static double AverageScore(double[] evaluation){
        double tempScore = 0;

        for(int i = 0; i < evaluation.length; i++){
            tempScore += evaluation[i];
        }
        double avgScore = tempScore/evaluation.length;
        return avgScore;
    }
    private static double AverageY(int[] genotypInt, double[] evaluationY){
        double tempY = 0;

        for(int i = 0; i < genotypInt.length; i++){
            tempY += evaluationY[i];
        }
        double avgY = tempY/genotypInt.length;
        return avgY;
    }

    private static double[] BestOneThisGen(int[] genotypInt, double[] score, double[] evaluationY){
        double[] bestOneThisGen = {-9999,-9999,-9999};
        for (int i = 0; i < genotypInt.length; i++) {
            if (score[i] > bestOneThisGen[2]){ //szuka najwiekszego score w tej generacji

                bestOneThisGen = new double[]{genotypInt[i],evaluationY[i], score[i]};
            }
        }
        return bestOneThisGen;
    }

    private static void ShowPop(Osobnik[] population, int[] genotypInt, double[] score, double[] percentageCon, double[] percentageSec, Osobnik[] newPopulation, int[] newPopInt, Osobnik[] crossPopulation, int[] crossPopInt, Osobnik[] mutPopulation, int[] mutPopInt, double averageScoreThisGen, double averageYThisGen){
        for (int j = 0; j < population.length; j++) {
            for (int i = 0; i < population[j].genotyp.length; i++) {
                System.out.print(population[j].genotyp[i]);
            }
            System.out.print(" [" + genotypInt[j] + "]");
            System.out.print(" | eval: " + score[j]);
            System.out.printf(" | percentageCon: %.2f", percentageCon[j]);
            System.out.printf(" | percentageSec: %.2f", percentageSec[j]);
            System.out.print(" | newPopulation: ");
            for (int i = 0; i < newPopulation[j].genotyp.length; i++) {
                System.out.print(newPopulation[j].genotyp[i]);
            }
            System.out.print(" [" + newPopInt[j] + "]");
            System.out.print(" | crossPopulation: ");
            for (int i = 0; i < crossPopulation[j].genotyp.length; i++) {
                System.out.print(crossPopulation[j].genotyp[i]);
            }
            System.out.print(" [" + crossPopInt[j] + "]");
            System.out.print(" | mutPopulation: ");
            for (int i = 0; i < mutPopulation[j].genotyp.length; i++) {
                System.out.print(mutPopulation[j].genotyp[i]);
            }
            System.out.print(" [" + mutPopInt[j] + "]");
            System.out.print("\n");
        }
        System.out.println("-----------------------------------");
        System.out.println("average score: [ " + averageScoreThisGen + " ] after detraction of TheWorst score");
        System.out.println("average Y: [ " + averageYThisGen + " ]");
        System.out.println();
    }
}
