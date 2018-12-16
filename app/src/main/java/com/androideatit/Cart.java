package com.androideatit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androideatit.Common.Common;
import com.androideatit.Database.Database;
import com.androideatit.Model.Order;
import com.androideatit.Model.Request;
import com.androideatit.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests =  database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new Request
                showAlertDialog();
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Thông báo");
        alertDialog.setMessage("Đặt hàng thành công !");
//        final EditText edtAddress = new EditText(Cart.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//        );
//
//        edtAddress.setLayoutParams(lp);
//        alertDialog.setView(edtAddress);
//        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Ok, cảm ơn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        Common.currentUser.getAddress(),
//                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );

                //Submit to Firebase
                //We will using System.Current
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                //Delete the cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Cảm ơn bạn đã sử dụng ShopingApp DK", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(Cart.this, Home.class);
                startActivity(homeIntent);
                finish();

//                This code is to show notifications in android status bar when user places order
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(Cart.this,0,intent,0);
                Notification.Builder notificationBuilder = new Notification.Builder(Cart.this)
                        .setTicker(getString(R.string.orderPlacedStr)).setContentTitle("Order Placed")
                        .setContentText("Your order is processing now").setSmallIcon(R.drawable.logo)
                        .setContentIntent(pendingIntent);
                Notification notification = notificationBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert nm != null;
                nm.notify(0,notification);
            }
        });

        /*alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/

        alertDialog.show();

    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuanlity()));
        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }



}
