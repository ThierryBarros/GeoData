package lacina.geodata.logic;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by tales on 14/03/18.
 */

public class DummyWriter {


    public static void write(String data) throws IOException {
        //String filename = "/data/user/location_data.txt";
        //"//sdcard//Download//"
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/location_data.txt");
        FileWriter writer = new FileWriter(file, true);
        writer.write(data + "\n");
        writer.close();

    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("/data/data/GeoDataActivity/geodata.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
