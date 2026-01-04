package com.example.beautyapp.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.example.beautyapp.R;
import com.example.beautyapp.ui.base.BaseActivity;

public class EmployeeManageScheduleActivity extends BaseActivity {
    private CardView cardPending, cardConfirmed, cardAll;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_manage_schedule);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.manage_schedule));
        }
        
        cardPending = findViewById(R.id.cardPending);
        cardConfirmed = findViewById(R.id.cardConfirmed);
        cardAll = findViewById(R.id.cardAll);
        
        cardPending.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeePendingAppointmentsActivity.class);
            startActivity(intent);
        });
        
        cardConfirmed.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeeConfirmedAppointmentsActivity.class);
            startActivity(intent);
        });
        
        cardAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeeAllAppointmentsActivity.class);
            startActivity(intent);
        });
    }
}


