package lacina.geodata.logic;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lacina.geodata.fragments.RaffleTabController;
import lacina.geodata.logic.buffers.GeoLocDataBuffer;

/**
 * Created by tales on 11/03/18.
 */

public class ServerCommunication {

    private static String serverUrl = "http://192.168.0.107:5000";

    public static void sendData(RequestQueue queue, final Map<String, String> dataPoint) {
        // the data is placed on a buffer, then this buffer is consumed at the following method
        GeoLocDataBuffer.getInstance().addDataPoint(dataPoint);
        sendBufferedGeoData(queue);

    }

    /**
     * Method to send all geodata points from the buffer.
     * @param queue
     */
    public static void sendBufferedGeoData(RequestQueue queue) {
        String dataUrl = serverUrl + "/geo_data";

        for (final Map<String, String> dataPoint : GeoLocDataBuffer.getInstance().getDataBuffer()) {

            StringRequest postRequest = new StringRequest(Request.Method.POST, dataUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("FLOW Response", response);
                            GeoLocDataBuffer.getInstance().getDataBuffer().remove(dataPoint);
                            Log.d("FLOWBUFFER ", String.valueOf(GeoLocDataBuffer.getInstance().getDataBuffer().size()));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("FLOW Error Send", error.toString());

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    for (String key : dataPoint.keySet()) {
                        params.put(key, dataPoint.get(key));
                    }

                    return params;
                }
            };

            queue.add(postRequest);

        }


    }

    /**
     * Method to send profile data.
     * Its relevance its to have a backup for profiles data.
     * So the user can retrieve its profile after the app uninstalling.
     * @param queue
     */
    public static void checkTickets(RequestQueue queue) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://192.168.0.107:5000/profile_game_points",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject responseJson;
                        try {
                            responseJson = new JSONObject(response);  //your response
                            String result = responseJson.getString("result");    //result is key for which you need to retrieve data

                            Double restPoints = Double.valueOf(new JSONObject(result).getString("rest_points"));
                            JSONArray tickets = new JSONObject(result).getJSONArray("tickets");

                            Profile.getInstance().setCurrentTickets(toTicketList(tickets));
                            Log.d("FLOWTicketsid", String.valueOf(Profile.getInstance()));
                            Log.d("FLOWTickets", Profile.getInstance().getCurrentTickets().toString());


                            RaffleTabController.getInstance().nextRaffle();


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }





                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("FLOW Error Send", error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("points", Profile.getInstance().getPoints().toString());
                params.put("device_id", Profile.getInstance().getId());
                params.put("current_tickets", Profile.getInstance().getCurrentTickets().toString());
                Long timestamp = System.currentTimeMillis()/1000;
                params.put("timestamp", timestamp.toString());

                return params;
            }
        };

        queue.add(postRequest);

    }


    public static void raffles(RequestQueue queue) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.0.107:5000/raffles",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        RaffleTabController.getInstance().setRaffles(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FLOW Error", error.toString());
            }
        });

        queue.add(stringRequest);

    }

    private static List<Long> toTicketList(JSONArray jsonArray) {
        List<Long> tickets = new ArrayList<Long>();

        for (int i = 0; i < jsonArray.length(); i++) {
                Object ticket = jsonArray.opt(i);
                tickets.add(Long.valueOf(ticket.toString()));
        }
        return tickets;
    }



}

