package com.lizhivscaomei.myapp.module.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lizhivscaomei.myapp.MyApplication;
import com.lizhivscaomei.myapp.R;
import com.lizhivscaomei.myapp.module.entity.LedgerEntity;

import org.apache.commons.lang3.StringUtils;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AddLegerActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private EditText totalAmount,date,totalWeight;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_leger);
        // Set up the login form.
        date = (EditText) findViewById(R.id.date);
        //默认当天日期
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        date.setText(sf.format(new Date()));
        totalWeight = (EditText) findViewById(R.id.total_weight);
        totalWeight.requestFocus();
        totalAmount = (EditText) findViewById(R.id.total_amount);

        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSaveDb();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(date, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSaveDb() {

        // Reset errors.
        date.setError(null);
        totalAmount.setError(null);

        // Store values at the time of the login attempt.
        final LedgerEntity ledgerEntity=new LedgerEntity();
        if(StringUtils.isNotEmpty(totalWeight.getText().toString())){
            ledgerEntity.setTotalWeight(Float.parseFloat(totalWeight.getText().toString()));
        }
        if(StringUtils.isNotEmpty(totalAmount.getText().toString())){

            ledgerEntity.setTotalAmount(Float.parseFloat(totalAmount.getText().toString()));
        }
        ledgerEntity.setDate(date.getText().toString());

        boolean cancel = true;
        View focusView = null;

        //校验空
        if (ledgerEntity!=null&& StringUtils.isNotEmpty(ledgerEntity.getDate())) {
            totalAmount.setError(getString(R.string.error_field_required));
            if(ledgerEntity.getTotalAmount()>0){
                if(ledgerEntity.getTotalWeight()>0){
                    cancel=false;
                }else {
                    totalWeight.setError(getString(R.string.error_field_required));
                    focusView = totalWeight;
                }
            }else {
                totalAmount.setError(getString(R.string.error_field_required));
                focusView = totalAmount;
            }
        }else{
            date.setError(getString(R.string.error_field_required));
            focusView = date;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //异步保存，写入数据库
           new AsyncTask<LedgerEntity, Boolean, Boolean>() {

               @Override
               protected Boolean doInBackground(LedgerEntity... ledgerEntities) {
                   try {
                       MyApplication.getXDbManager().save(ledgerEntity);
                   } catch (DbException e) {
                       return false;
                   }
                   return true;
               }

               @Override
               protected void onPostExecute(Boolean aBoolean) {
                   super.onPostExecute(aBoolean);
                   if(aBoolean){

                       Toast.makeText(AddLegerActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
//                       startActivity(new Intent(AddLegerActivity.this,LedgerMainActivity.class));
                       AddLegerActivity.this.finish();
                   }else {

                       Toast.makeText(AddLegerActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                   }
                   showProgress(false);
               }
           }.execute(ledgerEntity);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

