package com.sample.hrv.sensor;


import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import com.sample.hrv.DeviceScanActivity;
import com.sample.hrv.DeviceServicesActivity;
import com.sample.hrv.HRVCalculation;

import org.w3c.dom.Text;

import static java.lang.Math.max;
import static java.lang.Math.pow;

/**
 * Created by  by olli on 3/28/2014.
 */
public class BleHeartRateSensor extends BleSensor<float[]> {

    private final static String TAG = BleHeartRateSensor.class.getSimpleName();

    private static final String UUID_SENSOR_BODY_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
    
    private static final int SENSOR_BODY_LOCATION_OTHER = 0;
    private static final int SENSOR_BODY_LOCATION_CHEST = 1;
    private static final int SENSOR_BODY_LOCATION_WRIST = 2;
    private static final int SENSOR_BODY_LOCATION_FINGER = 3;
    private static final int SENSOR_BODY_LOCATION_HAND = 4;
    private static final int SENSOR_BODY_LOCATION_EAR = 5;
    private static final int SENSOR_BODY_LOCATION_FOOT = 6;
    
    private int location = -1;
    
	BleHeartRateSensor() {
		super();
	}

	@Override
	public String getName() {
		return "Heart rate";
	}

	@Override
	public String getServiceUUID() {
		return "0000180d-0000-1000-8000-00805f9b34fb";
	}

	public static String getServiceUUIDString() {
		return "0000180d-0000-1000-8000-00805f9b34fb";
	}
	
	@Override
	public String getDataUUID() {
		return "00002a37-0000-1000-8000-00805f9b34fb";
	}

	public static String getDataUUIDString() {
		return "00002a37-0000-1000-8000-00805f9b34fb";
	}
	
	@Override
	public String getConfigUUID() {
		return "00002902-0000-1000-8000-00805f9b34fb";
	}
	
    @Override
    public String getCharacteristicName(String uuid) {
        if (UUID_SENSOR_BODY_LOCATION.equals(uuid))
            return getName() + " Sensor body location";
        return super.getCharacteristicName(uuid);
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGattCharacteristic c) {
        super.onCharacteristicRead(c);

        Log.d(TAG, "onCharacteristicsReas");
        
        if ( !c.getUuid().toString().equals(UUID_SENSOR_BODY_LOCATION) )
            return false;

        location = c.getProperties();
        Log.d(TAG, "Sensor body location: " + location);
        return true;
    }


	float minHR = Float.MAX_VALUE;
	float maxHR = Float.MIN_VALUE;
	float avgHR = 0;
	float sumHR = 0;
	float avg = 0;

	float maxRR = Float.MIN_VALUE;
	float minRR = Float.MAX_VALUE;

	ArrayList<Float> RRValues = new ArrayList<Float>();
	int [] HRVValues = new int[3];
	int [] HRV = new int [7];

	ArrayList <Integer> RMSSD = new ArrayList<Integer>();
	ArrayList <Integer> SDNN = new ArrayList<Integer>();

	String Playlist = "<a href='https://www.youtube.com/watch?v=lTZ8gQD4vSk'> Try some soothing music and images. </a>";

	int stressThreshold = 0;

	@Override
	public String getDataString() {
		final float[] data = getData();
		if (data[0] < minHR){
			minHR = data[0];
		}
		if(data[0] > maxHR){
			maxHR = data[0];
		}
		avg = avg + 1;
		sumHR = sumHR + data[0];
		avgHR = sumHR/avg;

		int avgHRInteger = (int)(Math.round(avgHR));


		if (data[1] < minRR){
			minRR = data[1];
		}
		if(data[1] > maxRR){
			maxRR = data[1];
		}

		DeviceServicesActivity.stress_tips.setClickable(true);
		DeviceServicesActivity.stress_tips.setMovementMethod(LinkMovementMethod.getInstance());
		DeviceServicesActivity.stress_tips.setText(Html.fromHtml(Playlist));

		HRVCalculation hrv = new HRVCalculation();
		RRValues.add(data[1]);

		if(RRValues.size() > 300){
			HRVValues = hrv.HRVCalculation(RRValues);
			if(HRVValues[2] < 15){
				stressThreshold++;
				if(stressThreshold > 1800) {
					DeviceServicesActivity.nm.notify(45612, DeviceServicesActivity.notification.build());
				}
				DeviceServicesActivity.stress_text.setText("Stress Level is High");
			}else{
				if(stressThreshold < 1600 && stressThreshold > 0) {
					stressThreshold--;
					DeviceServicesActivity.nm.cancel(45612);
				}
				DeviceServicesActivity.stress_text.setText("Stress Level is Good");
			}
		}

		if(RRValues.size() == 7200){
			RMSSD.add(HRVValues[2]);
			SDNN.add(HRVValues[1]);
			RRValues.clear();
		}



		return "\nHeart Rate=" + data[0] + " bpm"
			+ "\nMin HR=" + minHR + " bpm" + "\nMax HR=" + maxHR + " bpm"
				//+ "\nAvg HR=" + avgHRInteger + " bpm" + "\n"
				+ "\n"
				+ "\nR-R Interval=" + data[1] + " ms"
				//+ "\nMRR=" + HRVValues[0] + " ms"
				+ "\nSDNN=" + HRVValues[1] + " ms"
				+ "\nRMSSD=" + HRVValues[2] + " ms"
				;
	}

	@Override
	public float[] parse(BluetoothGattCharacteristic c) {

		double heartRate = extractHeartRate(c);
		double contact = extractContact(c);
		double energy = extractEnergyExpended(c);
		Integer[] interval = extractBeatToBeatInterval(c);
		
		float[] result = null;
		if (interval != null) {
			result = new float[interval.length + 1];
		} else {
			result = new float[2];
			result[1] = -1.0f;
		}
		result[0] = (float) heartRate;
		
		if (interval != null) {
			for (int i = 0; i < interval.length; i++) {
				result[i+1] = interval[i].floatValue();
			}
		}
		
		return result;
	}

	private static double extractHeartRate(
			BluetoothGattCharacteristic characteristic) {

		int flag = characteristic.getProperties();
		Log.d(TAG, "Heart rate flag: " + flag);
		int format = -1;
		// Heart rate bit number format
		if ((flag & 0x01) != 0) {
			format = BluetoothGattCharacteristic.FORMAT_UINT16;
			Log.d(TAG, "Heart rate format UINT16.");
		} else {
			format = BluetoothGattCharacteristic.FORMAT_UINT8;
			Log.d(TAG, "Heart rate format UINT8.");
		}
		final int heartRate = characteristic.getIntValue(format, 1);
		Log.d(TAG, String.format("Received heart rate: %d", heartRate));
		return heartRate;
	}
	
	private static double extractContact(
			BluetoothGattCharacteristic characteristic) {

		int flag = characteristic.getProperties();
		int format = -1;
		// Sensor contact status
		if ((flag & 0x02) != 0) {
			Log.d(TAG, "Heart rate sensor contact info exists");
			if ((flag & 0x04) != 0) {
				Log.d(TAG, "Heart rate sensor contact is ON");
			} else {
				Log.d(TAG, "Heart rate sensor contact is OFF");
			}
		} else  {
			Log.d(TAG, "Heart rate sensor contact info doesn't exists");
		}
		//final int heartRate = characteristic.getIntValue(format, 1);
		//Log.d(TAG, String.format("Received heart rate: %d", heartRate));
		return 0.0d;
	}
	
	private static double extractEnergyExpended(
			BluetoothGattCharacteristic characteristic) {

		int flag = characteristic.getProperties();
		int format = -1;
		// Energy calculation status
		if ((flag & 0x08) != 0) {
			Log.d(TAG, "Heart rate energy calculation exists.");
		} else {
			Log.d(TAG, "Heart rate energy calculation doesn't exists.");
		}
		//final int heartRate = characteristic.getIntValue(format, 1);
		//Log.d(TAG, String.format("Received heart rate: %d", heartRate));
		return 0.0d;
	}
	
	private static Integer[] extractBeatToBeatInterval(
			BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        int format = -1;
        int energy = -1;
        int offset = 1; // This depends on hear rate value format and if there is energy data
        int rr_count = 0;
        
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Heart rate format UINT16.");
            offset = 3;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Heart rate format UINT8.");
            offset = 2;
        }
        if ((flag & 0x08) != 0) {
            // calories present
            energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;
            Log.d(TAG, "Received energy: {}"+ energy);
        }
        if ((flag & 0x16) != 0){
            // RR stuff.
            Log.d(TAG, "RR stuff found at offset: "+ offset);
            Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
            rr_count = ((characteristic.getValue()).length - offset) / 2;
            Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
            Log.d(TAG, "rr_count: "+ rr_count);
			if (rr_count > 0) {
				Integer[] mRr_values = new Integer[rr_count];
				for (int i = 0; i < rr_count; i++) {
					mRr_values[i] = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT16, offset);
					offset += 2;
					Log.d(TAG, "Received RR: " + mRr_values[i]);
				}
				return mRr_values;
			}
        }
        Log.d(TAG, "No RR data on this update: ");
        return null;
	}



}
