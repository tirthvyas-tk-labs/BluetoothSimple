# Simple Bluetooth scanner

<img src="./Screenshots/BluetoothSimple.png" width="300">

## Procedure:

1. Declare permissions in `Manifest`.
2. Request `permissions` at runtime.
3. Get a handle to the default local `BluetoothAdapter`.
4. Start the remote device discovery process and register `BroadcastReceiver`.
5. Get Bluetooth devices from `BroadcastReceiver`.
6. Unregister `BroadcastReceiver`.