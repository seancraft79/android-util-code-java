package com.sean.lib_code_java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@SuppressWarnings({"MissingPermission", "deprecation"})
public abstract class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    final String SECURETYPE_WEP = "WEP";
    final String SECURETYPE_WPA = "WPA";

    private ConnectivityManager connManager;
    private NetworkInfo mNetworkInfo;
    WifiManager wifiManager;

    public WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = getConnectivityManager(context);
        if(cm != null) return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return null;
    }

    private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                doWithScanResult();
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                context.sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    protected abstract void doWithScanResult();

    public void initWifiScan(Context context) {
        try {

            wifiManager = getWifiManager(context);
            connManager = getConnectivityManager(context);
            mNetworkInfo = getNetworkInfo(context);

            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            wifiManager.startScan();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void clearWifiList() throws Exception {
        try {
            int networkId = wifiManager.getConnectionInfo().getNetworkId();
            wifiManager.removeNetwork(networkId);
            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.disableNetwork(networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            if (list.size() > 0) {
                for (WifiConfiguration wc : list) {
                    wifiManager.removeNetwork(wc.networkId);
                    wifiManager.saveConfiguration();
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void wifiClearRescan() throws Exception {
        clearWifiList();
        wifiManager.startScan();
    }

    public void disconnect() throws Exception {

        try {
            NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netInfo.isConnected()) {
                wifiManager.disconnect();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public boolean connect(String networkSSID, String networkPass) {

        Log.d(TAG, "=== START CONNECT SSID : " + networkSSID + ", pass : " + networkPass);

        String cssid = getConnectedSSID();
        if (!isNullOrEmpty(cssid)) {

            if (cssid.equals(networkSSID)) {
                Log.d(TAG, networkSSID + " is already connected");
                return true;
            }

        } else {
            Log.e(TAG, "=== CONNECTED SSID Empty");
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);

        conf.status = WifiConfiguration.Status.ENABLED;
        conf.priority = 40;

        String secureType = getSecureType(networkSSID);
        Log.d(TAG, "SecureType : " + secureType);

        if (secureType.equals(SECURETYPE_WEP)) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            if (!com.sean.lib_code_java.StringUtil.isNullOrEmpty(networkPass)) {
                Log.d(TAG, "SetPW : " + networkPass);
                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }
            }

            conf.wepTxKeyIndex = 0;

        } else if (secureType.equals(SECURETYPE_WPA)) {
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            if (!com.sean.lib_code_java.StringUtil.isNullOrEmpty(networkPass)) {
                Log.d(TAG, "SetPW : " + networkPass);
                conf.preSharedKey = "\"" + networkPass + "\"";
            }

        } else {
            Log.d(TAG, "Configuring OPEN network");

            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedAuthAlgorithms.clear();
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }

        // add this network if device does not have it ?
        int networkId = wifiManager.addNetwork(conf);

        Log.d(TAG, "Added networkId : " + networkId);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                Log.d(TAG, "WifiConfiguration SSID " + i.SSID);

                boolean isDisconnected = wifiManager.disconnect();
                Log.d(TAG, "isDisconnected : " + isDisconnected);

                boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                Log.d(TAG, "isEnabled : " + isEnabled);

                boolean isReconnected = wifiManager.reconnect();
                Log.d(TAG, "isReconnected : " + isReconnected);
                if (isReconnected) {
                    return true;
                }

                break;
            }
        }

        return false;
    }

    public int getExistingNetworkId(String SSID) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (SSID.equalsIgnoreCase(existingConfig.SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    public String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !isBlank(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }

        return ssid;
    }

    public boolean isWifiConnected(Context context) {

        NetworkInfo activeNetwork = getNetworkInfo(context);

        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isWifiEnabled(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public String intToInetAddress() {
        int hostAddress = wifiManager.getDhcpInfo().serverAddress;
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes).toString();
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    public boolean isConnected(String ssid) {
        String cssid = getConnectedSSID();
        if (!com.sean.lib_code_java.StringUtil.isNullOrEmpty(cssid)) {

            if (cssid.equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    String getConnectedSSID() {
        WifiInfo info = wifiManager.getConnectionInfo();
        String cssid = info.getSSID();
        if (!com.sean.lib_code_java.StringUtil.isNullOrEmpty(cssid)) {
            cssid = cssid.replace("\"", "");
            return cssid;
        }
        return "";
    }

    public int getIpAddress() {
        return wifiManager.getConnectionInfo().getIpAddress();
    }

    public int getIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getIpAddress();
    }

    String getSecureType(String networkSSID) {
        List<ScanResult> scanResult = wifiManager.getScanResults();
        for (int i = 0; i < scanResult.size(); i++) {
            if (scanResult.get(i).SSID.equals(networkSSID)) {
                if (scanResult.get(i).capabilities.toUpperCase().contains(SECURETYPE_WEP)) {
                    return SECURETYPE_WEP;
                } else if (scanResult.get(i).capabilities.toUpperCase().contains(SECURETYPE_WPA)) {
                    return SECURETYPE_WPA;
                }
            }
        }
        return "";
    }

    // 신호강도
    public int getWifiRssi(Context context) {
        if(wifiManager == null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getRssi();
    }

    // numberOfLevels : 신호강도표시 단계
    public int getWifiSignalLevel(Context context, int numberOfLevels) {
        int rssi = getWifiRssi(context);
        int level = WifiManager.calculateSignalLevel(rssi, numberOfLevels);
        return level;
    }

    /** String util functions **/
    static boolean isNullOrEmpty(String str){

        if(str == null || str.isEmpty() || str.length() < 1)
            return true;

        str = removeSpace(str);
        if(str.isEmpty() || str.length() < 1)
            return true;

        return false;
    }

    static String removeSpace(String s) {

        String withoutspaces = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ')
                withoutspaces += s.charAt(i);
        }
        return withoutspaces;
    }

    static boolean isBlank(String string) {
        if (string == null || string.length() == 0)
            return true;

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i)))
                return false;
        }
        return true;
    }

    static boolean isWhitespace(int c){
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }
}
