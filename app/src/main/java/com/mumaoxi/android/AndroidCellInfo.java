package com.mumaoxi.android;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saxer on 12/21/15.
 */
public class AndroidCellInfo {
    private static final String TAG = AndroidCellInfo.class.getSimpleName();

    static class CellIDInfo {

        public int cellId;
        public String mobileCountryCode;
        public String mobileNetworkCode;
        public int locationAreaCode;
        public String radioType;

        public CellIDInfo() {
        }
    }

    /**
     * 获取基站信息
     *
     * @param context
     * @return
     */
    public static String getCellTowersInfo(Context context) {
        try {
            JSONArray array = new JSONArray();
            for (CellIDInfo info : getCellInfoArray(context)) {
                JSONObject object = new JSONObject();
                object.put("mcc", info.mobileCountryCode);
                object.put("mnc", info.mobileNetworkCode);
                object.put("lac", info.locationAreaCode);
                object.put("cell", info.cellId);
                array.put(object);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static ArrayList<CellIDInfo> getCellInfoArray(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        ArrayList<CellIDInfo> cellInfos = new ArrayList<>();
        CellIDInfo currentCell = new CellIDInfo();

        try {
            String radioType;
            int lac;
            int cid;

            if (manager.getCellLocation() instanceof GsmCellLocation) {
                GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
                lac = location.getLac();
                cid = location.getCid();
                radioType = "gsm";
            } else if (manager.getCellLocation() instanceof CdmaCellLocation) {
                CdmaCellLocation location = (CdmaCellLocation) manager.getCellLocation();
                lac = location.getNetworkId();
                cid = location.getBaseStationId();
                radioType = "cdma";
            } else {
                return cellInfos;
            }

            String mcc = (manager.getSimOperator() != null && manager
                    .getSimOperator().length() >= 3) ? manager
                    .getSimOperator().substring(0, 3) : "";
            String mnc = (manager.getSimOperator() != null && manager
                    .getSimOperator().length() >= 5) ? manager
                    .getSimOperator().substring(3, 5) : "";

            currentCell.cellId = cid;

            currentCell.mobileCountryCode = mcc;
            currentCell.mobileNetworkCode = mnc;
            currentCell.locationAreaCode = lac;

            currentCell.radioType = radioType;

            cellInfos.add(currentCell);

            // 获得邻近基站信息
            List<NeighboringCellInfo> list = manager
                    .getNeighboringCellInfo();
            int size = list != null ? list.size() : 0;
            for (int i = 0; i < size; i++) {

                CellIDInfo info = new CellIDInfo();
                info.cellId = list.get(i).getCid();
                info.mobileCountryCode = mcc;
                info.mobileNetworkCode = mnc;
                info.locationAreaCode = lac;

                cellInfos.add(info);
            }

        } catch (Exception e) {
            Log.e(TAG, "Get cell id info fail " + e.getMessage());
        }
        return cellInfos;
    }
}
