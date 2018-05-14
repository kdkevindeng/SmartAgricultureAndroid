package com.lizhivscaomei.myapp.module.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lizhivscaomei.myapp.MainActivity;
import com.lizhivscaomei.myapp.MyApplication;
import com.lizhivscaomei.myapp.R;
import com.lizhivscaomei.myapp.module.entity.LedgerEntity;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerMainActivity extends AppCompatActivity {
    ListView ledgerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //记账按钮
        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.fab);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LedgerMainActivity.this, AddLegerActivity.class));
            }
        });
        //ledgerList
        ledgerList = (ListView) findViewById(R.id.ledgerList);
        ledgerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final Map<String, Object> selecedItem = (Map<String, Object>) parent.getItemAtPosition(position);
                new AlertDialog.Builder(LedgerMainActivity.this).setTitle("提示").setMessage(selecedItem.get("text1").toString() + "，确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<LedgerEntity, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(LedgerEntity... ledgerEntities) {
                                try {
                                    MyApplication.getXDbManager().delete(ledgerEntities[0]);
                                } catch (DbException e) {
                                    return false;
                                }
                                return true;
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                                if(aBoolean){
                                    Toast.makeText(LedgerMainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    queryList();
                                }else {

                                    Toast.makeText(LedgerMainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute((LedgerEntity) selecedItem.get("entity"));
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryList();
    }

    public void queryList(){

        new AsyncTask<Void, Void, List<LedgerEntity>>() {
            @Override
            protected List<LedgerEntity> doInBackground(Void... voids) {
                try {
                    return MyApplication.getXDbManager().selector(LedgerEntity.class).orderBy("date",true).findAll();
                } catch (DbException e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<LedgerEntity> ledgerEntityList) {
                super.onPostExecute(ledgerEntityList);
                List<Map<String, Object>> dataList = new ArrayList<>();
                if(ledgerEntityList!=null){
                    for (LedgerEntity entity : ledgerEntityList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("entity", entity);
                        map.put("text1", "总金额：" + String.format("%.2f", entity.getTotalAmount()));
                        map.put("text2", entity.getDate());
                        map.put("text3", "总重量：" + String.format("%.2f", entity.getTotalWeight()) + "斤");
                        map.put("text4", "单价：" + String.format("%.2f", (entity.getTotalAmount() / entity.getTotalWeight())) + "元/斤");
                        dataList.add(map);
                    }
                }
                ledgerList.setAdapter(new SimpleAdapter(LedgerMainActivity.this, dataList, R.layout.item_horizontal_text3_line2, new String[]{"text1", "text2", "text3", "text4"}, new int[]{R.id.textView1, R.id.textView2, R.id.textView3,R.id.textView4}));

            }
        }.execute();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
