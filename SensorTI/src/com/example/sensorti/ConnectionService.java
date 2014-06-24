package com.example.sensorti;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

public class ConnectionService extends IntentService implements
		BluetoothAdapter.LeScanCallback {
	// variables declaration

	/*private LocationManager locationManager;
	private LocationListener locationListener;*/

	private static final String TAG = "BluetoothGattActivity";

	private static final String DEVICE_NAME = "SensorTag";

	/* Humidity Service */
	private static final UUID HUMIDITY_SERVICE = UUID
			.fromString("f000aa20-0451-4000-b000-000000000000");
	private static final UUID HUMIDITY_DATA_CHAR = UUID
			.fromString("f000aa21-0451-4000-b000-000000000000");
	private static final UUID HUMIDITY_CONFIG_CHAR = UUID
			.fromString("f000aa22-0451-4000-b000-000000000000");
	/* Barometric Pressure Service */
	private static final UUID PRESSURE_SERVICE = UUID
			.fromString("f000aa40-0451-4000-b000-000000000000");
	private static final UUID PRESSURE_DATA_CHAR = UUID
			.fromString("f000aa41-0451-4000-b000-000000000000");
	private static final UUID PRESSURE_CONFIG_CHAR = UUID
			.fromString("f000aa42-0451-4000-b000-000000000000");
	private static final UUID PRESSURE_CAL_CHAR = UUID
			.fromString("f000aa43-0451-4000-b000-000000000000");
	/* Acceleromter configuration servcie */
	private static final UUID ACCELEROMETER_SERVICE = UUID
			.fromString("f000aa10-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_DATA_CHAR = UUID
			.fromString("f000aa11-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_CONFIG_CHAR = UUID
			.fromString("f000aa12-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_PERIOD_CHAR = UUID
			.fromString("f000aa13-0451-4000-b000-000000000000");

	/* Gyroscope Configuration service */
	private static final UUID GYROSCOPE_SERVICE = UUID
			.fromString("f000aa50-0451-4000-b000-000000000000");
	private static final UUID GYROSCOPE_DATA_CHAR = UUID
			.fromString("f000aa51-0451-4000-b000-000000000000");
	private static final UUID GYROSCOPE_CONFIG_CHAR = UUID
			.fromString("f000aa52-0451-4000-b000-000000000000");
	/* Client Configuration Descriptor */
	private static final UUID CONFIG_DESCRIPTOR = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private BluetoothAdapter mBluetoothAdapter;
	private SparseArray<BluetoothDevice> mDevices;

	private BluetoothGatt mConnectedGatt;

	// variable declaration end

	public ConnectionService() {
		super("ConnectionService");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = manager.getAdapter();
		mDevices = new SparseArray<BluetoothDevice>();
		

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "handleIntent");
		startScan();

	}

/*	private void broadCast() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.quchen.flappycow");
		intent.putExtra("data", "3");
		sendBroadcast(intent);
	}*/
	
	/* BluetoothAdapter.LeScanCallback */
	
	@Override
	public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
		// TODO Auto-generated method stub
		Log.d(TAG, "in onlescan method");
		//Log.d(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
		/*
		 * We are looking for SensorTag devices only, so validate the name that
		 * each device reports before adding it to our collection
		 */
		if (DEVICE_NAME.equals(device.getName())) {

			mDevices.put(device.hashCode(), device);
				//device.getAddress().equals("BC:6A:29:AC:5F:14")
		

			mConnectedGatt = device.connectGatt(this, false, mGattCallback);

			
			mBluetoothAdapter.stopLeScan(this);
		}
		
	}

	private void startScan() {
		Log.d(TAG, "scan");
		if (mBluetoothAdapter.startLeScan(this)) {
			Log.d(TAG, "started");
		} else {
			Log.d(TAG, "not started");
		}

		// mHandler.postDelayed(mStopRunnable, 2500);
	}

	/*
	 * In this callback, we've created a bit of a state machine to enforce that
	 * only one characteristic be read or written at a time until all of our
	 * sensors are enabled and we are registered to get notifications.
	 */
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		/* State Machine Tracking */
		private int mState = 0;

		private void reset() {
			mState = 0;
		}

		private void advance() {
			mState++;
		}

		/*
		 * Send an enable command to each sensor by writing a configuration
		 * characteristic. This is specific to the SensorTag to keep power low
		 * by disabling sensors you aren't using.
		 */
		private void enableNextSensor(BluetoothGatt gatt) {
			Log.d("sensor", "enable");
			BluetoothGattCharacteristic characteristic;
			switch (mState) {
			case 0:
				Log.d(TAG, "Enabling accelerometer");
				characteristic = gatt.getService(ACCELEROMETER_SERVICE)
						.getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
				characteristic.setValue(new byte[] { 0x01 });
				break;
			
			case 1:
				Log.d(TAG, "Enabling config gyroscope");
				characteristic = gatt.getService(GYROSCOPE_SERVICE)
						.getCharacteristic(GYROSCOPE_CONFIG_CHAR);
				characteristic.setValue(new byte[] { 0x07 });
				break;
/*			case 1:
				Log.d(TAG, "Enabling config pressure cals");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_CONFIG_CHAR);
				characteristic.setValue(new byte[] { 0x01 });
				break;
			case 2:
				Log.d(TAG, "Enabling config pressure");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_CONFIG_CHAR);
				characteristic.setValue(new byte[] { 0x01 });
				break;*/
			case 2:
				Log.d(TAG, "Enabling config humidity");
				characteristic = gatt.getService(HUMIDITY_SERVICE)
						.getCharacteristic(HUMIDITY_CONFIG_CHAR);
				characteristic.setValue(new byte[] { 0x01 });
				break;

			default:
				// mHandler.sendEmptyMessage(MSG_DISMISS);
				Log.d(TAG, "All Sensors Enabled 1");
				return;
			}

			gatt.writeCharacteristic(characteristic);
		}

		private void readNextSensor(BluetoothGatt gatt) {
			BluetoothGattCharacteristic characteristic;
			switch (mState) {

			case 0:
				Log.d(TAG, "Reading accelerometer");
				characteristic = gatt.getService(ACCELEROMETER_SERVICE)
						.getCharacteristic(ACCELEROMETER_DATA_CHAR);
				break;
			case 1:
				Log.d(TAG, "Reading Gyroscope");
				characteristic = gatt.getService(GYROSCOPE_SERVICE)
						.getCharacteristic(GYROSCOPE_DATA_CHAR);
				break;
			/*case 1:
				Log.d(TAG, "Reading Pressure cals");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_CAL_CHAR);
				break;
			case 2:
				Log.d(TAG, "Reading Pressure");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_DATA_CHAR);
				break;*/
			case 2:
				Log.d(TAG, "Reading Humidity");
				characteristic = gatt.getService(HUMIDITY_SERVICE)
						.getCharacteristic(HUMIDITY_DATA_CHAR);
				break;

			default:
				
				Log.d(TAG, "All Sensors Enabled 2");
				return;
			}

			gatt.readCharacteristic(characteristic);
		}

		/*
		 *  * Enable notification of changes on the data characteristic for each
		 * sensor by writing the ENABLE_NOTIFICATION_VALUE flag to that
		 * characteristic's configuration descriptor.
		 */
		private void setNotifyNextSensor(BluetoothGatt gatt) {
			BluetoothGattCharacteristic characteristic;
			switch (mState) {

			case 0:
				Log.d(TAG, "Set notify accelerometer");
				characteristic = gatt.getService(ACCELEROMETER_SERVICE)
						.getCharacteristic(ACCELEROMETER_DATA_CHAR);
				break;

			case 1:
				Log.d(TAG, "Config gyroscope data");
				characteristic = gatt.getService(GYROSCOPE_SERVICE)
						.getCharacteristic(GYROSCOPE_DATA_CHAR);
				break;
				
				
			/*case 1:
				Log.d(TAG, "Config Pressure data cals");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_CAL_CHAR);
				break;
				
			case 2:
				Log.d(TAG, "Config Pressure data");
				characteristic = gatt.getService(PRESSURE_SERVICE)
						.getCharacteristic(PRESSURE_DATA_CHAR);
				break;*/
				
			case 2:
				Log.d(TAG, "Config Humidity data");
				characteristic = gatt.getService(HUMIDITY_SERVICE)
						.getCharacteristic(HUMIDITY_DATA_CHAR);
				break;

			default:
				
				Log.d(TAG, "All Sensors Enabled 3");
				return;
			}

			// Enable local notifications
			gatt.setCharacteristicNotification(characteristic, true);
			// Enabled remote notifications
			BluetoothGattDescriptor desc = characteristic
					.getDescriptor(CONFIG_DESCRIPTOR);
			desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(desc);
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			Log.d(TAG, "Connection State Change: " + status + " -> "
					+ connectionState(newState));
			if (status == BluetoothGatt.GATT_SUCCESS
					&& newState == BluetoothProfile.STATE_CONNECTED) {
				/*
				 * Once successfully connected, we must next discover all the
				 * services on the device before we can read and write their
				 * characteristics.
				 */
				gatt.discoverServices();
			
				// "Discovering Services..."));
			} else if (status == BluetoothGatt.GATT_SUCCESS
					&& newState == BluetoothProfile.STATE_DISCONNECTED) {
				/*
				 * If at any point we disconnect, send a message to clear the
				 * weather values out of the UI
				 */
			} else if (status != BluetoothGatt.GATT_SUCCESS) {
				/*
				 * If there is a failure at any stage, simply disconnect
				 */
				gatt.disconnect();
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.d(TAG, "Services Discovered: " + status);
			/*
			 * With services discovered, we are going to reset our state machine
			 * and start working through the sensors we need to enable
			 */
			reset();
			enableNextSensor(gatt);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// For each read, pass the data up to the UI thread to update the
			// display
			if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
				Log.d("sensor", "accelerometer");
			}
			if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
				Log.d("sensor", "gyroscope");
			}
			/*if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
				Log.d("sensor", "Pressure");
			}
			if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
				Log.d("sensor", "Pressure calc");
			}*/
			if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
				Log.d("sensor", "Humidity");
			}

			// After reading the initial value, next we enable notifications
			setNotifyNextSensor(gatt);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// After writing the enable flag, next we read the initial value
			readNextSensor(gatt);
		}
		
		

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			/*
			 * After notifications are enabled, all updates from the device on
			 * characteristic value changes will be posted here. Similar to
			 * read, we hand these up to the UI thread to update the display.
			 */
			/*if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
				updateAccelerometerCals(characteristic);
			}
			
			if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
				updateGyroValues(characteristic);
			}
			
			if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
				updatePressureCalValues(characteristic);
			}
			
			if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
				updatePressureValues(characteristic);
			}
			
			
			if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
				updateHumidityValues(characteristic);
			}*/
			/*updatePressureCalValues(characteristic);
			updatePressureValues(characteristic);
			updateHumidityValues(characteristic);*/

			updateAccelerometerCals(characteristic);
		}

		


		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			// Once notifications are enabled, we move to the next sensor and
			// start over with enable
			advance();
			enableNextSensor(gatt);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.d(TAG, "Remote RSSI: " + rssi);
		}

		private String connectionState(int status) {
			switch (status) {
			case BluetoothProfile.STATE_CONNECTED:
				return "Connected";
			case BluetoothProfile.STATE_DISCONNECTED:
				return "Disconnected";
			case BluetoothProfile.STATE_CONNECTING:
				return "Connecting";
			case BluetoothProfile.STATE_DISCONNECTING:
				return "Disconnecting";
			default:
				return String.valueOf(status);
			}
		}
	};

	/*private void updateGyroValues(BluetoothGattCharacteristic characteristic) {
		String gyroData = SensorTagData.extractGyroscopeReading(characteristic,
				0);
		String gyroValue =string +"\t" +gyroData ;
		SaveData(gyroValue);
		
	}*/
	
	private void updateAccelerometerCals(
			BluetoothGattCharacteristic characteristic) {
		Float[] values = SensorTagData.extractAccelerometerReading(
				characteristic, 0);

		Log.d("values",
				"x :" + values[0].toString() + "y:" + values[1].toString()
						+ "z:" + values[2].toString());

		/*LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		Log.d("values", "Latitude: " + latitude + " Longitude: "
				+ longitude);*/

		Date d = new Date();

		double humidity = SensorTagData.extractHumidity(
				characteristic);
		String humidity_value = Double.toString(humidity);
		Log.d("values", "humidity :" + humidity_value);
		
		String gyroscopeValue = SensorTagData.extractGyroscopeReading(characteristic, 0);
		Log.d("values", "gyro :" +gyroscopeValue);
		
		//String filename = "sensorData";
		String string = "\n"  + d.toString() + "\t" +String.valueOf(values[0]) + "\t"
				+ String.valueOf(values[1]) + "\t" + String.valueOf(values[2])+ "\t"+humidity_value + "\t" +gyroscopeValue ;

		SaveData(string);

	}
	
	
	
	
	
	

	private void SaveData(String string) {
		File sdCard = Environment.getExternalStorageDirectory();
		File directory = new File(sdCard.getAbsolutePath() + "/Data");
		if (!directory.exists())
			directory.mkdirs();
		String fname = "sensortag.txt";
		File file = new File(directory, fname);

		try {
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(string.getBytes());
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
