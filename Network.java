public class Network{
  private int layer;
  private double[][][] weights;
  private int numOfLayers;
  private int totalFit;
  private int dateFit;
  private int tweetFit;

  public Network(int layer, int numOfLayers){
    this.layer=layer;
    this.numOfLayers=numOfLayers;
    weights=new double[layer][layer][numOfLayers];
    //Setting up random weights
    for(int x=0;x<layer;x++){
      for(int y=0;y<layer;y++){
        for(int z=0;z<numOfLayers;z++){
          weights[x][y][z]=((Math.random()*2));
        }
      }
    }
  }

  public int getFitness(){

    return totalFit;
  }

  public void setWeights(double[][][] newWeights){
    weights=newWeights;
    return;
  }

  public double[][][] getWeights(){
    return weights;
  }

  //Gets the input tweet, runs it through the network, compares to the target tweet
  //First 140 values=tweet; valued at 75% of overall fitness
  //Next 14 values=date; valued at 25% of overall fitness
  //  2:Day;40%
  //  2:Month;20%
  //  4:Year;5%
  //  2:Hour;15%
  //  2:Minute;15%
  //  2:Second;5%
  public void test(double[] input, double[] target){
    double[] output=input;
    //Going through, layer by layer
    for(int z=0;z<numOfLayers;z++){
      double[] lastLayer=new double[input.length];
      //Setting up the temporary last layer as reference
      for(int i=0;i<input.length;i++){
        lastLayer[i]=output[i];
      }
      //Summation of nodes [the bit that might change]
      for(int x=0;x<layer;x++){
        for(int y=0;y<layer;y++){
          output[y]+=(lastLayer[x]*weights[x][y][z]);
        }
      }
    }
    //Comparing results with target
    double goodT=0;
    double goodD=0;
    double goodM=0;
    double goodY=0;
    double goodMin=0;
    double goodH=0;
    double goodS=0;
    for(int i=0;i<output.length;i++){
      if((int)(output[i]%255)==(int)target[i]){
        if(i<140){
          //Tweet
          goodT++;
        }else if(i<142){
          //Day
          goodD++;
        }else if(i<144){
          //Month
          goodM++;
        }else if(i<148){
          //Year
          goodY++;
        }else if(i<150){
          //Hour
          goodH++;
        }else if(i<152){
          //Minute
          goodMin++;
        }else{
          //Second
          goodS++;
        }
      }
    }

    tweetFit=(int)(((double)goodT/140.0)*100);

    double hFit=goodH/2.0;
    double minFit=goodMin/2.0;
    double sFit=goodS/2.0;
    double mFit=goodM/2.0;
    double dFit=goodD/2.0;
    double yFit=goodY/4.0;
    //Splitting up fitness
    dateFit=(int)((dFit*0.4+mFit*0.2+yFit*0.05+hFit*0.15+minFit*0.15+sFit*0.05)*100);

    totalFit=(int)(tweetFit*0.75+dateFit*0.25);
    return;
  }

  public Network[] generate(Network best, int size){
    Network[] output=new Network[size];
    for(int i=0;i<size;i++){
      output[i]=new Network(layer,numOfLayers);
      //Giving new network its genes
      double[][][] newWeights=new double[layer][layer][numOfLayers];
      for(int x=0;x<layer;x++){
        for(int y=0;y<layer;y++){
          for(int z=0;z<numOfLayers;z++){
            double r=(Math.random()*20);
            //Selecting which weights are selected for new network
            if(r>10){
              newWeights[x][y][z]=best.getWeights()[x][y][z];
            }else if(r>1){
              newWeights[x][y][z]=weights[x][y][z];
            }else{
              newWeights[x][y][z]=((Math.random()*2));
            }
          }
        }
      }
    }
    return output;
  }
}
