import java.util.*;
import java.text.*;
import twitter4j.*;
import java.io.*;

public class main{
  public static void delay(long d){
    long start=System.currentTimeMillis();
    long end=start+d;
    while(start<end){
      start=System.currentTimeMillis();
    }
    return;
  }

  public static void main(String[] args) throws TwitterException{
    //access the twitter API using twitter4j.properties file
    Scanner scan=new Scanner(System.in);
    Twitter twitter = TwitterFactory.getSingleton();
    SimpleDateFormat sdfa = new SimpleDateFormat("HH:mm:ss");
    String stuff=sdfa.format(new Date());
    System.out.println("Initiated at "+stuff);
    while(true){
      System.out.println("Whos twitter to analyse?");

      String user=scan.nextLine();
      Paging pg=new Paging();
      //Number of tweets to obtain
      int numberOfTweets = 1000;
      long lastID = Long.MAX_VALUE;
      ArrayList<Status> tweets = new ArrayList<Status>();
      while (tweets.size () < numberOfTweets) {
        try {
          tweets.addAll(twitter.getUserTimeline(user,pg));
          System.out.println("Gathered " + tweets.size() + " tweets");
          for (Status t: tweets)
          if(t.getId() < lastID) lastID = t.getId();
        }
        catch (TwitterException te) {
          System.out.println("Couldn't connect: " + te);
        };
        pg.setMaxId(lastID-1);
      }
      //Arrays of tweets to use later
      List<char[]> firstTweet=new ArrayList<>();
      List<char[]> nextTweet=new ArrayList<>();
      for(int i=tweets.size()-1;i>0;i--){
        //Actually converting tweets into characters
        Status s=tweets.get(i);
        char[] twitI=new char[140];
        String tweetI=s.getText();
        char[] untwitI=tweetI.toCharArray();
        for(int a=0;a<140;a++){
          if(a<untwitI.length){
            twitI[a]=untwitI[a];
          }else{
            twitI[a]=32;
          }
        }
        Status sI=tweets.get(i-1);
        char[] twitII=new char[140];
        String tweetII=sI.getText();

        char[] untwitII=tweetII.toCharArray();
        for(int a=0;a<140;a++){
          if(a<untwitII.length){
            twitII[a]=untwitII[a];
          }else{
            twitII[a]=32;
          }
        }

        //Tweet strings are twitI and twitII
        //Times are dateI and dateII
        //Overall is TI and TII
        Date dI=s.getCreatedAt();
        Date dII=sI.getCreatedAt();

        String daI=new SimpleDateFormat("ddMMyyyyHHmmss").format(dI);
        String daII=new SimpleDateFormat("ddMMyyyyHHmmss").format(dII);

        char[] dateI=daI.toCharArray();
        char[] dateII=daII.toCharArray();
        char[] TI=new char[154];
        char[] TII=new char[154];
        for(int k=0;k<154;k++){
          if(k<140){
            TI[k]=twitI[k];
            TII[k]=twitII[k];
          }else{
            TI[k]=dateI[k-140];
            TII[k]=dateII[k-140];
          }
        }
        //Adding character versions of tweets to list
        firstTweet.add(TI);
        nextTweet.add(TII);
      }

      //Sending off to be tested
      //Start
      //number of networks is pop
      int pop=500;
      int[] fitness=new int[pop];
      Network[] generation=new Network[pop];
      System.out.println("Testing intial setup...");
      for(int j=0;j<pop;j++){
        generation[j]=new Network(154,3);
        fitness[j]=0;
        for(int i=0;i<firstTweet.size();i++){
          //Converting char to double
          double[] a=new double[154];
          double[] b=new double[154];
          for(int k=0;k<firstTweet.get(i).length;k++){
            a[k]=(double)(firstTweet.get(i)[k]);
            b[k]=(double)(nextTweet.get(i)[k]);
          }
          if(i==5){
            //System.out.println(a[0]+" "+b[0]);
          }if(i==6){
            //System.out.println(a[0]+" "+b[0]);
          }
          //Testing each network
          generation[j].test(a,b);
          int howWell=generation[j].getFitness();
          if(i==5){
            //System.out.println(a[0]+" "+b[0]);
          }
          fitness[j]+=howWell;
        }
        //Getting average fitness
        fitness[j]=fitness[j]/firstTweet.size();
      }
      System.out.println("Initial test run completed, finding fittest");
      int fittest=0;
      int secondFittest=0;
      int track=0;
      int trackTwo=0;
      for(int j=0;j<pop;j++){
        System.out.println(fitness[j]+" "+j);
        if(fitness[j]>fittest){
          fittest=fitness[j];
          track=j;
          System.out.println(j);
        }else if(fitness[j]>secondFittest){
          secondFittest=fitness[j];
          trackTwo=j;
          System.out.println(j);
        }
      }
      System.out.println("Subject "+track+" was best, with a fitness of "+fittest+"%");
      //Looping
      //Forever finding the fittest until fitness is greater than 50% accuracy
      while(fittest<50){
        generation=generation[trackTwo].generate(generation[track],pop);
        for(int j=0;j<pop;j++){
          fitness[j]=0;
          for(int i=0;i<firstTweet.size();i++){
            //Converting char to double
            double[] aa=new double[154];
            double[] bb=new double[154];
            for(int k=0;k<firstTweet.get(i).length;k++){
              aa[k]=(double)firstTweet.get(i)[k];
              bb[k]=(double)nextTweet.get(i)[k];
            }

            generation[j].test(aa,bb);
            int howWell=generation[j].getFitness();
            fitness[j]+=howWell;
          }

          //System.out.println(fitness[j]+" subject "+j);
          //System.out.println(generation[j].getWeights()[0][0][0]);
          fitness[j]=fitness[j]/firstTweet.size();
        }
        fittest=0;
        secondFittest=0;
        track=0;
        trackTwo=0;
        for(int j=0;j<pop;j++){
          if(fitness[j]>fittest){
            fittest=fitness[j];
            track=j;
          }else if(fitness[j]>secondFittest){
            secondFittest=fitness[j];
            trackTwo=j;
          }
        }
        System.out.println("Subject "+track+" was best, with a fitness of "+fittest+"%");


      }
      //Prediction after [insert number]% accuracy
    }
  }

}
