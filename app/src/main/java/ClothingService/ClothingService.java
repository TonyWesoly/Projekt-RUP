package ClothingService;

public class ClothingService {

    private String clothes;
    private int time;


    public void setClothes(double temp){

        if(temp >= 21){
            this.clothes = "\n|Matka: " + TempEnum.HOT.getRandomClothes()
                    + "\n|Dziecko 1: " + TempEnum.HOTFORCHILD.getRandomClothes()
                    + "\n|Dziecko 2: " + TempEnum.HOTFORCHILD.getRandomClothes();
            this.time = TempEnum.HOT.getTime();
        }
        if(temp < 21 && temp >= 15){
            this.clothes = "\n|Matka: " + TempEnum.WARM.getRandomClothes()
                    + "\n|Dziecko 1: " + TempEnum.WARMFORCHILD.getRandomClothes()
                    + "\n|Dziecko 2: " + TempEnum.WARMFORCHILD.getRandomClothes();
            this.time = TempEnum.WARM.getTime();
        }
        if(temp < 15 && temp >= 1){
            this.clothes = "\n|Matka: " + TempEnum.COLD.getRandomClothes()
                    + "\n|Dziecko 1: " + TempEnum.COLDFORCHILD.getRandomClothes()
                    + "\n|Dziecko 2: " + TempEnum.COLDFORCHILD.getRandomClothes();
            this.time = TempEnum.COLD.getTime();
        }
        if(temp < 1){
            this.clothes = "\n|Matka: " + TempEnum.FREEZING.getRandomClothes()
                    + "\n|Dziecko 1: " + TempEnum.FREEZINGFORCHILD.getRandomClothes()
                    + "\n|Dziecko 2: " + TempEnum.FREEZINGFORCHILD.getRandomClothes();
            this.time = TempEnum.FREEZING.getTime();
        }
    }


    public void addAccessories(String weather){
        String x = weather.toLowerCase();
        if(x.contains("snow") || x.contains("blizzard")){
            this.clothes = this.clothes + "\n|Akcesoria: czapka";
            this.time = this.time + 2;
        }else if(x.contains("thunder")){
            this.clothes = this.clothes + "\n|Akcesoria: płaszcz przeciwdeszczowy";
            this.time = this.time + 2;
        }else if(x.contains("rain") || x.contains("drizzle") || x.contains("pellets") || x.contains("sleet")){
            this.clothes = this.clothes + "\n|Akcesoria: parasol";
            this.time = this.time + 2;
        }else if(x.contains("sunny") || x.contains("clear")){
            this.clothes = this.clothes + "\n|Akcesoria: okulary przeciwsłoneczne";
            this.time = this.time + 2;
        }

    }

    public String getClothes(){
        return this.clothes;
    }
    public int getTime(){
        return this.time;
    }

}
