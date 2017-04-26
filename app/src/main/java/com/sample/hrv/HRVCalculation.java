package com.sample.hrv;

import java.util.ArrayList;

/**
 * Created by isaali93 on 25/04/2017.
 */

public class HRVCalculation implements Runnable {

    ArrayList <Integer> RRValues = new ArrayList();
    int [] HRV = new int [2];

    @Override
    public void run() {

    }

    public int [] HRVCalculation (int [] RR){
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
