package lacina.geodata.logic;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import lacina.geodata.utils.UtilColors;

public class TicketRow extends TableRow {

    private int reservedSlots;
    private int capacity;
    private int usedSlots;
    private Context context;

    public TicketRow (Context context, int capacity){
        super(context);
        this.context = context;
        this.reservedSlots = capacity;
        this.capacity = capacity;
        this.usedSlots = 0;
    }

    public void reserveTicketSlot(){
        if (getReservedSlots() > 0){
            TextView emptyTicket = new TextView(context);
            addView(emptyTicket);
            buildTicketShape(emptyTicket);

            reservedSlots--;
        }
    }

    public void buildTicketShape(TextView emptyTicket){
        TicketRow.LayoutParams params = (TicketRow.LayoutParams) emptyTicket.getLayoutParams();
        params.height = TableLayout.LayoutParams.WRAP_CONTENT;
        params.width = 0;
        params.leftMargin = 8;
        params.rightMargin = 8;
        params.topMargin = 4;
        params.weight = new Float(0.25);
        emptyTicket.setBackgroundColor(UtilColors.DARK_BLUE);
        emptyTicket.setTextColor(UtilColors.WHITE);
        emptyTicket.setGravity(Gravity.CENTER);
        emptyTicket.setLayoutParams(params);
        emptyTicket.setVisibility(View.INVISIBLE);
    }

    //counts blank tickets
    private int getReservedSlots(){
        return reservedSlots;
    }

    //counts fileld tickets
    public boolean isThereAnyAvailabelSlot(){
        if (usedSlots < capacity){
            return true;
        }
        return false;
    }

    public void makeTicket(Long ticketNumber) {
        TextView ticket = (TextView) getChildAt(usedSlots);
        ticket.setText("#" + ticketNumber);
        ticket.setVisibility(View.VISIBLE);
        usedSlots++;
    }

    public int getUsedSlots(){
        return usedSlots;
    }
}
