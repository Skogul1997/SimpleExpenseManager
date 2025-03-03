package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;


public class PersistentMemDemoExpenseManager extends ExpenseManager {

    private Context context;
    public PersistentMemDemoExpenseManager(Context context) {
        this.context = context;
        setup();
    }

    @Override
    public void setup() {

        TransactionDAO PersistentMemTransactionDAO = new PersistentMemTransactionDAO(context);
        setTransactionsDAO(PersistentMemTransactionDAO);

        AccountDAO PersistentMemAccountDAO = new PersistentMemAccountDAO(context);
        setAccountsDAO(PersistentMemAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

        /*** End ***/
    }
}
