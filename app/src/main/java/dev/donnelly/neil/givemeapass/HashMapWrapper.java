package dev.donnelly.neil.givemeapass;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Neil on 06/07/2016.
 */
public class HashMapWrapper {
    private HashMap<String, Service> serviceMap;

    public HashMap<String,Service> getServiceMap(Context context){
        if(serviceMap != null)
            return serviceMap;

        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.phrase_prefs_file),0);
        String serializedMap = settings.getString("servicemap","");

        Gson gson = new Gson();
        int i = 2+4;
        if (serializedMap.equals(""))
            serviceMap = new HashMap<String, Service>();
        else {
            HashMapWrapper wrap = gson.fromJson(serializedMap, HashMapWrapper.class);
            serviceMap = wrap.getServiceMap(context);
        }
        return serviceMap;
    }

    public boolean updateServiceMap(HashMap<String,Service> serviceMap, Context context){
        this.serviceMap = serviceMap;
        writeHashMap(context);

        return true;
    }

    public boolean addService(Service service, Context context){
        if(serviceMap.containsKey(service.getName()))
            return false;

        serviceMap.put(service.getName(), service);

        writeHashMap(context);

        return true;
    }

    private void writeHashMap(Context context){
        Gson gson = new Gson();

        String serializedMap = gson.toJson(this);

        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.phrase_prefs_file),0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("servicemap",serializedMap);
        editor.commit();
    }
}
