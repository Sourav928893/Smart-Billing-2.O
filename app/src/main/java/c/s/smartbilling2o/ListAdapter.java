package c.s.smartbilling2o;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<POJOInvoices> implements View.OnClickListener {

    private ArrayList<POJOInvoices> dataSet;
    Context mContext;
    private static class ViewHolder {
        TextView txtInvoiceNo;
        TextView txtInvoiceDate;
        TextView txtUsername;
    }

    public ListAdapter(ArrayList<POJOInvoices> data, Context context) {
        super(context, R.layout.total_sale_list, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        POJOInvoices dataModel = getItem(position);

        if (dataModel != null) {
            Intent intent = new Intent(mContext, InvoiceDetails.class);
            intent.putExtra("InvoiceNo", dataModel.getInvoiceNo());
            intent.putExtra("Username", dataModel.getUsername());
            mContext.startActivity(intent);
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        POJOInvoices dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.total_sale_list, parent, false);
            viewHolder.txtInvoiceNo = (TextView) convertView.findViewById(R.id.listInvoiceNo);
            viewHolder.txtInvoiceDate = (TextView) convertView.findViewById(R.id.listDate);
            viewHolder.txtUsername = (TextView) convertView.findViewById(R.id.listUsername);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.txtInvoiceNo.setText(dataModel.getInvoiceNo());
        viewHolder.txtInvoiceDate.setText(dataModel.getDate());
        viewHolder.txtUsername.setText(dataModel.getUsername());

        // Set listener for the list item
        convertView.setOnClickListener(this);
        convertView.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
