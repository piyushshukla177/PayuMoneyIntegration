package com.service.repeebag;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerifyActivity extends AppCompatActivity {

    ProgressBar progress;
    EditText bank_account_number,verify_account_number,bank_name_et,ifsc_code_et;

    Button btn_verify;

    String UPI_ID="BHARATPE.0852317505@icici";
    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    String TAG ="VerifyActivity", txnid ="txt12346", amount ="10", phone ="8806971598",
            prodname ="Loan", firstname ="Rupeeb Bag", email ="anmoljadhav3197@gmail.com",
            merchantId ="6851342", merchantkey="zttdO0aJ";  //   first test key only

    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        progress=findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        btn_verify=findViewById(R.id.btn_verify);
        bank_account_number=findViewById(R.id.bank_account_number);
        verify_account_number=findViewById(R.id.verify_account_number);
        bank_name_et=findViewById(R.id.bank_name_et);
        ifsc_code_et=findViewById(R.id.ifsc_code_et);
        btn_verify.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if(verify())
                    {
                        payUsingUpi("BHARATPE",UPI_ID,"Payment to RepeeBag",amount);
                    }
                    }
                }
        );
    }

    boolean verify()
    {
        boolean b=true;
        if(bank_account_number.getText().toString().isEmpty())
        {
            b=false;
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
            return b;
        }
        if(verify_account_number.getText().toString().isEmpty())
        {
            b=false;
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
            return b;
        }

        if(verify_account_number.getText().toString().length()<10)
        {
            b=false;
            Toast.makeText(this,"Enter Valid Bank Account Number",Toast.LENGTH_SHORT).show();
            return b;
        }

        if(bank_name_et.getText().toString().isEmpty())
        {
            b=false;
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
            return b;
        }
        if(ifsc_code_et.getText().toString().isEmpty())
        {
            b=false;
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
            return b;
        }
        if(!bank_account_number.getText().toString().equals(verify_account_number.getText().toString()))
        {
            b=false;
            Toast.makeText(this,"account Number did not match",Toast.LENGTH_SHORT).show();
           bank_account_number.requestFocus();
            return b;
        }
        return b;
    }

    public void startpay(){
        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                   // User Phone number
                .setProductName(prodname)                   // Product Name or description
                .setFirstName(firstname)                              // User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);
        try {
            paymentParam = builder.build();
            // generateHashFromServer(paymentParam );
            getHashkey();
        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }
    }

    void getHashkey() {
        progress.setVisibility(View.VISIBLE);
        String url = "https://mystore177.000webhostapp.com/payu/new_hash_r.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.setVisibility(View.GONE);
//                          JSONObject jsonResponse = new JSONObject(response);
                            String merchantHash = response.toString();
                            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                                paymentParam.setMerchantHash(merchantHash);
                                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, VerifyActivity.this, R.style.AppTheme_default, false);
//                                Toast.makeText(StartPaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "hash empty");
                            } else {
                                paymentParam.setMerchantHash(merchantHash);
                                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, VerifyActivity.this, R.style.AppTheme_default, false);
                            }
                        } catch (Exception ex) {
                            progress.setVisibility(View.GONE);
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
//                        Toast.makeText(VerifyActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", merchantkey);
                params.put("txnid", txnid);
                params.put("amount", amount);
                params.put("productinfo", prodname);
                params.put("firstname", firstname);
                params.put("email", email);
                return params;
            }
        };
        VolleySingleton.getInstance(VerifyActivity.this).addToRequestQueue(stringRequest);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//     // Result Code is -1 send from Payumoney activity
//        Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
//        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
//            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );
//
//            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
//
//                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
//                    //Success Transaction
//                    Intent intent=new Intent(VerifyActivity.this,FinalActivity.class);
//                    intent.putExtra("success","true");
//                    startActivity(intent);
//                } else{
//                    //Failure Transaction
//                }
//                // Response from Payumoney
//                String payuResponse = transactionResponse.getPayuResponse();
//
//                // Response from SURl and FURL
//                String merchantResponse = transactionResponse.getTransactionDetails();
//                Log.e(TAG, "tran "+payuResponse+"---"+ merchantResponse);
//            } /* else if (resultModel != null && resultModel.getError() != null) {
//                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
//            } else {
//                Log.d(TAG, "Both objects are null!");
//            }*/
//        }
//    }


    void payUsingUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(VerifyActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(VerifyActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(VerifyActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(VerifyActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(VerifyActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(VerifyActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
