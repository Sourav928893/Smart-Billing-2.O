package c.s.smartbilling2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import c.s.smartbilling2o.model.Invoice;

public class RecentBillsAdapter extends RecyclerView.Adapter<RecentBillsAdapter.ViewHolder> {

    private List<Invoice> invoiceList;

    public RecentBillsAdapter(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        holder.tvCustomerName.setText(invoice.getCustomerName());
        holder.tvBillDate.setText(invoice.getDate());
        holder.tvTotalAmount.setText(String.format("₹%.2f", invoice.getTotalAmount()));
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), InvoiceDetails.class);
            Bundle bundle = new Bundle();
            bundle.putString("InvoiceNo", String.valueOf(invoice.getId()));
            bundle.putString("Username", invoice.getCustomerName()); // Existing InvoiceDetails expects Username
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvBillDate, tvTotalAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBillDate = itemView.findViewById(R.id.tvBillDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}
