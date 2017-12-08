package com.murach.tipcalculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.murach.tipcalculator.data.Database;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    // set up preferences
    private SharedPreferences prefs;

    float billAmount;
    float avg;
    DecimalFormat df = new DecimalFormat(".00");

    Database database;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        database = new Database(this);

        database.open();

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        
        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = prefs.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();

        if(database.getCount() == 0) {
            database.addTip(new Tip(0, 0, 40.60f, .15f));
            database.addTip(new Tip(0, 0, 40.60f, .15f));
            Log.d("null", "add new row");
        }

        ArrayList<Tip> objects = database.getTips();

        for(Tip tip : objects) {
            String logAll = "ID: " + tip.getId() +
                    " | Date: " + String.valueOf(tip.getDateMillis()) +
                    " | Bill Amount: " + tip.getBillAmount() +
                    " | Tip Percent: " + tip.getTipPercent();
            Log.d("TipCalculator:getTips:", logAll);
        }

        if(database != null) {
            Cursor c = database.getTip();
            c.moveToLast();

            String logLast = "ID: " + c.getString(0) +
                    " | Date: " + c.getString(1) +
                    " | Bill Amount: " + c.getString(2) +
                    " | Tip Percent: " + c.getString(3);

            Log.d("TipCalculator:getTip:", logLast);

            avg = database.getAverage();

            String logAverage = "Tip Percent AVG: " + df.format(avg);

            Log.d("TipCalculator: : ", logAverage);

            billAmountEditText.setText("");

            tipPercent = avg;
            int avgDisplay = (int)(avg * 100);

            percentTextView.setText(avgDisplay + "%");
        }
    }
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            tipPercent = tipPercent - .01f;
            calculateAndDisplay();
            break;
        case R.id.percentUpButton:
            tipPercent = tipPercent + .01f;
            calculateAndDisplay();
            break;
        }
    }

    public void SaveTipCalculation(View view) {

        if(!billAmountEditText.getText().toString().equals("")){

            Tip object = new Tip();
            object.getDateMillis();
            object.getDateStringFormatted();

            Tip tip = new Tip(1, object.getDateMillis(), billAmount, tipPercent);

            database.addTip(tip);

            billAmountEditText.setText("");

            tipPercent = avg;
            int avgDisplay = (int)(avg * 100);

            percentTextView.setText(avgDisplay + "%");
        } else {
            billAmount = 0;
        }
    }

    public void deleteAll(View view) {

        database.deleteAllTips();

        billAmountEditText.setText("");
        tipTextView.setText("$0.00");
        totalTextView.setText("$0.00");

        tipPercent = .15f;
        int avgDisplay = (int)(tipPercent * 100);

        percentTextView.setText(avgDisplay + "%");

    }
}