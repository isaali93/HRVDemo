package com.sample.hrv;

import java.util.ArrayList;

/**
 * Created by isaali93 on 25/04/2017.
 */

public class HRVCalculation implements Runnable {

    ArrayList <Integer> RRValues = new ArrayList();

    @Override
    public void run() {

    }

    int [] HRV = new int [3];

    public int [] HRVCalculation (ArrayList<Float> RRValues){

        float RRSum = 0;
        float MRR = 0;

        float SDNNTotal = 0;
        float SDNN = 0;

        float RMSSD = 0;
        float RRDifference = 0;
        float RRDSq = 0;
        float RRDSum = 0;
        float RRDDiv = 0;
        for(int i = 0; i< RRValues.size(); i++){
            RRSum = RRSum + RRValues.get(i);
        }
        MRR = RRSum/(RRValues.size()-1);
        HRV [0] = (int)MRR;

        for(int i=0; i < RRValues.size(); i++){
            SDNNTotal += Math.pow(RRValues.get(i) - MRR, 2);
        }
        SDNN = (float)Math.sqrt(SDNNTotal/(RRValues.size()-1));
        //HRV [1] = (int)SDNN;
        HRV [1] = (int)SDNN;

        for(int i = 1; i<RRValues.size(); i++){
            RRDifference = RRValues.get(i) - RRValues.get(i-1);
            RRDSq = (float)Math.pow(RRDifference, 2);
            RRDSum += RRDSq;
        }

        RRDDiv = RRDSum/RRValues.size();
        RMSSD = (float)Math.sqrt(RRDDiv);
        HRV[2] = (int)RMSSD;
        /*
        int RRDifference1 = Math.abs(RR[0] - RR[1]);
        int RRDifference2 = Math.abs(RR[1] - RR[2]);
        RRDifference1 =(int)(Math.pow(RRDifference1, 2));
        RRDifference2 =(int)(Math.pow(RRDifference2, 2));
        int RRSum = RRDifference1 + RRDifference2;
        HRV [0] = (int)Math.sqrt(RRSum/2);
        RRValues.add(HRV[0]);
        /*
        int AverageRMSSD = 0;
        int sumRMSSD = 0;
        for(int i = 0; i<RRValues.size(); i++){
            sumRMSSD = sumRMSSD + RRValues.get(i);
        }
        AverageRMSSD = sumRMSSD/RRValues.size();
        HRV[1] = AverageRMSSD;
        */
        return HRV;
    }

}
