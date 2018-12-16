package com.androideatit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androideatit.Common.Common;
import com.androideatit.Model.Request;
import com.androideatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText("Mã phiếu: " + adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText("Trạng thái: " + convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText("SĐT nhận hàng: " + model.getPhone());
                viewHolder.txtOrderAddress.setText("Giao hàng đến: " + model.getAddress());

            }
        };

        recyclerView.setAdapter(adapter);

    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Đã xác nhận";
        else if(status.equals("1"))
            return "Đang giao hàng";
        else
            return "Đã giao hàng";
    }
}
