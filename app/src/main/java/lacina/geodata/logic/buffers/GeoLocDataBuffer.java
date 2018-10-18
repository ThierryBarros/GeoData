package lacina.geodata.logic.buffers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeoLocDataBuffer {

    private static GeoLocDataBuffer instance = null;
    private List<Map<String, String>> dataBuffer;

    public GeoLocDataBuffer(){

        dataBuffer = new ArrayList<Map<String, String>>();
    }

    public static GeoLocDataBuffer getInstance() {
        if(instance == null) {
            instance = new GeoLocDataBuffer();
        }
        return instance;
    }

    public void addDataPoint(Map<String, String> dataPoint){
        dataBuffer.add(dataPoint);
    }

    public List<Map<String, String>> getDataBuffer(){
        return dataBuffer;
    }

    public void setDataBuffer(List<Map<String, String>> dataBuffer){
        this.dataBuffer = dataBuffer;
    }


}
