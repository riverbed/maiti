package com.maiti.testapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.maiti.testapp.MAITITestApplication;
import com.maiti.testapp.R;
import com.riverbed.mobile.android.apmlib.UserExperience;
import com.riverbed.mobile.android.apmlib.objects.SettingsObject;
import com.riverbed.mobile.android.apmlib.objects.TransactionId;

import java.util.Stack;

public class MainActivity extends FragmentActivity {

    private UserExperience userExperience = null;
    private SettingsObject settingsObject = null;

    private Handler handler = new Handler();

    private Stack<TransactionId> openedTransactions = new Stack<TransactionId>();


    private Button endCurrentTransactionBtn;
    private Button endRootTransactionBtn;
    private Button addEventToCurrentBtn;
    private Button addEventToRootBtn;
    private Button addErrorToCurrentTransactionBtn;
    private Button addTag1ToCurrentTransactionBtn;
    private Button addTag2ToCurrentTransactionBtn;
    private Button addTag3ToCurrentTransactionBtn;
    private Button addUserDataToCurrentTransactionBtn;

    private int notificationCounter = 0;
    private int transactionCounter = 0;
    private int eventCounter = 0;
    private int errorCounter = 0;
    private int tag1Counter = 0;
    private int tag2Counter = 0;
    private int tag3Counter = 0;
    private int userDataCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userExperience = MAITITestApplication.getUserExperience(getApplication());
        settingsObject = MAITITestApplication.getMAITILibSettings(getApplication());

        addEventToCurrentBtn = (Button) findViewById(R.id.add_event);
        addEventToRootBtn = (Button) findViewById(R.id.add_event_to_root);
        addErrorToCurrentTransactionBtn = (Button) findViewById(R.id.add_error_message);
        addTag1ToCurrentTransactionBtn = (Button) findViewById(R.id.add_tag1);
        addTag2ToCurrentTransactionBtn = (Button) findViewById(R.id.add_tag2);
        addTag3ToCurrentTransactionBtn = (Button) findViewById(R.id.add_tag3);
        addUserDataToCurrentTransactionBtn = (Button) findViewById(R.id.add_user_data);

        endCurrentTransactionBtn = (Button) findViewById(R.id.end_transaction);
        endRootTransactionBtn = (Button) findViewById(R.id.end_root_transaction);

        CheckBox switchMAITIModeBtn = (CheckBox) findViewById(R.id.enable_lib);
        switchMAITIModeBtn.setChecked(true);
        switchMAITIModeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userExperience.setEnabled(isChecked);
            }
        });


        CheckBox recordConnectionInfoBtn = (CheckBox) findViewById(R.id.recordConnectionInfo);
        recordConnectionInfoBtn.setChecked(true);
        recordConnectionInfoBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsObject.setRecordConn(isChecked);
            }
        });

        CheckBox recordMemoryBtn = (CheckBox) findViewById(R.id.recordMemory);
        recordMemoryBtn.setChecked(true);
        recordMemoryBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsObject.setRecordMemory(isChecked);
            }
        });

        CheckBox recordSerialBtn = (CheckBox) findViewById(R.id.recordSerial);
        recordSerialBtn.setChecked(true);
        recordSerialBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsObject.setRecordSerial(isChecked);
            }
        });

        refreshButtonsState();
    }




    public void onSendSimpleTransaction(View view) {
        final TransactionId simpleTransaction = userExperience.transactionStart("Simple Transaction");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userExperience.transactionEnd(simpleTransaction);
            }
        }, 4000);
    }

    public void onSendNotification(View view) {
        userExperience.notification("NotificationTransaction_" + ++notificationCounter, "NotificationTransaction Error", "NotificationTransaction UserTag1", "NotificationTransaction UserTag2", "NotificationTransaction UserTag3", "NotificationTransaction UserData");
    }

    public void onStartTransaction(View view) {
        TransactionId currentRunningTransaction = openedTransactions.size() > 0 ? openedTransactions.peek() : null;
        TransactionId newTransactionId = userExperience.transactionStart("Transaction_" + ++transactionCounter, currentRunningTransaction);
        if (newTransactionId == null) {
            Toast.makeText(this, "Error starting Transaction", Toast.LENGTH_SHORT).show();
        }

        openedTransactions.push(newTransactionId);
        refreshButtonsState();
    }

    public void onEndTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.pop();
            userExperience.transactionEnd(currentRunningTransaction);
            refreshButtonsState();
        }
    }

    public void onEndRootTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId rootRunningTransaction = openedTransactions.firstElement();
            userExperience.transactionEnd(rootRunningTransaction);
            openedTransactions.remove(rootRunningTransaction);
            refreshButtonsState();
        }
    }

    public void onAddEventToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();
            userExperience.setTransactionEvent("Event_" + eventCounter++, currentRunningTransaction);
            refreshButtonsState();
        }
    }

    public void onAddEventToRootTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId rootTransaction = openedTransactions.firstElement();
            userExperience.setTransactionEvent("Event_" + ++eventCounter, rootTransaction);
            refreshButtonsState();
        }
    }

    public void onAddErrorMessageToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();
            userExperience.setTransactionEvent("ErrorMessage_" + ++errorCounter, currentRunningTransaction);
            refreshButtonsState();
        }
    }

    public void onAddTag1ToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();
            userExperience.setTransactionUserTag1(currentRunningTransaction, generateString("Tag1_" + ++tag1Counter, 128));
            refreshButtonsState();
        }
    }

    public void onAddTag2ToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();
            userExperience.setTransactionUserTag2(currentRunningTransaction, generateString("Tag2_" + ++tag2Counter, 128));
            refreshButtonsState();
        }
    }

    public void onAddTag3ToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();
            userExperience.setTransactionUserTag3(currentRunningTransaction, generateString("Tag3_" + ++tag3Counter, 128));
            refreshButtonsState();
        }
    }

    private String generateString(String initialString, int length) {
        StringBuilder builder = new StringBuilder(initialString);
        builder.append(" ");
        for (int i = 0; i < length - builder.length(); i++) {
            builder.append("A");
        }
        return builder.toString();
    }


    public void onAddUserDataToTransaction(View view) {
        if (openedTransactions.size() > 0) {
            TransactionId currentRunningTransaction = openedTransactions.peek();

            userExperience.setTransactionUserData(currentRunningTransaction, generateString("UserData_" + String.valueOf(++userDataCounter), 16000));
            refreshButtonsState();
        }
    }

    public void onCrash(View view) throws Exception {
        throw new Exception("Testing App Crash Exception");
    }


    private void refreshButtonsState() {
        boolean noOpenedTransactionsLeft = openedTransactions.isEmpty();
        endCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        endRootTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        addEventToCurrentBtn.setEnabled(!noOpenedTransactionsLeft);
        addEventToRootBtn.setEnabled(!noOpenedTransactionsLeft);
        addErrorToCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        addTag1ToCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        addTag2ToCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        addTag3ToCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
        addUserDataToCurrentTransactionBtn.setEnabled(!noOpenedTransactionsLeft);
    }
}
