package com.game.sdk.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.game.sdk.db.DBHelper;
import com.game.sdk.domain.AgentDbBean;
import com.game.sdk.log.L;


/**
 * Created by liu hong liang on 2016/9/29.
 */

public class AgentDbDao {
    public static final String AGENT_TABLE_NAME="tagent";


    private static AgentDbDao agentDbDao;
    private DBHelper dbHelper;
    private AgentDbDao(Context context){
        dbHelper = new DBHelper(context, null, 2);
    }
    public synchronized static AgentDbDao getInstance(Context context){
        if(agentDbDao==null){
            agentDbDao=new AgentDbDao(context);
        }
        return agentDbDao;
    }
    public void addOrUpdate(AgentDbBean agentDbBean){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("select * from " + AGENT_TABLE_NAME + " where packageName=?", new String[]{agentDbBean.getPackageName()});
        AgentDbBean oldAgentDbBean=null;
        while (cursor.moveToNext()){
            oldAgentDbBean=new AgentDbBean();
            oldAgentDbBean.setPackageName(cursor.getString(cursor.getColumnIndex(AgentDbBean.PACKAGE_NAME)));
            oldAgentDbBean.setInstallCode(cursor.getString(cursor.getColumnIndex(AgentDbBean.INSTALL_CODE)));
            if(oldAgentDbBean.getInstallCode()==null){
                oldAgentDbBean.setInstallCode("1_0");
            }
            oldAgentDbBean.setAgent(cursor.getString(cursor.getColumnIndex(AgentDbBean.AGENT)));
        }

        if(oldAgentDbBean!=null){
            L.d("AgentDbDao","更新老的agent-->"+oldAgentDbBean.toString());
            oldAgentDbBean.setAgent(agentDbBean.getAgent());
            oldAgentDbBean.setInstallCode(agentDbBean.getInstallCode());
            int update = writableDatabase.update(AGENT_TABLE_NAME, oldAgentDbBean.toContentValues(), AgentDbBean.PACKAGE_NAME + "=?", new String[]{oldAgentDbBean.getPackageName()});
            L.d("AgentDbDao","更新agent-->"+oldAgentDbBean.toString()+" count="+update);
        }else{
            long insert = writableDatabase.insert(AGENT_TABLE_NAME, null, agentDbBean.toContentValues());
            L.d("AgentDbDao","存入agent-->"+agentDbBean.toString()+" count="+insert);
        }
        cursor.close();
        writableDatabase.close();
    }

    /**
     * 更新为已经使用
     * @param packageName
     * @param installCode
     */
    public void useInstallCode(String packageName,String installCode){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AgentDbBean.INSTALL_CODE,installCode);
        int update = writableDatabase.update(AGENT_TABLE_NAME, cv,"packageName=?", new String[]{packageName});

    }
    public void updateAllAgent(String agent){
        if(agent==null) agent="";
        //先取出一个查看是否和要更新的agent是否相同
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("select * from " + AGENT_TABLE_NAME + " limit 1", null);
        AgentDbBean oldAgentDbBean=null;
        while (cursor.moveToNext()){
            oldAgentDbBean=new AgentDbBean();
            oldAgentDbBean.setPackageName(cursor.getString(cursor.getColumnIndex(AgentDbBean.PACKAGE_NAME)));
            oldAgentDbBean.setInstallCode(cursor.getString(cursor.getColumnIndex(AgentDbBean.INSTALL_CODE)));
            if(oldAgentDbBean.getInstallCode()==null){
                oldAgentDbBean.setInstallCode("1_0");
            }
            oldAgentDbBean.setAgent(cursor.getString(cursor.getColumnIndex(AgentDbBean.AGENT)));
        }
        if(oldAgentDbBean!=null&&!agent.equals(oldAgentDbBean.getAgent())){//有并且不一样
            ContentValues cv = new ContentValues();
            cv.put(AgentDbBean.AGENT,agent);
            int update = writableDatabase.update(AGENT_TABLE_NAME, cv, null, null);
            L.d("AgentDbDao","更新了所有的agent--> count="+update+"  agent="+agent+" packageName="+oldAgentDbBean.getPackageName());
        }else{
            L.d("AgentDbDao","无需更新--> agent="+agent);
        }
        writableDatabase.close();
    }
    public AgentDbBean getAgengDbBeanByPackageName(String packageName){
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("select * from " + AGENT_TABLE_NAME + " where packageName=?", new String[]{packageName});
        AgentDbBean oldAgentDbBean=null;
        while (cursor.moveToNext()){
            oldAgentDbBean=new AgentDbBean();
            oldAgentDbBean.setPackageName(cursor.getString(cursor.getColumnIndex(AgentDbBean.PACKAGE_NAME)));
            oldAgentDbBean.setInstallCode(cursor.getString(cursor.getColumnIndex(AgentDbBean.INSTALL_CODE)));
            if(oldAgentDbBean.getInstallCode()==null){
                oldAgentDbBean.setInstallCode("1_0");
            }
            oldAgentDbBean.setAgent(cursor.getString(cursor.getColumnIndex(AgentDbBean.AGENT)));
        }
        return oldAgentDbBean;
    }
    public void deleteAgentDbBeanByPackageName(String packageName){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int delete = writableDatabase.delete(AGENT_TABLE_NAME, "packageName=?", new String[]{packageName});
        L.e("AgentDbDao","删除"+packageName+" count="+delete);
    }

    /**
     * 删除所有的数据
     */
    public void deleteAgentDb(){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int delete = writableDatabase.delete(AGENT_TABLE_NAME, null,null);
        L.e("AgentDbDao","删除"+" count="+delete);
    }
}
