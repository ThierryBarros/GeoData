package lacina.geodata.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lacina.geodata.R;
import lacina.geodata.logic.Profile;
import lacina.geodata.logic.ServerCommunication;
import lacina.geodata.logic.TicketRow;
import lacina.geodata.utils.UtilColors;

/**
 * Class to manage RaffleTab views.
 * Created by tales on 02/04/18.
 */

public class RaffleTabController {

    private int ticketsPerRow = 4;
    private static RaffleTabController instance = null;
    private static Activity activity;
    private boolean isNextRaffleData = true;

    private Map<String, Map<String, String>> raffles;

    private String nextRaffleDate = "--/--/----";
    private String lastRaffleDate = "--/--/----";
    private String lastRaffleTicket = "-----";

    private TableLayout tableLayout;
    private ArrayList<Long> pastTickets;

    private RaffleTabController() {  }

    private RaffleTabController(Activity actv) {
        activity = actv;
    }

    public static RaffleTabController getInstance() {
        if(instance == null) {
            instance = new RaffleTabController();
        }
        return instance;
    }

    public static RaffleTabController getInstance(Activity activity) {
        if(instance == null) {
            instance = new RaffleTabController(activity);
        }
        return instance;
    }

    public void init(){
        ServerCommunication.raffles(Volley.newRequestQueue(activity));
        ServerCommunication.checkTickets(Volley.newRequestQueue(activity));
        //generateCurrentTickets();
        pastTickets = getPastTickets();
        setRaffleButtonsEvent();
        raffleScreen();
        nextRaffle();
        //makeNewTicket();

    }

    private void buildTicketTable(TableLayout tableLayout, List<Long> tickets){
        int nRows = (int) Math.ceil(tickets.size() / (float) ticketsPerRow);

        //build empty ticket rows to be then populated
        for (int i = 0; i < nRows; i++){
            appendRow(tableLayout);
        }

        //populate empty TextViews
        populateTicketTable(tableLayout, tickets);

    }

    public void makeNewTicket(){
//        Long ticketNumber = getTicketNumber();
        Long ticketNumber = Long.valueOf(123456);
        Profile.getInstance().addCurrentTicket(ticketNumber);
        TicketRow lastTicketRow = getLastTicketRow();

        if (lastTicketRow == null){
            lastTicketRow = createRow();
        }

        if (lastTicketRow.isThereAnyAvailabelSlot()){
            lastTicketRow.makeTicket(ticketNumber);
        }

    }

    private Long getTicketNumber(){
        return Long.valueOf(new Random().nextInt(999));

    }

    private TicketRow getLastTicketRow(){
        return (TicketRow) tableLayout.getChildAt(tableLayout.getChildCount() - 1);
    }

    private void populateTicketTable(TableLayout tableLayout, List<Long> tickets) {
        int ticketIndex = 0;

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);

            if (row instanceof TicketRow){
                TicketRow ticketRow = (TicketRow) row;

                for (int j = 0; j < ticketRow.getChildCount(); j++){

                    if (ticketIndex < tickets.size() && ticketRow.isThereAnyAvailabelSlot()){
                        ticketRow.makeTicket(tickets.get(ticketIndex));
                        ticketIndex++;
                    }
                }
            }
        }

    }

    private void appendRow(TableLayout tableLayout) {
        TicketRow row = createRow();
        tableLayout.addView(row);
    }

    /**
     * Create row and fill it with empty tickets
     */
    private TicketRow createRow(){
        TicketRow row = new TicketRow(activity, ticketsPerRow);

        for (int i = 0; i < ticketsPerRow; i++){
            row.reserveTicketSlot();
        }


        return row;
    }

    private void generateCurrentTickets(){
        List<Long> currentTickets = new ArrayList<Long>();

//        int ticketN;

//        for (int i = 0; i < 2; i++){
//            ticketN = new Random().nextInt(999);
//            currentTickets.add(Long.valueOf(ticketN));
//        }

//        Collections.sort(currentTickets);

        Profile.getInstance().setCurrentTickets(currentTickets);
    }

    private ArrayList<Long> getPastTickets(){
        ArrayList<Long> pastTickets = new ArrayList<Long>();
        int ticketN;

        for (int i = 0; i < 26; i++){
            ticketN = new Random().nextInt(999);
            pastTickets.add(Long.valueOf(ticketN));
        }

        Collections.sort(pastTickets);
        return pastTickets;
    }

    public static void killInstance() {
        instance = null;
    }


    public static boolean isRaffleTabAlive(){
        if (activity == null){
            return false;
        }else{
            return true;
        }
    }

    //asociate each button to build its respective layout
    private void setRaffleButtonsEvent(){
        Button buttonNextRaffle = activity.findViewById(R.id.button_next_raffle);
        buttonNextRaffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextRaffle();
            }
        });

        Button buttonLastRaffle = activity.findViewById(R.id.button_last_raffle);
        buttonLastRaffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pastRaffle();
            }
        });
    }

    /**
     * Updates the next raffle table layout with current tickets at Profile
     */
    public void nextRaffle(){
        tableLayout = (TableLayout) activity.findViewById(R.id.table_raffle_layout);
        tableLayout.removeAllViews();
        buildTicketTable(tableLayout, Profile.getInstance().getCurrentTickets());
        isNextRaffleData = true;
        instance.raffleScreen();
    }

    /**
     * Updates the past raffle table layout with past tickets at Profile
     */
    public void pastRaffle(){
        tableLayout = (TableLayout) activity.findViewById(R.id.table_raffle_layout);
        tableLayout.removeAllViews();
        buildTicketTable(tableLayout, pastTickets);
        isNextRaffleData = false;
        instance.raffleScreen();
    }

    //it places the buttons and labels for overall raffle information
    private void raffleScreen(){
        TextView textRaffleText = activity.findViewById(R.id.text_raffle_text);
        TextView textRaffleDate = activity.findViewById(R.id.text_raffle_date);
        TextView textLastTicketText = activity.findViewById(R.id.text_last_ticket_text);
        TextView textLastTicketNumber = activity.findViewById(R.id.text_last_ticket_number);

        //if user chooses to see next raffle info
        if (isNextRaffleData){
            textRaffleText.setText("Próximo Sorteio: ");
            textRaffleDate.setText(nextRaffleDate);
            textLastTicketText.setVisibility(View.INVISIBLE);
            textLastTicketNumber.setVisibility(View.INVISIBLE);

            Button buttonNextRaffle = activity.findViewById(R.id.button_next_raffle);
            buttonNextRaffle.setBackgroundColor(UtilColors.BLUE);
            buttonNextRaffle.setTextColor(Color.WHITE);

            Button buttonLastRaffle = activity.findViewById(R.id.button_last_raffle);
            buttonLastRaffle.setBackgroundColor(UtilColors.LTGRAY);
            buttonLastRaffle.setTextColor(Color.WHITE);
        }
        //if user chooses to see past raffle info
        else{
            textRaffleText.setText("Último Sorteio: ");
            tableLayout = (TableLayout) activity.findViewById(R.id.table_raffle_layout);

            textRaffleDate.setText(lastRaffleDate);
            textLastTicketText.setVisibility(View.VISIBLE);
            textLastTicketNumber.setText(lastRaffleTicket);
            textLastTicketNumber.setVisibility(View.VISIBLE);

            Button buttonNextRaffle = activity.findViewById(R.id.button_next_raffle);
            buttonNextRaffle.setBackgroundColor(UtilColors.LTGRAY);
            buttonNextRaffle.setTextColor(Color.WHITE);

            Button buttonLastRaffle = activity.findViewById(R.id.button_last_raffle);
            buttonLastRaffle.setBackgroundColor(UtilColors.BLUE);
            buttonLastRaffle.setTextColor(Color.WHITE);
        }
    }

    /**
     * Update label text data at raffle tab
     * @param rafflesJson
     */
    public void setRaffles(String rafflesJson){
        Gson gson = new Gson();
        Map<String, Map<String, String>> raffles = gson.fromJson(rafflesJson, HashMap.class);

        this.raffles = raffles;
        Log.d("FLOWRAFFLE", raffles.get("current_raffle").get("Date").toString());
        nextRaffleDate = raffles.get("current_raffle").get("Date");
        lastRaffleDate = raffles.get("last_raffle").get("Date");
        lastRaffleTicket = raffles.get("last_raffle").get("ticket");
    }

}

